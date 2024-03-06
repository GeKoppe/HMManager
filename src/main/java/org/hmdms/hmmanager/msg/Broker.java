package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class manages all messages given to the system and gives them to their respective services, which in turn
 * subscribe to this class
 */
public class Broker {
    private final Logger logger = LoggerFactory.getLogger(Broker.class);

    /**
     * All messages, sorted by topic
     */
    private HashMap<TopicC, LinkedList<MessageInfo>> mq;
    /**
     * All observers that subscribe to this broker
     */
    private final ArrayList<ISubscriber> subscribers;
    public Broker () {
        logger.debug("Broker instantiated");
        this.mq = new HashMap<>();
        this.subscribers = new ArrayList<>();
    }

    /**
     * Adds message {@param mi} to the queue for the given topic {@param topic}
     * @param topic Topic to which the message should be added
     * @param mi Message to be added
     */
    public void addMessage(TopicC topic, MessageInfo mi) {
        if (topic == null) throw new IllegalArgumentException("No topic given");
        this.logger.debug("Adding message " + mi + " to message queue");

        // If the topic currently does not exist in the hashmap, add it
        if (this.mq.get(topic) == null || this.mq.get(topic).isEmpty()) {
            this.logger.debug("Topic " + topic + " currently not existent in queue, adding it");
            this.mq.put(topic, new LinkedList<>());
        }

        // Add message to the list in the message queue
        this.mq.get(topic).add(mi);
        this.logger.debug("MessageInfo object " + mi + " added to queue for topic " + topic);
    }

    /**
     * Notifies all observer of the given topic and gives them any uncollected messages for the given topic
     * @param topic Topic for which the observers should be notified
     */
    public void notifyObservers(TopicC topic) {
        if (this.mq.get(topic) == null) {
            this.logger.debug("Topic " + topic.name() + " does not currently exist in message queue");
            return;
        }
        // Iterate through all observers
        for (var o : this.subscribers) {
            // If observer observes given topic, give them messages concerning the topic
            if (o.notify(topic)) {
                try {
                    ArrayList<MessageInfo> mis = this.getUncollectedMessages(topic);
                    o.giveMessages(mis);
                    for (var m : mis) {
                        m.setCollected(true);
                        m.setCollectionDate(new Date());
                    }
                    this.logger.debug("Notified observers of new message");
                } catch (Exception ex) {
                    this.logger.debug("Exception occurred while giving observer messages: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Method to notify all observers of all topics to get any not yet collected messages out
     */
    public void notifyAllObservers() {
        for (var topic : this.mq.keySet()) {
            for (var o : this.subscribers) {
                // If observer observes given topic, give them messages concerning the topic
                if (o.notify(topic)) {
                    try {
                        ArrayList<MessageInfo> mis = this.getUncollectedMessages(topic);
                        o.giveMessages(mis);
                        for (var m : mis) {
                            m.setCollected(true);
                            m.setCollectionDate(new Date());
                        }
                        this.logger.debug("Notified observers of new message");
                    } catch (Exception ex) {
                        this.logger.debug("Exception occurred while giving observer messages: " + ex.getMessage());
                    }
                }

            }
        }
    }

    /**
     * Goes through the entire queue for the given topic and returns all messages in that queue, which aren't collected yet.
     * @param topic Topic for which the queue should be analysed
     * @return All uncollected messages of that topic
     */
    private ArrayList<MessageInfo> getUncollectedMessages(TopicC topic) {
        ArrayList<MessageInfo> msg = new ArrayList<>();
        for (var msgInf : this.mq.get(topic)) {
            if (!msgInf.isCollected()) msg.add(msgInf);
        }
        return msg;
    }

    /**
     * Adds an observer that subscribes to this broker
     * @param o Observer object
     */
    public void addObserver(ISubscriber o) {
        this.subscribers.add(o);
        this.logger.debug("Added observer " + o.toString());
    }

    public ArrayList<MessageInfo> cleanup(int timeoutSeconds) {

        return new ArrayList<MessageInfo>();
    }

}
