package org.hmdms.hmmanager.msg;

/**
 * Defines states for various components in the system
 */
public enum StateC {
    /**
     * Initialized but did not start yet
     */
    INITIALIZED,
    /**
     * Component has started
     */
    STARTED,
    /**
     * Component is currently doing something
     */
    WORKING,
    /**
     * Component was completely stopped
     */
    STOPPED,
    /**
     * Component was disregarded
     */
    DESTROYED
}
