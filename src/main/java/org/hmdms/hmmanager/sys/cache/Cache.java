package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.sys.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

// TODO this is probably better implemented as an interface
/**
 * Base class for all caches
 */
public abstract class Cache {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    /**
     * Locks for objects
     */
    protected static HashMap<String, ReentrantLock> locks = new HashMap<>();

    protected static StateC state;
    /**
     * Default constructor
     */
    public Cache() { }

    /**
     * Invalidates the cache
     * Must be implemented by subclasses
     * @return True, if cache could be invalidated
     */
    public static boolean invalidate() {
        return true;
    }
}
