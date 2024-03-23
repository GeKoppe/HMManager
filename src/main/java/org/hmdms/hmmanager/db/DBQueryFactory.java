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

    public static @NotNull DBQuery createSelectQuery() {
        DBQuery q = createDefaultQuery();
        q.setType(QueryTypeC.SELECT);
        return q;
    }

    public static @NotNull DBQuery createSelectQuery(String query) {
        DBQuery q = createSelectQuery();
        q.setQueryString(query);
        return q;
    }

    public static @NotNull DBQuery createInsertQuery() {
        DBQuery q = createDefaultQuery();
        q.setType(QueryTypeC.INSERT);
        return q;
    }

    public static @NotNull DBQuery createInsertQuery(String query) {
        DBQuery q = createInsertQuery();
        q.setQueryString(query);
        return q;
    }
}
