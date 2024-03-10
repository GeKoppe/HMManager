package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.core.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Entry point for messages into the system. Receives messages from other components, distributes them among
 * {@link Broker} objects and coordinates answering the messages when the job is finished
 */
@Component
public class Coordinator implements Runnable {
    /**
     * Current state of the coordinator
     */
    private StateC state;
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Coordinator.class);
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
    private final boolean brokerAutoScaling;
    private int droppedMessages = 0;
    private JmsTemplate tpl;

    /**
     * Standard constructor for Coordinator. Looks up number of brokers to instantiate from config.properties file and instantiates them.
     * @throws IOException When config.properties file is not found
     */
    public Coordinator() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        this.numOfBrokers = Integer.parseInt(prop.getProperty("msg.scaling.brokers"));
        this.messageTimeout = Integer.parseInt(prop.getProperty("msg.timeout"));
        this.brokerAutoScaling = Boolean.parseBoolean(prop.getProperty("msg.scaling.brokers.autoScaling"));

        this.brokers = new ArrayList<>();
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }
        logger.debug("Working with " + this.numOfBrokers + " brokers");
        this.state = StateC.INITIALIZED;

        this.nextBroker = 0;
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
            this.logger.debug("Message " + mi + " has been dropped due to brokers not being available");
            this.logger.info(this.droppedMessages + " have been dropped so far");
        } else this.logger.debug("Added message info object to broker " + this.nextBroker + ": " + this.brokers.get(this.nextBroker).toString());
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
        if (!this.state.equals(StateC.STARTED)) return;
        this.state = StateC.WORKING;

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

        // Setup all brokers
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }

        for (Broker br : this.brokers) {
            br.addSubscriber(new TestSubscriber());
        }

        this.state = StateC.STARTED;
        this.logger.debug("Coordinator is fully setup");
    }

    @JmsListener(destination = "coordinator", containerFactory = "hmmanagerFactory")
    public void receiveMessage(JmsMessage mi) {
        this.newMessage(mi.getTopic(), mi.getMi());
    }

    public JmsTemplate getTpl() {
        return tpl;
    }

    public void setTpl(JmsTemplate tpl) {
        this.tpl = tpl;
    }
}
