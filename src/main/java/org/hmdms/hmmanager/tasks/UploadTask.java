package org.hmdms.hmmanager.tasks;

import org.hmdms.hmmanager.db.DBConnection;
import org.hmdms.hmmanager.db.DBConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadTask extends Task {
    private final Logger logger = LoggerFactory.getLogger(UploadTask.class);
    private String sqlStatement;
    private final DBConnection conn = DBConnectionFactory.newDefaultConnection();
    public UploadTask() {
        this.logger.debug("Instanciated new UploadTask");
    }

    @Override
    public boolean execute() {

        return true;
    }
}
