package org.hmdms.hmmanager;

import org.hmdms.hmmanager.core.StateC;
import org.hmdms.hmmanager.msg.Coordinator;
import org.hmdms.hmmanager.msg.MessageInfo;
import org.hmdms.hmmanager.msg.MessageInfoFactory;
import org.hmdms.hmmanager.msg.TopicC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class HMManager {
    private static final Logger logger = LoggerFactory.getLogger(HMManager.class);
    private static StateC state = StateC.WORKING;
    public static void main(String[] args) {
        try {
            Coordinator co = new Coordinator();
            co.setup();
            Thread coordinatorThread = new Thread(co);
            coordinatorThread.start();

            boolean currentlyTest = true;

            while(state.equals(StateC.WORKING)) {
                Thread.sleep(1000);
                MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
                mi.setFrom("Me");

                HashMap<String, String> hm = new HashMap<>();
                hm.put("Hello", "World");
                hm.put("Moin", "Welt");
                mi.setInformation(hm);

                co.newMessage(currentlyTest ? TopicC.TEST : TopicC.LOGIN, mi);
                currentlyTest = !currentlyTest;
            }
            co.setState(StateC.STOPPED);
            coordinatorThread.interrupt();
        } catch (Exception ex) {
            logger.warn("Exception in running the coordinator: " + ex.getMessage());
        }

    }
}
