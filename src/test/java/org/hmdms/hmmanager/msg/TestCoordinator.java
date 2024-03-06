package org.hmdms.hmmanager.msg;

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
            TestObserver t = new TestObserver();
            for (var b : co.getBrokers()) {
                b.addObserver(t);
            }
            mi.setFrom("Me");

            HashMap<String, String> hm = new HashMap<>();
            hm.put("Hello", "World");
            hm.put("Moin", "Welt");
            mi.setInformation(hm);

            co.newMessage("Test", mi);
            assertTrue(true);
        } catch (Exception ex) {
            this.logger.info("Exception occurred in test addNewMessage: " + ex.getMessage());
            fail();
        }
    }
}
