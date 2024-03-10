package org.hmdms.hmmanager;

import org.hmdms.hmmanager.core.StateC;
import org.hmdms.hmmanager.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;

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

            ConfigurableApplicationContext ctx = SpringApplication.run(JmsBroker.class);
            JmsTemplate tpl = ctx.getBean(JmsTemplate.class);
            co.setTpl(tpl);

            boolean currentlyTest = false;
            while (true) {
                Thread.sleep(500);
                MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
                mi.setFrom("Me");

                HashMap<String, String> hm = new HashMap<>();
                hm.put("Hello", "World");
                hm.put("Moin", "Welt");
                mi.setInformation(hm);
                tpl.convertAndSend("coordinator",  new JmsMessage(currentlyTest ? TopicC.TEST : TopicC.LOGIN, mi));
                currentlyTest = !currentlyTest;
            }
        } catch (Exception ex) {
            logger.warn("Exception in running the coordinator: " + ex.getMessage());
        }

    }
}
