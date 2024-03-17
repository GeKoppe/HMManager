package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.sys.HealthC;
import org.hmdms.hmmanager.sys.PerformanceCheck;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.sys.BlockingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class manages all messages given to the system and gives them to their respective services, which in turn
 * subscribe to this class
 */
public class Broker extends BlockingComponent {
    /**
     * All messages, sorted by topic
     */
    private final HashMap<TopicC, LinkedList<MessageInfo>> mq;
    /**
     * All subscribers that subscribe to this broker
     */
    private final ArrayList<ISubscriber> subscribers;

    /**
     * Threads of all subscribers to this broker
     */
    private final LinkedList<Thread> subThreads;


    /**
     * Default constructor
     * @throws IllegalArgumentException Exception thrown by super constructor
     */
    public Broker () throws IllegalArgumentException {
        super(new String[]{"mq", "sub", "thread"});
        this.mq = new HashMap<>();
        this.subscribers = new ArrayList<>();
        this.setState(StateC.STARTED);
        this.subThreads = new LinkedList<>();
        this.logger.debug("Broker instantiated");
    }

    /**
     * Adds message {@param mi} to the queue for the given topic {@param topic}
     * @param topic Topic to which the message should be added
     * @param mi Message to be added
     */
    public boolean addMessage(TopicC topic, MessageInfo mi) {
        if (topic == null || mi == null) throw new IllegalArgumentException("No topic or message given");
        try {
            if (this.notifySingle(topic, mi)) {
                this.logger.debug("Notified subscriber");
                return true;
            }
        } catch (Exception ex) {
            this.logger.info("Could not add message due to exception: " + ex.getMessage() + ". Adding it to queue instead");
            StringBuilder sb = new StringBuilder();
            for (var stel : ex.getStackTrace()) {
                sb.append(stel.toString());
                sb.append("\n");
                sb.append("\t");
            }
            this.logger.debug(sb.toString());
        }
        try {
            if (!this.tryToAcquireLock("mq")) return false;
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
        } finally {
            this.unlock("mq");
        }
    }

