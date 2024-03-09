package org.hmdms.hmmanager.sys;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class represents a system component that uses blocking actions like concurrent reading and writing
 * on the same object.
 * Locks must be defined by child class.
 */
public abstract class BlockingComponent extends Component {
    /**
     * Map of all locks
     */
    private final HashMap<String, ReentrantLock> locks;
    /**
     * Logger
     */
    private final Logger logger;
    /**
     * Default constructor for BlockingComponent
     * @param lockIds Ids for all locks that should be instantiated
     * @throws IllegalArgumentException When no lockIds are given, an entry in {@param lockIds} is empty
     * or if there is a duplicate
     */
    public BlockingComponent(String @NotNull [] lockIds) throws IllegalArgumentException {
        super();

        // Check if lockIds are given
        if (lockIds.length == 0) {
            throw new IllegalArgumentException("No lockIds given");
        }
        this.locks = new HashMap<>();

        // Iterate through all lockIds and add a lock to the hashmap
        for (String lockId : lockIds) {
            // Check, that there are no empty or duplicate ids
            if (lockId == null || lockId.isEmpty()) {
                throw new IllegalArgumentException("Empty lock id given");
            }
            if (this.locks.get(lockId) != null) {
                throw new IllegalArgumentException("Duplicate lockId given");
            }
            ReentrantLock newLock = new ReentrantLock();
            this.locks.put(lockId, newLock);
        }

        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Tries to acquire lock with the given id
     * @param lockId ID of the lock that should be acquired
     * @return True, if locking worked, false otherwise
     * @throws IllegalArgumentException When null or an empty string is given in {@param lockId} or no lock
     * with given id exists
     */
    protected boolean tryToAcquireLock(String lockId) throws IllegalArgumentException {
        this.logger.trace("Thread " + Thread.currentThread() + " trying to acquire lock for id " + lockId);
        if (lockId == null || lockId.isEmpty()) {
            throw new IllegalArgumentException("No lockId given");
        }
        if (this.locks.get(lockId) == null) {
            throw new IllegalArgumentException("No lock with lockId " + lockId + " defined");
        }

        if (!this.locks.get(lockId).isHeldByCurrentThread()) {
            try {
                if (!this.locks.get(lockId).tryLock(200, TimeUnit.MILLISECONDS)) {
                    this.logger.trace("Lock " + this.locks.get(lockId) + " could not be acquired, still held by other thread");
                    return false;
                }
                this.logger.trace("Lock " + this.locks.get(lockId) + " now held by " + Thread.currentThread());
            } catch (Exception ex) {
                this.logger.debug("Exception occurred while trying to acquire lock on object " + locks.get(lockId) + ": " + ex.getMessage());
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
    protected void unlock(String lockId) throws IllegalArgumentException {
        this.logger.trace("Thread " + Thread.currentThread() + " trying to release lock for id " + lockId);
        if (lockId == null || lockId.isEmpty()) {
            throw new IllegalArgumentException("No lockId given");
        }
        if (this.locks.get(lockId) == null) {
            throw new IllegalArgumentException("No lock with lockId " + lockId + " defined");
        }

        if (this.locks.get(lockId).isHeldByCurrentThread()) this.locks.get(lockId).unlock();
    }
}
