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

    private StateC state;
    public Broker () {
        logger.debug("Broker instantiated");
        this.mq = new HashMap<>();
        this.subscribers = new ArrayList<>();
        this.state = StateC.STARTED;
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
    public void notifySubscriber(TopicC topic) {
        this.state = StateC.WORKING;
        if (this.mq.get(topic) == null) {
            this.logger.debug("Topic " + topic.name() + " does not currently exist in message queue");
            return;
        }
        // Iterate through all observers
        for (ISubscriber o : this.subscribers) {
            // If observer observes given topic, give them messages concerning the topic
            if (o.getTopic().equals(topic) && o.getState().equals(StateC.STARTED)) {
                try {
                    ArrayList<MessageInfo> mis = this.getUncollectedMessages(topic);
                    o.notify(mis);
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
        this.state = StateC.STARTED;
    }

    /**
     * Method to notify all observers of all topics to get any not yet collected messages out
     */
    public void notifyAllSubscribers() {
        for (TopicC topic : this.mq.keySet()) {
            for (ISubscriber o : this.subscribers) {
                // If subscriber subscribes to given topic, give them messages concerning the topic
                if (o.getTopic().equals(topic)) {
                    try {
                        ArrayList<MessageInfo> mis = this.getUncollectedMessages(topic);
                        o.notify(mis);
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

    /**
     * Cleans all messages that are older than {@param timeoutSeconds} seconds.
     * @param timeoutSeconds Time in seconds after which messages are to be removed from the message queue
     * @return All cleaned messages, because the coordinator still has to answer them
     */
    public ArrayList<MessageInfo> cleanup(int timeoutSeconds) {
        ArrayList<MessageInfo> cleaned = new ArrayList<>();

        // Iterate through all topics
        for (TopicC t : this.mq.keySet()) {
            // Iterate through all messages in that topic
            for (MessageInfo m : this.mq.get(t)) {
                // If message is older than timeoutSeconds, remove it from message queue and add it to the cleaned messages
                if (((int) (new Date().getTime() - m.getReceived().getTime()) / 1000) > timeoutSeconds) {
                    this.logger.info("Message " + m.toString() + " is being cleaned up as it has not finished after timeout");
                    cleaned.add(m);
                    this.mq.get(t).remove(m);
                }
            }
        }

        // Return all cleaned messages to the coordinator so it can answer
        return cleaned;
    }

}