    private boolean notifySingle(TopicC topic, MessageInfo mi) {
        if (topic == null || mi == null) throw new IllegalArgumentException("No topic or message given");
        if (!this.tryToAcquireLock("sub")) return false;

        boolean transferred = false;
        for (ISubscriber sub : this.subscribers) {
            if (sub.getTopic().equals(topic)) {
                try {
                    ArrayList<MessageInfo> mis = new ArrayList<>();
                    mis.add(mi);
                    // Get all collected messages and give them to the subscriber
                    transferred = sub.notify(mis);
                    if (transferred) {
                        this.logger.debug("Notified subscriber of new message");
                        break;
                    }
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
        this.unlock("sub");
        return transferred;
    }

    /**
     * Method to notify all subscribers of all topics to get any not yet collected messages out
     */
    public boolean notifyAllSubscribers() {
        if (!this.tryToAcquireLock("mq")) return false;
        try {
            Set<TopicC> keySet = this.mq.keySet();
            for (TopicC topic : keySet) {
                ArrayList<MessageInfo> mis = new ArrayList<>();
                boolean transferred = false;

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
                            transferred = sub.notify(mis);
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
                if (transferred) {
                    for (var m : mis) {
                        for (int i = 0; i < this.mq.get(topic).size(); i++) {
                            if (m.getUuid().equals(this.mq.get(topic).get(i).getUuid())) {
                                this.mq.get(topic).get(i).setCollected(true);
                                this.mq.get(topic).get(i).setCollectionDate(new Date());
                            }
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
        } finally {
            this.unlock("mq");
        }

    }
    /**
     * Adds a subscriber that subscribes to this broker
     * @param s subscriber object
     */
    public boolean addSubscriber(ISubscriber s) {
        if (!this.tryToAcquireLock("sub")) {
            this.logger.info("Could not lock subscribers object and therefore cannot add subscriber " + s.toString() + ", returning");
            this.unlock("sub");
            return false;
        }
        this.subscribers.add(s);
        this.logger.debug("Added subscriber " + s.toString());

        if (!this.tryToAcquireLock("thread")) {
            this.logger.info("Could not lock threads object and therefore cannot add subscriber " + s.toString() + ", returning");
            this.unlock("thread");
            this.subscribers.remove(s);
            this.unlock("sub");
            return false;
        }
        this.subThreads.add(new Thread(s));
        this.subThreads.getLast().start();
        this.logger.debug("Successfully added subscriber " + s + " and started their thread");
        this.unlock("sub");
        this.unlock("thread");
        return true;
    }

    /**
     * Cleans all messages that are older than {@param timeoutSeconds} seconds.
     * @param timeoutSeconds Time in seconds after which messages are to be removed from the message queue
     * @return All cleaned messages, because the coordinator still has to answer them
     */
    public ArrayList<MessageInfo> cleanup(int timeoutSeconds) {
        try {
            if (!this.tryToAcquireLock("mq")) return null;
            ArrayList<MessageInfo> cleaned = new ArrayList<>();
            // Iterate through all topics
            for (TopicC t : this.mq.keySet()) {
                ArrayList<MessageInfo> tCleaned = new ArrayList<>();
                ArrayList<MessageInfo> aCleaned = new ArrayList<>();
                LinkedList<MessageInfo> currentQueue = this.mq.get(t);
                // Iterate through all messages in that topic
                for (MessageInfo m : currentQueue) {
                    // If message was already collected, check the collection date
                    if (m.isCollected()) {
                        /* TODO think about how to monitor which messages, have been collected, were not answered yet
                            and what to do with those
                         */
                        aCleaned.add(m);
                    } else if (((int) (new Date().getTime() - m.getReceived().getTime()) / 1000) > timeoutSeconds) {
                        // If the message has not been collected, check the received time instead
                        this.logger.info("Message " + m + " is being cleaned up as it has not finished after timeout");
                        tCleaned.add(m);
                    }
                }
                this.mq.get(t).removeAll(tCleaned);
                cleaned.addAll(tCleaned);
                this.mq.get(t).removeAll(aCleaned);
            }

            // Return all cleaned messages to the coordinator, so it can answer
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
        } finally {
            this.unlock("mq");
        }
    }

    /**
     * Sets stopped status on all subscribers and then waits for their thread to end.
     * If the thread does not end within 200ms, the thread will be interrupted
     */
    public void destroy() {
        // Set stopped state on all subscribers so their thread comes to a stop
        for (ISubscriber sub : this.subscribers) {
            sub.setState(StateC.STOPPED);
        }

        // Wait for all threads to finish. If
        for (Thread t : this.subThreads) {
            try {
                // Try to join the thread. If the thread doesn't conclude within 200ms, interrupt it
                this.logger.debug("Waiting for thread " + t + " to end");
                t.join(200);
                if (t.isAlive()) {
                    this.logger.debug("Thread " +  t + " still alive, setting interrupt");
                    t.interrupt();
                }
            } catch (InterruptedException ex) {
                this.logger.debug("Thread " + t + " was interrupted unexpectedly while trying to join it: " + ex.getMessage());
                StringBuilder sb = new StringBuilder();
                for (var stel : ex.getStackTrace()) {
                    sb.append("\n");
                    sb.append("\t");
                    sb.append(stel.toString());
                }
                this.logger.debug(sb.toString());
                this.logger.debug("Setting interrupt on thread " + t + " to make sure it is dead");
                t.interrupt();
            }
        }
        this.setState(StateC.DESTROYED);
    }

    /**
     * Iterates through all Performance Checkers in {@link org.hmdms.hmmanager.sys.Component#performance} and determines
     * whether
     */
    public void checkOwnHealth() {
        HealthC base = this.health;
        for (String key : this.performance.keySet()) {
            PerformanceCheck p = this.performance.get(key);

            float performanceIndicator = (float) (p.getAverageOperationTime() / p.getBaselineTime());
            if (performanceIndicator > 4F) {
                base = HealthC.TROUBLED.compareTo(base) > 0 ? HealthC.TROUBLED : base;
            } else if (performanceIndicator > 2F) {
                base = HealthC.SLOW.compareTo(base) > 0 ? HealthC.SLOW : base;
            } else if (performanceIndicator >= 1F) {
                base = HealthC.HEALTHY.compareTo(base) >= 0 ? HealthC.HEALTHY : base;
            } else {
                base = HealthC.OVERACHIEVING;
            }
        }
        this.health = base;
    }
}
