package org.hmdms.hmmanager.tasks;

import org.hmdms.hmmanager.db.DBConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadTask extends Task {
    private final Logger logger = LoggerFactory.getLogger(UploadTask.class);
    public UploadTask() {
        this.type = TaskTypes.UPLOAD;
        this.conn = DBConnectionFactory.newDefaultConnection();
        this.logger.debug("Instanciated new UploadTask");
    }

    @Override
    public boolean execute() {

        return true;
    }

    @Override
    public void answer() {
        this.logger.debug("Answering message from message queue");
    }
}
