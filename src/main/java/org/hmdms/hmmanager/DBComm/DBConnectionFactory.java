package org.hmdms.hmmanager.DBComm;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DBConnectionFactory {

    @Contract(value = " -> new", pure = true)
    public static @NotNull DBConnection newConnection() throws Exception {
        return new DBConnection();
    }
    @Contract(pure = true)
    public static @NotNull DBConnection newConnection(String jdbc, String command) throws Exception {
        DBConnection conn = new DBConnection();
        return conn;
    }
}
