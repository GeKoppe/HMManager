package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 */
public class Coordinator {
    private State state;
    private final Logger logger = LoggerFactory.getLogger(Coordinator.class);
    private ArrayList<Broker> brokers;
    private int nextBroker;
    private int numOfBrokers;

    /**
     * Standard constructor for Coordinator. Looks up number of brokers to instantiate from config.properties file and instantiates them.
     * @throws IOException
     */
    public Coordinator() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        this.numOfBrokers = Integer.parseInt(prop.getProperty("msg.scaling.brokers"));

        this.brokers = new ArrayList<>();
        for (int i = 0; i < this.numOfBrokers; i++) {
            this.brokers.add(new Broker());
        }
        logger.debug("Working with " + this.numOfBrokers + " brokers");
        this.state = State.INITIALIZED;

        this.nextBroker = 0;
    }

    public void newMessage(String topic, MessageInfo mi) {
        this.brokers.get(this.nextBroker).addMessage(topic, mi);
        this.nextBroker = (this.nextBroker + 1) % this.numOfBrokers;
    }

    public ArrayList<Broker> getBrokers() {
        return this.brokers;
    }

    public State getState() {
        return this.state;
    }
}
