package org.hmdms.hmmanager;

import org.hmdms.hmmanager.tasks.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HMManager {
    private static final Logger logger = LoggerFactory.getLogger(HMManager.class);
    public static void main(String[] args) {
        logger.info("Starting application");

        TaskManager tm = new TaskManager();
        tm.run();

        logger.info("Exiting application");
    }
}
