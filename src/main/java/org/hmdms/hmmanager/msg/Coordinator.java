package org.hmdms.hmmanager.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.hmdms.hmmanager.msg.subscribers.Subscriber;
import org.hmdms.hmmanager.sys.HealthC;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.sys.BlockingComponent;
import org.hmdms.hmmanager.utils.ClassFinder;
import org.hmdms.hmmanager.utils.LoggingUtils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

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
    /**
     * Name of the message queue which the coordinator subscribes to.
     * Name of the queue is defined in config.properties file in property mq.hmmanager.queue.name
     */
    private final String queueName;
    /**
     * Host on which the rabbitmq service is active
     */
    private final String mqHost;
    /**
     * Factory for creating connections to the rabbitmq service
     */
    private final ConnectionFactory factory = new ConnectionFactory();

    /**
     * Standard constructor for Coordinator. Looks up number of brokers to instantiate from config.properties file and instantiates them.
     * @throws IOException When config.properties file is not found
     */
    public Coordinator() throws IOException {
        super(new String[]{"brokers"});
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        // Get configuration from config.properties
        this.numOfBrokers = Integer.parseInt(prop.getProperty("msg.scaling.brokers"));
        this.messageTimeout = Integer.parseInt(prop.getProperty("msg.timeout"));
        this.brokerAutoScaling = Boolean.parseBoolean(prop.getProperty("msg.scaling.brokers.autoScaling"));
        this.queueName = prop.get("mq.hmmanager.queue.name").toString();
        this.mqHost = prop.get("mq.host").toString();

        this.brokers = new ArrayList<>();
        logger.debug("Working with " + this.numOfBrokers + " brokers");

        this.factory.setHost(this.mqHost);

        this.nextBroker = 0;
        this.state = StateC.INITIALIZED;
    }

    /**
     * Is called, whenever a new message from the queue should be added to a broker.
     * Converts {@param message} into a {@link JsonNode} and adds it to the next free broker.
     * @param message Message the broker should receive
     * @param props AMQP Message props
     * @throws IllegalArgumentException When the message has no topic or message property
     * @throws JsonProcessingException When {@param message} could not be deserialized into a {@link JsonNode}
     */
    private void newMessage(String message, BasicProperties props)
            throws IllegalArgumentException, JsonProcessingException {
        this.logger.debug("Parsing json message into MessageInfo object");
        try {
            JsonNode node = new ObjectMapper().readTree(message);
            if (!node.has("topic")) {
                this.logger.info("Incomplete message received, no topic given");
                throw new IllegalArgumentException("No topic given in json message");
            }
            if (!node.has("message")) {
                this.logger.info("Incomplete message received, no message body given");
                throw new IllegalArgumentException("No message body given in json message");
            }

            TopicC mTopic = TopicC.valueOf(node.get("topic").asText());

            MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
            mi.setJsonMessage(node.get("message").toString());
            mi.setMessageProps(props);
            this.logger.debug("Created MessageInfo object for executing task");

            this.newMessage(mTopic, mi);
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
     * something else than {@link StateC#WORKING}
     */
    @Override
    public void run() {
        this.setup();
        if (this.state.equals(StateC.DESTROYED) || this.state.equals(StateC.INITIALIZED)) {
            this.logger.debug(String.format("Cannot start coordinator, coordinator state is %s", this.state));
            return;
        }
        this.state = StateC.WORKING;
        this.logger.debug("Setting up message queue consumer");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.mqHost);
        Connection conn = null;
        Channel channel = null;
        // Set up consumer
        try {
            conn = factory.newConnection();
            channel = conn.createChannel();
            channel.queueDeclare(this.queueName, false, false, false, null);
            Channel finalChannel = channel;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                this.logger.debug("Received new message, start handling");
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                this.newMessage(message, delivery.getProperties());
                finalChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                this.logger.debug("Acknowledged message");
            };
            channel.basicConsume(this.queueName, false, deliverCallback, (consumerTag -> { }));
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "warn",
                    "%s occurred while trying to setup the message queue consumer: %s"
            );
        }

        // Loop while the state of the component is still at WORKING
        while (this.state.equals(StateC.WORKING)) {
            try {
                // Iterate through all brokers
                for (Broker b : this.brokers) {
                    // Notify all subscribers of the current broker of new messages
                    b.notifyAllSubscribers();

                    // Get all messages that weren't distributed yet
                    ArrayList<MessageInfo> cleanedMessages = b.cleanup(this.messageTimeout);

                    // Respond to all messages that weren't yet distributed
                    for (MessageInfo mi : cleanedMessages) {
                        this.logger.warn(String.format("Message %s was not distributed to a subscriber yet and was therefore cleaned", mi.toString()));
                        try (Connection connection = this.factory.newConnection(); Channel chann = connection.createChannel()) {
                            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                                            .Builder()
                                            .correlationId(mi.getMessageProps().getCorrelationId())
                                            .build();

                            // TODO build the answer object
                            chann.basicPublish("", mi.getMessageProps().getReplyTo(), replyProps, "fail".getBytes(StandardCharsets.UTF_8));
                        } catch (Exception ex) {
                            LoggingUtils.logException(ex, this.logger, "warn");
                        }
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
        try {
            if (channel != null) channel.close();
            if (conn != null) conn.close();
        } catch (Exception ex) {

        }
        this.logger.debug("Coordinator stopped");
        this.state = StateC.STOPPED;
    }

    /**
     * Instantiates all brokers this coordinator coordinates
     */
    public void setup(){
        this.logger.debug("Setting up coordinator");
        this.logger.debug("Adding brokers");
        this.tryToAcquireLock("brokers");

        // Setup all brokers
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }
        this.unlock("brokers");

        Set<Class<? extends Subscriber>> subClasses = ClassFinder.findMessageSubscribers();
        ArrayList<Constructor<?>> subPrototypes = new ArrayList<>();

        for (var clazz : subClasses) {
            var constructors = clazz.getConstructors();
            for (Constructor<?> constr : constructors) {
                var params = constr.getParameterTypes();
                if (params.length == 1) {
                    subPrototypes.add(constr);
                }
            }
        }
        for (Broker br : this.brokers) {
            for (Constructor<?> constr : subPrototypes) {
                try {
                    br.addSubscriber((Subscriber) constr.newInstance(this.factory));
                } catch (Exception ex) {
                    LoggingUtils.logException(
                            ex,
                            this.logger,
                            "warn",
                            "%s occurred while trying to instantiate subscriber prototypes: %s"
                    );
                }
            }
        }

        this.state = StateC.STARTED;
        this.logger.debug("Coordinator is fully setup");
    }

    /**
     * NOT YET IMPLEMENTED FULLY
     */
    private void checkAndRedeployBrokers() {
        if (!this.tryToAcquireLock("brokers")) return;
        ArrayList<Broker> toDelete = new ArrayList<>();
        // TODO implement health check correctly
        for (Broker b : this.brokers) {
            b.checkOwnHealth();
            if (b.getHealth().compareTo(HealthC.SLOW) >=0) {
                b.destroy();
                toDelete.add(b);
            }
        }
        this.brokers.removeAll(toDelete);
        for (int i = 0; i < toDelete.size(); i++) {
            Broker newB = new Broker();
            this.brokers.add(newB);
        }
        this.unlock("brokers");
    }
}
