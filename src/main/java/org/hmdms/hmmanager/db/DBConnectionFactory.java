package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Factory implementation for DBConnection. Provides methods
 */
public class DBConnectionFactory {
    private static final Logger logger = LoggerFactory.getLogger(DBConnectionFactory.class);

    /**
     * Creates an object for connection to the default application database as defined in
     * config.properties file
     * @return Database connection object
     */
    @Contract(value = " -> new", pure = true)
    public static DBConnection newDefaultConnection() {
        DBConnection conn;
        try {
            conn = new DBConnection();
            logger.debug("Instanciated DBConnection");
        } catch (Exception ex) {
            logger.info(ex.getClass().getName() + " during instanciation in method newDefaultConnection: " + ex.getMessage());
            conn = null;
        }
        return conn;
    }

    /**
     * Creates a custom object for database connection to any existing database
     * @param jdbc JDBC Connection String to the Database
     * @param user User for database connection
     * @param pw Password for database connection
     * @return Object for database connection to given
     */
    @Contract(pure = true)
    public static DBConnection newConnection(String jdbc, String user, String pw, String dbName) {
        DBConnection conn;
        try {
            conn = new DBConnection(jdbc, user, pw, dbName);
            logger.debug("Instanciated DBConnection " + conn);
        } catch (Exception ex) {
            logger.info(ex.getClass().getName() + " during instanciation in method newDefaultConnection: " + ex.getMessage());
            logger.debug(Arrays.toString(ex.getStackTrace()));
            conn = null;
        }
        return conn;
    }

    /**
     * Clones the given connection object
     * @param conn Connection to be cloned
     * @return New instance of DBConnection class with same fields as in given conn
     */
    @Contract("_ -> new")
    public static @NotNull DBConnection cloneConnection(@NotNull DBConnection conn) {
        logger.debug("Cloning connection " + conn);
        return new DBConnection(conn.getConnString(), conn.getUser(), conn.getPw(), conn.getDbName());
    }
}
