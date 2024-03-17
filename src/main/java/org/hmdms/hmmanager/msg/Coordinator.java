package org.hmdms.hmmanager.msg;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.hmdms.hmmanager.sys.HealthC;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.sys.BlockingComponent;
import org.hmdms.hmmanager.utils.LoggingUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Entry point for messages into the system. Receives messages from other components, distributes them among
 * {@link Broker} objects and coordinates answering the messages when the job is finished
 */
public class Coordinator extends BlockingComponent implements Runnable {
    /**
     * Current state of the coordinator
     */
    private StateC state;
    /**
     * List of all instantiated {@link Broker} objects
     */
    private final ArrayList<Broker> brokers;
    /**
     * Index of the next broker to receive a message
     */
    private int nextBroker;
    /**
     * Number of broker objects
     */
    private final int numOfBrokers;
    /**
     * Time in seconds before a message is dropped
     */
    private final int messageTimeout;
    /**
     * Signifies, whether new brokers should be added automatically when the system notices, that current number
     * of brokers is not sufficient.
     */
    private final boolean brokerAutoScaling;
    /**
     * Counter for number of messages, that could not be delivered to the brokers due to some problem.
     */
    private int droppedMessages = 0;
    private final String queueName;
    private final String mqHost;

    /**
     * Standard constructor for Coordinator. Looks up number of brokers to instantiate from config.properties file and instantiates them.
     * @throws IOException When config.properties file is not found
     */
    public Coordinator() throws IOException, TimeoutException {
        super(new String[]{"brokers"});
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        // Get configuration from config.properties
        this.numOfBrokers = Integer.parseInt(prop.getProperty("msg.scaling.brokers"));
        this.messageTimeout = Integer.parseInt(prop.getProperty("msg.timeout"));
        this.brokerAutoScaling = Boolean.parseBoolean(prop.getProperty("msg.scaling.brokers.autoScaling"));
        this.queueName = prop.get("mq.coordinator.name").toString();
        this.mqHost = prop.get("mq.host").toString();

        this.brokers = new ArrayList<>();
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }
        logger.debug("Working with " + this.numOfBrokers + " brokers");

