package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
     * Service to do config loading asynchronously
     */
    protected static ExecutorService ex = Executors.newFixedThreadPool(5);
    /**
     * Locks for objects
     */
    protected static HashMap<String, ReentrantLock> locks = new HashMap<>();

    /**
     * State of the cache
     */
    protected static StateC state;

    protected static boolean cacheInitialized = false;
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

    /**
     * Tries to acquire lock with the given id
     * @param lockId ID of the lock that should be acquired
     * @return True, if locking worked, false otherwise
     * @throws IllegalArgumentException When null or an empty string is given in {@param lockId} or no lock
     * with given id exists
     */
    protected static boolean tryToAcquireLock(String lockId) throws IllegalArgumentException {
        logger.trace("Thread " + Thread.currentThread() + " trying to acquire lock for id " + lockId);
        if (lockId == null || lockId.isEmpty()) {
            throw new IllegalArgumentException("No lockId given");
        }
        if (locks.get(lockId) == null) {
            throw new IllegalArgumentException("No lock with lockId " + lockId + " defined");
        }

        if (!locks.get(lockId).isHeldByCurrentThread()) {
            try {
                if (!locks.get(lockId).tryLock(200, TimeUnit.MILLISECONDS)) {
                    logger.trace("Lock " + locks.get(lockId) + " could not be acquired, still held by other thread");
                    return false;
                }
                logger.trace("Lock " + locks.get(lockId) + " now held by " + Thread.currentThread());
            } catch (Exception ex) {
                LoggingUtils.logException(ex, logger);
                return false;
            }
        }
        return true;
    }

    /**
     * Unlocks the lock with given {@param lockId}
     * @param lockId The lock to be unlocked
     * @throws IllegalArgumentException When null or an empty string is given in {@param lockId} or no lock
     * with given id exists
     */
    protected static void unlock(String lockId) throws IllegalArgumentException {
        logger.trace("Thread " + Thread.currentThread() + " trying to release lock for id " + lockId);
        if (lockId == null || lockId.isEmpty()) {
            throw new IllegalArgumentException("No lockId given");
        }
        if (locks.get(lockId) == null) {
            throw new IllegalArgumentException("No lock with lockId " + lockId + " defined");
        }

        if (locks.get(lockId).isHeldByCurrentThread()) locks.get(lockId).unlock();
    }
}
