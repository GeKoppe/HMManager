package org.hmdms.hmmanager;

import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.msg.*;
import org.hmdms.hmmanager.sys.cache.ConfigCache;
import org.hmdms.hmmanager.sys.exceptions.system.CachingException;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputFilter;

public final class HMManager {
    private static final Logger logger = LoggerFactory.getLogger(HMManager.class);
    private static StateC state = StateC.WORKING;
    public static void main(String[] args) {
        try {
            try {
                loadConfigs();
            } catch (Exception ex) {
                // TODO Error handling
            }
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

    private static void loadConfigs() throws CachingException {
        try {
            ConfigCache.loadDbConfig();
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            throw new CachingException(
                    ex,
                    String.format(
                            "%s occurred while caching a config: %s",
                            ex.getClass().getName(),
                            ex.getMessage()
                    )
            );
        }
    }
}
