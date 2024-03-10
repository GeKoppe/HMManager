package org.hmdms.hmmanager.sys;

import org.hmdms.hmmanager.core.HealthC;
import org.hmdms.hmmanager.core.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class represents a basic system component
 */
public abstract class Component {
    /**
     * State of the component
     */
    protected StateC state;
    protected HealthC health;
    /**
     * Logger
     */
    private final Logger logger;

    /**
     * Default constructor
     */
    public Component() {
        this.state = StateC.INITIALIZED;
        this.health = HealthC.HEALTHY;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Gets state of the component
     * @return State of the component
     */
    public StateC getState() {
        return state;
    }

    /**
     * Sets state of the component
     * @param state New state of the component
     */
    public void setState(StateC state) {
        this.state = state;
    }

    /**
     * Gets health of the component
     * @return Health of the component
     */
    public HealthC getHealth() {
        return health;
    }

    /**
     * Sets health of the component
     * @param health New health of the component
     */
    public void setHealth(HealthC health) {
        this.health = health;
    }
}
