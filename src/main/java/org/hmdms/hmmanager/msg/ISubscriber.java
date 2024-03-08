package org.hmdms.hmmanager.msg;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Interface that needs to be implemented by all Observers
 */
public interface ISubscriber extends Runnable {

    /**
     * Method is called when
     * @param mi
     * @return
     */
    void notify(ArrayList<MessageInfo> mi);
    TopicC getTopic();

    StateC getState();

    ArrayList<MessageInfo> getAnswers();
}
