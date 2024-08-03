package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.utils.LoggingUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputFilter;

import static org.junit.Assert.*;

public class TestCache {

    private static final Logger logger = LoggerFactory.getLogger(TestCache.class);
    @Test
    public void testInitConfigCache() {
        try {
            boolean allConditionsMet = false;
            ConfigCache.initCaches();
            String userName = (String) ConfigCache.getDbConfigProperty("username");
            String jdbc = (String) ConfigCache.getDbConfigProperty("url");
            String mqHost = (String) ConfigCache.getSysConfigProperty("mq.host");
            String mq = (String) ConfigCache.getSysConfigProperty("mq.hmmanager.queue.name");

            allConditionsMet = userName.equals("hmdb")
                    && jdbc.equals("jdbc:postgresql://localhost:5433/hmdms")
                    && mqHost.equals("localhost")
                    && mq.equals("hmmanager");
            assertTrue(allConditionsMet);
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            fail();
        }
    }
    @Test
    public void testInvalidProperty() {
        try {
            ConfigCache.initCaches();
            ConfigCache.getSysConfigProperty("Test");
        } catch (Exception ex) {
            if (ex.getClass().equals(IllegalArgumentException.class)) {
                assertTrue(true);
            } else {
                fail();
            }
            return;
        }
        fail();
    }
}
