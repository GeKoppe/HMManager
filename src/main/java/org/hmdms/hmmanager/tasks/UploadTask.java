package org.hmdms.hmmanager.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadTask extends Task {
    private final Logger logger = LoggerFactory.getLogger(UploadTask.class);
    private final List<String> keys = new ArrayList<String>();

    public UploadTask() {
        this.logger.debug("Instanciated new UploadTask");
    }

    @Override
    public boolean execute() {

        return true;
    }
}
