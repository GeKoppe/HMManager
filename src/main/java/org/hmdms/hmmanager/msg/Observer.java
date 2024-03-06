package org.hmdms.hmmanager.msg;

import java.util.ArrayList;

public interface Observer {

    public boolean notify(String topic);
    public boolean getState(String id);
    public void giveMessages(ArrayList<MessageInfo> mi);
}
