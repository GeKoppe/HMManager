package org.hmdms.hmmanager.msg;

/**
 * Collection of all message types in the system
 */
public enum TopicC {
    /**
     * INTERNAL USE
     */
    TEST,
    /**
     * All messages concerning logins and authorisation at the system.
     */
    AUTH,
    /**
     * All messages concerning file upload or download
     */
    FILE
}