        this.nextBroker = 0;
        this.state = StateC.INITIALIZED;
    }

    private void newMessage(byte[] message) {
        ByteArrayInputStream bi = new ByteArrayInputStream(message);
        try (ObjectInputStream oi = new ObjectInputStream(bi) ) {
            MessageInfo mi = (MessageInfo) oi.readObject();
            oi.close();
            this.newMessage(TopicC.TEST, mi);
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger, "info", "Message could not be deserialized due to an %s: %s");
        }
    }
    /**
     * Adds a new message to the next broker in line
     * @param topic Topic to which the message should be added
     * @param mi Message to be added to topic {@param topic}
     */
    public void newMessage(TopicC topic, MessageInfo mi) {
        if (topic == null || mi == null) throw new IllegalArgumentException("No topic or message info given");

        // Try to add answer to next broker. If it does not work, try the next broker instead
        int tries = 0;
        boolean dropped = false;
        while (!this.brokers.get(this.nextBroker).addMessage(topic, mi) && tries < 2*this.numOfBrokers) {
            this.nextBroker = (this.nextBroker + 1) % this.numOfBrokers;
            tries++;

            // If after 2 iterations of all brokers the message could still not be delivered, mark it as dropped
            // TODO check broker health in coordinator thread and if necessary redeploy them
            if (tries == 2*this.numOfBrokers) {
                dropped = true;
                break;
            }
        }

        // Log that the message has been dropped
        if (dropped) {
            this.droppedMessages++;
            this.logger.info("Message " + mi + " has been dropped due to brokers not being available");
            this.logger.info(this.droppedMessages + " have been dropped so far");
        } else this.logger.trace("Added message info object to broker " + this.nextBroker + ": " + this.brokers.get(this.nextBroker).toString());
        this.nextBroker = (this.nextBroker + 1) % this.numOfBrokers;
    }

    /**
     * Returns all brokers this coordinator coordinates
     * @return All brokers of this coordinator
     */
    public ArrayList<Broker> getBrokers() {
        return this.brokers;
    }

    /**
     * Returns current state of this coordinator
     * @return Current state
     */
    public StateC getState() {
        return this.state;
    }

    /**
     * Sets state of the coordinator. Can be used to stop it from working by setting state to StateC.STOPPED
     * @param state Current working state of the coordinator
     */
    public void setState(StateC state) { this.state = state; }

    /**
     * Runs main logic of the coordinator. Can and should be run in a different thread.
     * Loops through the entire logic over and over again, until the state of the coordinator is set to
     * something else than StateC.WORKING
     */
    @Override
    public void run() {
        if (this.state.equals(StateC.DESTROYED) || this.state.equals(StateC.INITIALIZED)) {
            this.logger.debug(String.format("Cannot start coordinator, coordinator state is %s", this.state));
            return;
        }
        this.state = StateC.WORKING;
        this.logger.debug("Setting up message queue consumer");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.mqHost);

        try (Connection conn = factory.newConnection(); Channel channel = conn.createChannel()) {
            channel.queueDeclare(this.queueName, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                byte[] message = delivery.getBody();
                this.newMessage(message);
            };
            channel.basicConsume(this.queueName, true, deliverCallback, consumerTag -> { });
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "warn",
                    "%s occurred  while trying to setup the message queue: %s"
            );
            return;
        }

        // Loop while the state of the component is still at WORKING
        while (this.state.equals(StateC.WORKING)) {
            try {
                // Iterate through all brokers
                for (Broker b : this.brokers) {
                    // Notify all subscribers of the current broker of new messages
                    b.notifyAllSubscribers();
                    b.collectAnswersFromSubs();
                    HashMap<TopicC, ArrayList<MessageInfo>> answers = b.getAndDeleteAnswers();

                    for (TopicC top : answers.keySet()) {
                        for (MessageInfo mi : answers.get(top)) {
                            this.logger.debug("Answer from broker: " + mi.toString());
                        }
                    }
                    ArrayList<MessageInfo> cleanedMessages = b.cleanup(this.messageTimeout);
                    for (MessageInfo mi : cleanedMessages) {
                        this.logger.info("Cleaned up message: " + mi.toString());
                    }
                }
            } catch (Exception ex) {
                this.logger.info("Exception in run of coordinator: " + ex.getMessage());
                StringBuilder stack = new StringBuilder();
                for (var stel : ex.getStackTrace()) {
                    stack.append(stel.toString());
                    stack.append("\t\n");
                }
                this.logger.debug(stack.toString());
            }
        }

        // When the thread is stopped, destroy all brokers. This will cause them to stop all subscribers
        // and stop all of their threads
        for (Broker br : this.brokers) {
            br.destroy();
        }

        this.logger.debug("Coordinator stopped");
        this.state = StateC.STOPPED;
    }

    /**
     * Instantiates all brokers this coordinator coordinates
     */
    public void setup() {
        this.logger.debug("Setting up coordinator");
        this.logger.debug("Adding brokers");
        this.tryToAcquireLock("brokers");

        // Setup all brokers
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }
        this.unlock("brokers");

        for (Broker br : this.brokers) {
            br.addSubscriber(new TestSubscriber());
        }

        this.state = StateC.STARTED;
        this.logger.debug("Coordinator is fully setup");
    }

    private void checkAndRedeployBrokers() {
        if (!this.tryToAcquireLock("brokers")) return;
        ArrayList<Broker> toDelete = new ArrayList<>();
        // TODO implement health check correctly
        for (Broker b : this.brokers) {
            b.checkOwnHealth();
            if (b.getHealth().compareTo(HealthC.SLOW) >=0) {
                b.destroy();
                b.collectAnswersFromSubs();
                HashMap<TopicC, ArrayList<MessageInfo>> answers = b.getAndDeleteAnswers();
                // TODO cache the answers

                toDelete.add(b);
            }
        }
        this.brokers.removeAll(toDelete);
        for (int i = 0; i < toDelete.size(); i++) {
            Broker newB = new Broker();
            newB.addSubscriber(new TestSubscriber());
            this.brokers.add(newB);
        }
        this.unlock("brokers");
    }
}
