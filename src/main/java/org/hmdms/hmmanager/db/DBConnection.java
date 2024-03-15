package org.hmdms.hmmanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DBConnection {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String query;
    DBConnection() {

    }

    /**
     * Returns the query the connection is supposed to execute on the database
     * @return Query the connection is supposed to execute on the database
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query the connection is supposed to execute on the database
     * @param query Query the connection is supposed to execute on the database
     */
    public void setQuery(String query) {
        this.query = query;
    }
}
