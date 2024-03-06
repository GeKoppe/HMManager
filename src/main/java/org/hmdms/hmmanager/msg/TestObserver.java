package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestObserver implements Observer {

    private final Logger logger = LoggerFactory.getLogger(TestObserver.class);
    private final String topic = "Test";
    public TestObserver() {

    }


    @Override
    public boolean notify(String topic) {
        if (topic.equals(this.topic)) return true;
        return false;
    }

    @Override
    public void giveMessages(ArrayList<MessageInfo> mi) {
        for (var info : mi) {
            logger.debug("Gotten new Message: " + info.toString());
            System.out.println(info.toString());
        }
    }
    @Override
    public boolean getState(String id) {
        return true;
    }

    public void receive(ArrayList<MessageInfo> mis) {

    }
}
