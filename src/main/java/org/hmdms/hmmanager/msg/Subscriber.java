package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.sys.BlockingComponent;
import org.hmdms.hmmanager.sys.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Base Class for all message subscribers
 */
public abstract class Subscriber extends BlockingComponent implements ISubscriber {
    /**
     * Logger
     */
    protected final Logger logger;
    /**
     * All currentMessages this subscriber is working on
     */
    protected final ArrayList<MessageInfo> currentMessages;
    /**
     * Answers to the messages in the {@link Subscriber#currentMessages} list
     */
    protected final ArrayList<MessageInfo> answers;
    /**
     * Topic the Subscriber subscribes to
     */
    protected TopicC topic;

    /**
     * Default constructor
     * @param lockIds Ids for locks the {@link BlockingComponent} should instantiate
     */
    public Subscriber(String[] lockIds) {
        super(lockIds);
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.currentMessages = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.state = StateC.INITIALIZED;
    }

    /**
     * Returns the topic the subscriber subscribes to
     * @return topic the subscriber subscribes to
     */
    @Override
    public TopicC getTopic() {
        return this.topic;
    }

    /**
     * Sets the topic the subscriber subscribes to
     * @param topic topic the subscriber subscribes to
     */
    public void setTopic(TopicC topic) {
        this.topic = topic;
    }
}
