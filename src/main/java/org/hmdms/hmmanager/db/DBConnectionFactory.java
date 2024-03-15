package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class for constructing DB Connections
 */
public abstract class DBConnectionFactory {

    @Contract(" -> new")
    public static @NotNull DBConnection newDefaultConnection() {
        return new DBConnection();
    }
}
