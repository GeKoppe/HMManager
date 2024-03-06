package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestSubscriber implements ISubscriber {

    private final Logger logger = LoggerFactory.getLogger(TestSubscriber.class);
    private final TopicC topic = TopicC.TEST;
    public TestSubscriber() {

    }


    @Override
    public boolean notify(TopicC topic) {
        if (topic == this.topic) return true;
        return false;
    }

    @Override
    public void giveMessages(ArrayList<MessageInfo> mi) {
        for (var info : mi) {
            logger.debug("Gotten new Message: " + info.toString());
            System.out.println(info);
        }
    }
    @Override
    public boolean getState(String id) {
        return true;
    }

}
