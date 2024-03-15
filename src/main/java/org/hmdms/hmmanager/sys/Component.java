package org.hmdms.hmmanager.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Class represents a basic system component
 */
public abstract class Component {
    /**
     * State of the component
     */
    protected StateC state;
    /**
     * Health of the component
     */
    protected HealthC health;
    /**
     * Logger
     */
    protected final Logger logger;

    protected final HashMap<String, PerformanceCheck> performance;

    /**
     * Default constructor
     */
    public Component() {
        this.state = StateC.INITIALIZED;
        this.health = HealthC.HEALTHY;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.performance = new HashMap<>();
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

    public void addPerformanceCheck(String id) throws IllegalArgumentException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("No id given");
        }

        if (this.performance.get(id) != null) {
            throw new IllegalArgumentException("Performance id already exists");
        }

        this.performance.put(id, new PerformanceCheck());
    }

    protected void startOperation(String id) throws IllegalArgumentException {
        if (this.performance.get((id)) == null) {
            throw new IllegalArgumentException(String.format("No performancechecker with id %s exists", id));
        }
        this.performance.get(id).startOperation();
    }

    /**
     * Ends operation with given id and returns duration of that operation
     * @param id ID of the performance checker which should end the operation
     * @return Duration of the operation in milliseconds
     */
    protected int endOperation(String id) {
        if (this.performance.get((id)) == null) {
            throw new IllegalArgumentException(String.format("No performancechecker with id %s exists", id));
        }
        return this.performance.get(id).endOperation();
    }
}
