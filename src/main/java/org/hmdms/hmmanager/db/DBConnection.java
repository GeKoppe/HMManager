package org.hmdms.hmmanager.db;

import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class for executing connecting to a database and executing sql queries on that database
 */
class DBConnection {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Prepared queries for this connection should execute on the database
     */
    private final ArrayList<DBQuery> queries = new ArrayList<>();
    /**
     * {@link Connection} object used for executing queries on the database
     */
    private Connection dbConnection;
    /**
     * Jdbc Connection String
     */
    private String jdbcString;
    /**
     * User used for connecting to the database
     */
    private String user;
    /**
     * Password of {@link DBConnection#user}.
     */
    private String password;

    /**
     * Default constructor for DBConnection object.
     * This constructor reads the db_config.properties file from the resources and uses the given information
     * to initialise a database connection.
     * Other database
     * @throws IOException When the configuration file or parts of it could not be read
     */
    DBConnection() throws IOException {
        this.logger.debug("Instantiating new default db connection");
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        this.logger.info("Read the configuration file");
        try {
            this.user = prop.get("user").toString();
            // TODO check if password is hashed and do something with it
            this.password = prop.get("password").toString();
            this.jdbcString = prop.get("").toString();
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "warn",
                    "%s exception occured while reading the database configuration from db_config.properties: %s"
            );
            throw ex;
        }
    }

    private void connect() throws SQLException {
        try {
            this.dbConnection = DriverManager.getConnection(this.jdbcString, this.user, this.password);
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "warn",
                    "%s exception occured while connecting to the database: %s"
            );
            throw ex;
        }
    }

    /**
     * Disconnects from the database if the connection is currently open
     * @throws IllegalStateException When the connection is not currently established.
     * @throws SQLException When something goes wrong with closing the connection
     */
    public void disconnect() throws IllegalStateException, SQLException {
        if (this.dbConnection == null || this.dbConnection.isClosed()) {
            this.logger.info("Trying to disconnect from a database to which the object is not connected");
            throw new IllegalStateException("Not connected to database");
        }
        this.dbConnection.close();
    }


    public String getJdbcString() {
        return jdbcString;
    }

    public void setJdbcString(String jdbcString) {
        this.jdbcString = jdbcString;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
