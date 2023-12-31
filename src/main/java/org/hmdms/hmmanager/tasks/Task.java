package org.hmdms.hmmanager.tasks;

import org.hmdms.hmmanager.db.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Task implements ITask {
    private final Logger logger = LoggerFactory.getLogger(Task.class);
    protected TaskTypes type;
    protected DBConnection conn;

}
