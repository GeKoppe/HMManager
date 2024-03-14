package org.hmdms.hmmanager.sys;

import java.util.Date;

/**
 * Class to monitor performance of components
 */
public class PerformanceCheck {
    /**
     * Start date of the last / current operation
     */
    private Date currentOpStart;
    /**
     * End date of the last / current operation
     */
    private Date currentOpEnd;
    /**
     * Average time in milliseconds an operation takes
     */
    private int averageOperationTime;
    /**
     * Baseline time in milliseconds it takes for operations to complete
     */
    private int baselineTime;
    /**
     * Number of operations monitored
     */
    private int opsMonitored;

    /**
     * Default constructor
     */
    public PerformanceCheck() {
        this.currentOpStart = null;
        this.currentOpEnd = null;
        this.averageOperationTime = 0;
        this.baselineTime = 0;
        this.opsMonitored = 0;
    }

    /**
     * Sets {@link PerformanceCheck#currentOpStart} to current date and nulls {@link PerformanceCheck#currentOpEnd}.
     */
    public void startOperation() {
        this.currentOpStart = new Date();
        this.currentOpEnd = null;
    }

    /**
     * Ends the current operation
     * @return Relative discrepancy in operation time to baseline
     * TODO finish this implementation. Not yet needed but will be interesting at some point
     */
    public int endOperation() {
        this.currentOpEnd = new Date();
        int operationLength = (int) (this.currentOpEnd.getTime() - this.currentOpStart.getTime());

        if (this.averageOperationTime == 0) {
            this.averageOperationTime = operationLength;
        }

        this.opsMonitored++;

        if (this.baselineTime == 0) {
            this.baselineTime = operationLength;
        }

        return operationLength;
    }

    /**
     * Gets average operation time in milliseconds
     * @return average operation time in milliseconds
     */
    public int getAverageOperationTime() {
        return averageOperationTime;
    }

    /**
     * Gets the baseline operation time in milliseconds calculated by the performance check
     * @return Baseline operation time in milliseconds calculated by the performance check
     */
    public int getBaselineTime() {
        return baselineTime;
    }

    /**
     * Gets number of operations monitored
     * @return Number of operations monitored
     */
    public int getOpsMonitored() {
        return opsMonitored;
    }
}
