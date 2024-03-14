package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.sys.StateC;
import org.junit.Test;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;

import static org.junit.Assert.*;

public class TestCoordinator {

    private final Logger logger = LoggerFactory.getLogger(TestCoordinator.class);

    @Test
    public void instBrokerAmountByConfig() {
        try {
            Coordinator co = new Coordinator();
            assertEquals(2, co.getBrokers().size());
        } catch (Exception ex) {
            this.logger.info("Exception occurred in test instBrokers: " + ex.getMessage());
            fail();
        }
    }

    @Test
    public void addNewMessage() {
        try {
            Coordinator co = new Coordinator();
            MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
            TestSubscriber t = new TestSubscriber();
            mi.setFrom("Me");

            HashMap<String, String> hm = new HashMap<>();
            hm.put("Hello", "World");
            hm.put("Moin", "Welt");
            mi.setInformation(hm);

            co.newMessage(TopicC.TEST, mi);
            assertTrue(true);
        } catch (Exception ex) {
            this.logger.info("Exception occurred in test addNewMessage: " + ex.getMessage());
            fail();
        }
    }

    @Test
    public void addMessageAndNotifySingle() {
        try {
            Coordinator co = new Coordinator();
            MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
            TestSubscriber t = new TestSubscriber();
            mi.setFrom("Me");

            HashMap<String, String> hm = new HashMap<>();
            hm.put("Hello", "World");
            hm.put("Moin", "Welt");
            mi.setInformation(hm);

            co.newMessage(TopicC.TEST, mi);
            for (var b : co.getBrokers()) {
                //b.notifySubscriber(TopicC.LOGIN);
            }

            for (var b : co.getBrokers()) {
                //b.notifySubscriber(TopicC.TEST);
            }

            for (var b : co.getBrokers()) {
                b.notifyAllSubscribers();
            }
            assertTrue(true);
        } catch (Exception ex) {
            this.logger.info("Exception occurred in test addNewMessage: " + ex.getMessage());
            fail();
        }
    }

    @Test
    public void testThread() {
        try {
            Coordinator co = new Coordinator();
            MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
            mi.setFrom("Me");

            HashMap<String, String> hm = new HashMap<>();
            hm.put("Hello", "World");
            hm.put("Moin", "Welt");
            mi.setInformation(hm);
            co.setup();
            Thread thread = new Thread(co);
            thread.start();

            co.newMessage(TopicC.LOGIN, mi);
            co.newMessage(TopicC.TEST, mi);
            Thread.sleep(12000);
            co.setState(StateC.STOPPED);
            Thread.sleep(1000);
        } catch (Exception ex) {
            this.logger.info("Exception occured in test testThreead: " + ex.getMessage());
            fail();
        }
    }

}
