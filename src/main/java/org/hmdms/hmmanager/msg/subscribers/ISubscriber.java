package org.hmdms.hmmanager.msg.subscribers;

import org.hmdms.hmmanager.msg.MessageInfo;
import org.hmdms.hmmanager.msg.TopicC;
import org.hmdms.hmmanager.sys.StateC;

import java.util.ArrayList;

/**
 * Interface that needs to be implemented by all Observers
 */
public interface ISubscriber extends Runnable {
    /**
     * Gives the subscriber all new messages concerning their topic
     * @param mi Messages
     * @return True, if messages could be transmitted
     */
    boolean notify(ArrayList<MessageInfo> mi);

    /**
     * Method used to check for the topic the subscriber subscribes to
     * @return Topic of the subscriber
     */
    TopicC getTopic();

    /**
     * Sets state of the subscriber
     * @param state State of the subscriber
     */
    void setState(StateC state);
}
