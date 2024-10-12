package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.core.user.ExecutionContext;
import org.hmdms.hmmanager.sys.cache.ConfigCache;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestHMInterface {

    private final Logger logger = LoggerFactory.getLogger(TestHMInterface.class);
    private ExecutionContext ec;

    private HMInterface hmInterface;
    @Before
    public void setUp() throws InterruptedException {
        this.ec = new ExecutionContext("Hello World");
        this.hmInterface = new HMInterface(this.ec);
        Future<Boolean> c = ConfigCache.initCachesAsync();
        while (!c.isDone()) {
            this.logger.trace("Loading configs...");
            //noinspection BusyWait
            Thread.sleep(50);
        }

    }

    @Test
    public void testGetElement() {
        try {
            ArrayList<ElementC> inf = new ArrayList<>();
            inf.add(ElementC.ALL_ELEMENT);

            Element el = this.hmInterface.getElement("1", inf);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}
