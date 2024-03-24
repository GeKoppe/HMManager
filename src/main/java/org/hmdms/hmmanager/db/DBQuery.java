package org.hmdms.hmmanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Holds information about an SQL query.
 */
public class DBQuery {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Type of the query. This is used for deciding, what method to use when querying the database.
     */
    private QueryTypeC type;
    /**
     * SQL query to execute on a database
     */
    private String queryString;

    /**
     * Constructs an empty DBQuery object
     */
    DBQuery() {

    }

    /**
     * Constructs a DBQuery object and sets classfield {@link DBQuery#queryString}
     * @param query SQL String this query should fire at the database
     */
    DBQuery(String query) {
        this.queryString = query;
    }

    /**
     * Constructs a DBQuery object and sets sql query and query type
     * @param query SQL query
     * @param type Query type
     */
    DBQuery(String query, QueryTypeC type) {
        this.queryString = query;
        this.type = type;
    }

    /**
     * Gets type of the query
     * @return type of the query
     */
    public QueryTypeC getType() {
        return type;
    }

    /**
     * Sets type of the query
     * @param type type of the query
     */
    public void setType(QueryTypeC type) {
        this.type = type;
    }

    /**
     * Gets sql query
     * @return sql query
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Sets sql query
     * @param queryString sql query
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * {@inheritDoc}
     * @param o Object to be checked for equality
     * @return True, if {@param o} is equal to this object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBQuery dbQuery)) return false;
        return getType() == dbQuery.getType() && Objects.equals(getQueryString(), dbQuery.getQueryString());
    }

    /**
     * {@inheritDoc}
     * @return Hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getType(), getQueryString());
    }
}
