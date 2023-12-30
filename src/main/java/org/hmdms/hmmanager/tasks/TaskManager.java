package org.hmdms.hmmanager.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public final class TaskManager implements Runnable {
    private ArrayList<ITask> newTasks;
    private final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    public TaskManager() {
        this.newTasks = new ArrayList<>();
        this.logger.info("TaskManager online");
    }

    @Override
    public void run() {
        while (true) {
            this.logger.debug("Currently there are " + newTasks.size() + " new Tasks");
        }
    }
}
