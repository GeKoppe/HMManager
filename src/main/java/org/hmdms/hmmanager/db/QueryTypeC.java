package org.hmdms.hmmanager.db;

/**
 * Defines the type of the query to be executed.
 * Used internally by {@link DBConnection} when executing query to decide, what kind of {@link java.sql.ResultSet} is
 * expected from the execution and how to execute the query-
 */
public enum QueryTypeC {
    /**
     * A SELECT Query
     */
    SELECT,
    /**
     * An UPDATE Query
     */
    UPDATE,
    /**
     * An INSERT Query
     */
    INSERT,
    /**
     * A DELETE Query
     */
    DELETE
}
