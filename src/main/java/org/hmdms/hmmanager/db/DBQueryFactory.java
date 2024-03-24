package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class for Database Query objects
 */
public class DBQueryFactory {

    /**
     * Creates an empty {@link DBQuery} object and returns it.
     * @return An empty {@link DBQuery} object
     */
    @Contract(" -> new")
    public static @NotNull DBQuery createDefaultQuery() {
        return new DBQuery();
    }

    /**
     * Callback to {@link DBQueryFactory#createDefaultQuery()}. Sets the query type to {@link QueryTypeC#SELECT}
     * @return A {@link DBQuery} Object with it's type set.
     */
    public static @NotNull DBQuery createSelectQuery() {
        DBQuery q = createDefaultQuery();
        q.setType(QueryTypeC.SELECT);
        return q;
    }

    /**
     * Callback to {@link DBQueryFactory#createSelectQuery()}. Sets SQL query in the new {@link DBQuery} object
     * @param query SQL Query to be executed on the database
     * @return {@link DBQuery} object with both type and sql query set.
     */
    public static @NotNull DBQuery createSelectQuery(String query) {
        DBQuery q = createSelectQuery();
        q.setQueryString(query);
        return q;
    }

    /**
     * Callback to {@link DBQueryFactory#createDefaultQuery()}. Sets type in the new {@link DBQuery} object to
     * {@link QueryTypeC#INSERT} and returns that object.
     * @return {@link DBQuery} object with type set to {@link QueryTypeC#INSERT}
     */
    public static @NotNull DBQuery createInsertQuery() {
        DBQuery q = createDefaultQuery();
        q.setType(QueryTypeC.INSERT);
        return q;
    }

    /**
     * Callback to {@link DBQueryFactory#createInsertQuery()}. Sets sql query on the new query object to {@param query}
     * and returns that object.
     * @param query SQL Query which should be executed by the query
     * @return {@link DBQuery} object with type set to {@link QueryTypeC#INSERT} and sql query set to {@param query}.
     */
    public static @NotNull DBQuery createInsertQuery(String query) {
        DBQuery q = createInsertQuery();
        q.setQueryString(query);
        return q;
    }
}
