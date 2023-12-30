package org.hmdms.hmmanager.tasks;

public interface ITask {
    public default boolean execute() {
        return false;
    }
}
