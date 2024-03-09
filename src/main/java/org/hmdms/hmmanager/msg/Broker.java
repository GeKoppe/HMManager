package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.core.BlockingComponent;
import org.hmdms.hmmanager.core.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class manages all messages given to the system and gives them to their respective services, which in turn
 * subscribe to this class
 */
@BlockingComponent
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
    private final HashMap<TopicC, ArrayList<MessageInfo>> answers;
    /**
     * Currently not used
     */
    @Deprecated
    private final HashMap<TopicC, LinkedList<MessageInfo>> cache;
    /**
     * Threads of all subscribers to this broker
     */
    private final LinkedList<Thread> subThreads;
    /**
     * Current State of the broker
     */
    private StateC state;

    /**
     * Default constructor
     */
    public Broker () {
        this.mq = new HashMap<>();
        this.subscribers = new ArrayList<>();
        this.state = StateC.STARTED;
        this.answers = new HashMap<>();
        this.cache = new HashMap<>();
        this.subThreads = new LinkedList<>();
        this.logger.debug("Broker instantiated");
    }

    /**
     * Adds message {@param mi} to the queue for the given topic {@param topic}
     * @param topic Topic to which the message should be added
     * @param mi Message to be added
     */
    public boolean addMessage(TopicC topic, MessageInfo mi) {
        try {
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
            return true;
        } catch (Exception ex) {
            this.logger.info("Could not add message due to exception: " + ex.getMessage());
            StringBuilder sb = new StringBuilder();
            for (var stel : ex.getStackTrace()) {
                sb.append(stel.toString());
                sb.append("\n");
                sb.append("\t");
            }
            this.logger.debug(sb.toString());
            return false;
        }
    }

    /**
     * Method to notify all subscribers of all topics to get any not yet collected messages out
     */
    public boolean notifyAllSubscribers() {
        try {
            Set<TopicC> keySet = this.mq.keySet();
            for (TopicC topic : keySet) {
                ArrayList<MessageInfo> mis = new ArrayList<>();

                if (this.mq.get(topic) == null || this.mq.get(topic).isEmpty()) {
                    continue;
                }
                for (ISubscriber sub : this.subscribers) {
                    // If subscriber subscribes to given topic, give them messages concerning the topic
                    if (sub.getTopic().equals(topic)) {
                        try {
                            // Get all collected messages and give them to the subscriber
                            LinkedList<MessageInfo> uncollected = this.mq.get(topic);
                            for (MessageInfo next : uncollected) {
                                if (!next.isCollected()) mis.add(next);
                            }
                            sub.notify(mis);
                            this.logger.trace("Notified subscribers of new message");
                        } catch (Exception ex) {
                            this.logger.debug("Exception occurred while giving subscribers messages: " + ex.getMessage());
                            StringBuilder stack = new StringBuilder();
                            for (var stel : ex.getStackTrace()) {
                                stack.append(stel.toString());
                                stack.append("\t\n");
                            }
                            this.logger.debug(stack.toString());
                        }
                    }
                }

                // Mark all transferred messages as collected
                for (var m : mis) {
                    for (int i = 0; i < this.mq.get(topic).size(); i++) {
                        if (m.getUuid().equals(this.mq.get(topic).get(i).getUuid())) {
                            this.mq.get(topic).get(i).setCollected(true);
                            this.mq.get(topic).get(i).setCollectionDate(new Date());
                        }
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            this.logger.info("Could notify subscribers due to exception: " + ex.getMessage());
            StringBuilder sb = new StringBuilder();
            for (var stel : ex.getStackTrace()) {
                sb.append(stel.toString());
                sb.append("\n");
                sb.append("\t");
            }
            this.logger.debug(sb.toString());
            return false;
        }

    }

    /**
     * Goes through the entire queue for the given topic and returns all messages in that queue, which aren't collected yet.
     * @param mis MessageInfo Object
     * @return All uncollected messages of that topic
     */
    private ArrayList<MessageInfo> getUncollectedMessages(LinkedList<MessageInfo> mis) {
        ArrayList<MessageInfo> msg = new ArrayList<>();
        for (MessageInfo next : mis) {
            if (!next.isCollected()) msg.add(next);
        }
        return msg;
    }

    /**
     * Adds a subscriber that subscribes to this broker
     * @param s subscriber object
     */
    public void addSubscriber(ISubscriber s) {
        this.subscribers.add(s);
        this.logger.debug("Added subscriber " + s.toString());

        this.subThreads.add(new Thread(s));
        this.subThreads.getLast().start();
    }

    /**
     * Cleans all messages that are older than {@param timeoutSeconds} seconds.
     * @param timeoutSeconds Time in seconds after which messages are to be removed from the message queue
     * @return All cleaned messages, because the coordinator still has to answer them
     */
    public ArrayList<MessageInfo> cleanup(int timeoutSeconds) {

        try {
            ArrayList<MessageInfo> cleaned = new ArrayList<>();
            // Iterate through all topics
            for (TopicC t : this.mq.keySet()) {
                ArrayList<MessageInfo> tCleaned = new ArrayList<>();
                LinkedList<MessageInfo> currentQueue = this.mq.get(t);
                // Iterate through all messages in that topic
                for (MessageInfo m : currentQueue) {
                    // If message was already collected, check the collection date
                    if (m.isCollected()) {
                        Date collectionDate = m.getCollectionDate();
                        if (collectionDate == null) {
                            continue;
                        }

                        // If collection date is longer ago than timeoutSeconds, remove the message from the queue
                        if (((int) (new Date().getTime() - collectionDate.getTime()) / 1000) > timeoutSeconds) {
                            this.logger.info("Message " + m + " is being cleaned up as it has not finished after timeout");
                            tCleaned.add(m);
                        }
                    } else if (((int) (new Date().getTime() - m.getReceived().getTime()) / 1000) > timeoutSeconds) {
                        // If the message has not been collected, check the received time instead
                        this.logger.info("Message " + m + " is being cleaned up as it has not finished after timeout");
                        tCleaned.add(m);
                    }
                }
                this.mq.get(t).removeAll(tCleaned);
                cleaned.addAll(tCleaned);
            }

            // Return all cleaned messages to the coordinator so it can answer
            return cleaned;
        } catch (Exception ex) {
            this.logger.info("Could notify subscribers due to exception: " + ex.getMessage());
            StringBuilder sb = new StringBuilder();
            for (var stel : ex.getStackTrace()) {
                sb.append(stel.toString());
                sb.append("\n");
                sb.append("\t");
            }
            this.logger.debug(sb.toString());
            return null;
        }
    }


    /**
     * Checks all subscribers for answers they compiled since last check. Adds those answers to
     * the internal answers list of the broker
     */
    public boolean collectAnswersFromSubs() {
        try {
            for (ISubscriber sub : this.subscribers) {
                ArrayList<MessageInfo> sCollected = new ArrayList<>();
                for (MessageInfo mi : sub.getAnswers()) {
                    if (this.answers.get(sub.getTopic()) == null) {
                        this.logger.debug("Topic " + sub.getTopic() + " currently does not exist in answers, adding it");
                        this.answers.put(sub.getTopic(), new ArrayList<>());
                    }
                    this.answers.get(sub.getTopic()).add(mi);

                    sCollected.add(mi);
                }
                for (MessageInfo mi : sCollected) {
                    sub.removeAnswer(mi);
                }
            }

            for (TopicC top : this.answers.keySet()) {
                ArrayList<MessageInfo> toRemove = new ArrayList<>();
                for (MessageInfo mi : this.answers.get(top)) {
                    // TODO transmit answer to client
                    for (MessageInfo info : this.mq.get(top)) {
                        if (info.getUuid().equals(mi.getUuid())) {
                            toRemove.add(info);
                        }
                    }
                }
                this.answers.get(top).removeAll(toRemove);
            }

            return true;
        } catch (Exception ex) {
            this.logger.info("Could notify subscribers due to exception: " + ex.getMessage());
            StringBuilder sb = new StringBuilder();
            for (var stel : ex.getStackTrace()) {
                sb.append(stel.toString());
                sb.append("\n");
                sb.append("\t");
            }
            this.logger.debug(sb.toString());
            return false;
        }
    }

    public void destroy() {
        for (ISubscriber sub : this.subscribers) {
            sub.setState(StateC.STOPPED);
        }
        for (Thread t : this.subThreads) {
            t.interrupt();
        }
        this.state = StateC.DESTROYED;
    }
}
