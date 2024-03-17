package org.hmdms.hmmanager;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.msg.*;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
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

            boolean currentlyTest = false;
            while (true) {
                if (!coordinatorThread.isAlive()) {
                    return;
                }
                Thread.sleep(500);
                MessageInfo mi = MessageInfoFactory.createDefaultMessageInfo();
                mi.setFrom("Me");

                HashMap<String, String> hm = new HashMap<>();
                hm.put("Hello", "World");
                hm.put("Moin", "Welt");
                mi.setInformation(hm);
                currentlyTest = !currentlyTest;
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                try (Connection conn = factory.newConnection(); Channel ch = conn.createChannel()) {
                    ch.queueDeclare("coordinator", false, false, false, null);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(out);
                    os.writeObject(mi);
                    ch.basicPublish("", "coordinator", null, out.toByteArray());
                    out.flush();
                } catch (Exception ex) {
                    LoggingUtils.logException(ex, logger);
                }
            }
        } catch (Exception ex) {
            logger.warn("Exception in running the coordinator: " + ex.getMessage());
        }

    }
}
