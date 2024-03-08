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
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Broker.class);

    /**
     * All messages, sorted by topic
     */
    private final HashMap<TopicC, LinkedList<MessageInfo>> mq;
    /**
     * All subscribers that subscribe to this broker
     */
    private final ArrayList<ISubscriber> subscribers;
    /**
     * Answers collected from subscribers
     */
    private final ArrayList<MessageInfo> answers;

    private StateC state;
    public Broker () {
        logger.debug("Broker instantiated");
        this.mq = new HashMap<>();
        this.subscribers = new ArrayList<>();
        this.state = StateC.STARTED;
        this.answers = new ArrayList<>();
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
     * Notifies all subscribers of the given topic and gives them any uncollected messages for the given topic
     * @param topic Topic for which the subscribers should be notified
     */
    public void notifySubscriber(TopicC topic) {
        this.state = StateC.WORKING;
        if (this.mq.get(topic) == null) {
            this.logger.debug("Topic " + topic.name() + " does not currently exist in message queue");
            return;
        }
        // Iterate through all subscribers
        for (ISubscriber o : this.subscribers) {
            // If subscriber subscribes given topic, give them messages concerning the topic
            if (o.getTopic().equals(topic) && o.getState().equals(StateC.STARTED)) {
                try {
                    ArrayList<MessageInfo> mis = this.getUncollectedMessages(topic);
                    o.notify(mis);
                    for (var m : mis) {
                        m.setCollected(true);
                        m.setCollectionDate(new Date());
                    }
                    this.logger.trace("Notified subscribers of new message");
                } catch (Exception ex) {
                    this.logger.info("Exception occurred while giving subscribers messages: " + ex.getMessage());
                }
            }
        }
        this.state = StateC.STARTED;
    }

    /**
     * Method to notify all subscribers of all topics to get any not yet collected messages out
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
                        this.logger.trace("Notified subscribers of new message");
                    } catch (Exception ex) {
                        this.logger.debug("Exception occurred while giving subscribers messages: " + ex.getMessage());
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
     * Adds an subscriber that subscribes to this broker
     * @param s subscriber object
     */
    public void addSubscriber(ISubscriber s) {
        this.subscribers.add(s);
        this.logger.debug("Added subscriber " + s.toString());
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
                // If message was already collected, check the collection date
                if (m.isCollected()) {
                    Date collectionDate = m.getCollectionDate();
                    if (collectionDate == null) {
                        m.setCollectionDate(new Date());
                        continue;
                    }

                    // If collection date is longer ago than timeoutSeconds, remove the message from the queue
                    if (((int) (new Date().getTime() - collectionDate.getTime()) / 1000) > timeoutSeconds) {
                        this.logger.info("Message " + m + " is being cleaned up as it has not finished after timeout");
                        cleaned.add(m);
                        this.mq.get(t).remove(m);
                    }
                } else if (((int) (new Date().getTime() - m.getReceived().getTime()) / 1000) > timeoutSeconds) {
                    // If the message has not been collected, check the received time instead
                    this.logger.info("Message " + m + " is being cleaned up as it has not finished after timeout");
                    cleaned.add(m);
                    this.mq.get(t).remove(m);
                }
            }
        }

        // Return all cleaned messages to the coordinator so it can answer
        return cleaned;
    }


    /**
     * Checks all subscribers for answers they compiled since last check. Adds those answers to
     * the internal answers list of the broker
     */
    public void collectAnswersFromSubs() {
        for (ISubscriber sub : this.subscribers) {
            this.answers.addAll(sub.getAnswers());
        }
    }
}
