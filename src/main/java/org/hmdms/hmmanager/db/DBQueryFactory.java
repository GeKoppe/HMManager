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
}
