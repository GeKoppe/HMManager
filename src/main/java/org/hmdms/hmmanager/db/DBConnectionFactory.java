package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Factory class for constructing DB Connections
 */
public abstract class DBConnectionFactory {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DBConnectionFactory.class);
    /**
     * Creates a new DB Connection object for connecting to the applications own database.
     * Uses the default constructor {@link DBConnection#DBConnection()}, which reads the database configuration
     * from the db_config.properties file to connect to the database.
     * @return DBConnection object with an instantiated Connection property which is configured to access the
     * applications own database
     * @throws IOException Thrown when an exception occurs during the read of the db_config.properties file
     */
    @Contract(" -> new")
    public static @NotNull DBConnection newDefaultConnection() throws IOException {
        try {
            return new DBConnection();
        } catch (Exception ex) {
            logger.info(String.format("%s during instantiation of a new DBConnection due to: %s", ex.getClass().getName(), ex.getMessage()));
            StringBuilder sb = new StringBuilder();
            for (var stel : ex.getStackTrace()) {
                sb.append(stel.toString());
                sb.append("\n\t");
            }
            logger.trace(sb.toString());
            throw ex;
        }
    }
}
