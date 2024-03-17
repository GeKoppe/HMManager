package org.hmdms.hmmanager;

import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HMManager {
    private static final Logger logger = LoggerFactory.getLogger(HMManager.class);
    private static StateC state = StateC.WORKING;
    public static void main(String[] args) {
        try {
            Thread coordinatorThread = new Thread(new Coordinator());
            coordinatorThread.start();

            while (true) {
                if (!coordinatorThread.isAlive()) {
                    return;
                }
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            logger.warn("Exception in running the coordinator: " + ex.getMessage());
        }

    }
}
