package org.hmdms.hmmanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class Structure extends Element {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Structure.class);

    public Structure() { }

    /**
     * TODO implement
     * @param rs ResultSet from which to fill the IFillable
     * @return
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
