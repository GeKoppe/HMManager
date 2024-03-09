package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.core.StateC;

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
     * Method used to check current State of the subscriber (is it working on something else, did it die etc.)
     * @return State of the subscriber
     */
    StateC getState();

    /**
     * Method used to collect all answers from the subscriber
     * @return Answers the subscriber compiled
     */
    ArrayList<MessageInfo> getAnswers();

    void setState(StateC state);

    boolean removeAnswer(MessageInfo mi);
}
