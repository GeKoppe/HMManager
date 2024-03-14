package org.hmdms.hmmanager.sys;

/**
 * Used for tracking health of system components and objects.
 */
public enum HealthC {
    /**
     * Component is working faster than initially calculated as baseline operation time by a defined percentage
     */
    OVERACHIEVING,
    /**
     * Component is working as intended
     */
    HEALTHY,
    /**
     * Component is slower than usual.
     * Component will analyse its own speed during first few operations and set it as a baseline for
     * the components speed. If consecutive operations are slower by a defined percentage, component will mark itself
     * as slow.
     */
    SLOW,
    /**
     * Component ran into several problems
     * This could for example be several dropped messages in case of the {@link org.hmdms.hmmanager.msg.Coordinator}.
     */
    TROUBLED,
    /**
     * Component does not respond anymore. This could be due to processes not being able to reserve locks
     * in case of component being a {@link org.hmdms.hmmanager.sys.BlockingComponent}.
     */
    UNRESPONSIVE
}
