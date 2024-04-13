package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.utils.LoggingUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class TestCache {

    private static final Logger logger = LoggerFactory.getLogger(TestCache.class);
    @Test
    public void initConfigCache() {
        try {
            ConfigCache.initCaches();
            String userName = (String) ConfigCache.getDbConfigProperty("username");
            String jdbc = (String) ConfigCache.getDbConfigProperty("url");

            if (userName.equals("hmdb") && jdbc.equals("jdbc:postgresql://localhost:5433/hmdms")) {
                assertTrue(true);
            } else {
                fail();
            }
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            fail();
        }
    }
}
