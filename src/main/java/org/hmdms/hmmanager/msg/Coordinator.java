package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Entry point for messages into the system. Receives messages from other components, distributes them among
 * {@link Broker} objects and coordinates answering the messages when the job is finished
 */
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
        this.brokers.get(this.nextBroker).addMessage(topic, mi);
        this.logger.debug("Added message info object to broker " + this.nextBroker + ": " + this.brokers.get(this.nextBroker).toString());
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
     * @param state
     */
    public void setState(StateC state) { this.state = state; }

    /**
     * Runs main logic of the coordinator. Can and should be run in a different thread
     */
    @Override
    public void run() {
        if (!this.state.equals(StateC.STARTED)) return;
        this.state = StateC.WORKING;
        while (this.state.equals(StateC.WORKING)) {
            for (Broker b : this.brokers) {
                b.notifyAllSubscribers();
                b.collectAnswersFromSubs();
                ArrayList<MessageInfo> cleanedMessages = b.cleanup(this.messageTimeout);
            }
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
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }


        for (Broker b : this.brokers) {
            b.addSubscriber(new TestSubscriber());
        }
        this.state = StateC.STARTED;
        this.logger.debug("Coordinator is fully setup");
    }
}
