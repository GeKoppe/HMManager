package org.hmdms.hmmanager.msg;

import java.util.ArrayList;

/**
 * Interface that needs to be implemented by all Observers
 */
public interface ISubscriber {

    /**
     * Method is called when
     * @param topic
     * @return
     */
    public boolean notify(TopicC topic);
    public boolean getState(String id);

    /**
     * Message for handling all new messages
     * @param mi List of all message objects
     */
    public void giveMessages(ArrayList<MessageInfo> mi);
}
