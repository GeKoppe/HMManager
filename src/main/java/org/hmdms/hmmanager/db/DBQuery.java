package org.hmdms.hmmanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds information about an SQL query.
 */
public class DBQuery {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Type of the query. This is used for deciding, what method to use when querying the database.
     */
    private QueryTypeC type;
    /**
     * SQL query to execute on a database
     */
    private String queryString;
    DBQuery() {

    }

    DBQuery(String query) {
        this.queryString = query;
    }
    DBQuery(String query, QueryTypeC type) {
        this.queryString = query;
        this.type = type;
    }


    public QueryTypeC getType() {
        return type;
    }

    public void setType(QueryTypeC type) {
        this.type = type;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
