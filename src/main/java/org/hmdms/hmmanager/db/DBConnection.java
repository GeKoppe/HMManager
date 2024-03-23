package org.hmdms.hmmanager.db;

import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class for executing connecting to a database and executing sql queries on that database
 */
public class DBConnection {
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

    /**
     * Executes all queries in {@link DBConnection#queries} in order.
     * Returns a {@link ResultSet} for each of the executed queries. In case of non-dql queries, the result
     * contains the number of modified rows.
     * @return All results for all queries
     * @throws SQLException When connecting to the database failed
     */
    public ArrayList<ResultSet> execute() throws SQLException {
        this.logger.debug("Establishing connection to database");
        this.connect();
        this.logger.debug("Connection established");

        // TODO nobody can match the statement to the resultset afterwards, fix this
        // TODO this method should probably not exist at all
        ArrayList<DBQuery> toRemove = new ArrayList<>();
        ArrayList<ResultSet> results = new ArrayList<>();
        for (DBQuery query : this.queries) {
            try {
                if (query.getQueryString() == null || query.getQueryString().isEmpty()) {
                    this.logger.debug("Empty query given");
                    throw new IllegalArgumentException("Empty query");
                }

                if (query.getType() == null) {
                    this.logger.debug("No query type given");
                    throw new IllegalArgumentException("Empty query type");
                }

                this.logger.debug(String.format("Query is of type %s", query.getType().name()));
                if (query.getType().compareTo(QueryTypeC.SELECT) > 0) {
                    PreparedStatement st = this.dbConnection.prepareStatement(query.getQueryString(), Statement.RETURN_GENERATED_KEYS);
                    st.execute();
                    results.add(st.getGeneratedKeys());
                } else {
                    Statement st = this.dbConnection.createStatement();
                    ResultSet rs = st.executeQuery(query.getQueryString());
                    results.add(rs);
                }
                toRemove.add(query);
            } catch (Exception ex) {
                LoggingUtils.logException(
                        ex,
                        this.logger,
                        "info"
                );
            }
        }
        this.queries.removeAll(toRemove);

        if (!this.dbConnection.isClosed()) this.disconnect();
        return results;
    }

    /**
     * Executes a single query instead of all queries in {@link DBConnection#queries}.
     * Returns the result of the execution as resultset. In case of non-dql queries, the result contains
     * the number of modified rows
     * @param query Query to be executed
     * @return Result of the executed query
     * @throws SQLException When connecting to the database failed.
     * @throws IllegalArgumentException When {@param query} is not properly filled, meaning no query string or type is
     * defined
     */
    public ResultSet execute(DBQuery query) throws SQLException, IllegalArgumentException {
        if (query.getQueryString() == null || query.getQueryString().isEmpty()) {
            this.logger.debug("Empty query given");
            throw new IllegalArgumentException("Empty query");
        }

        if (query.getType() == null) {
            this.logger.debug("No query type given");
            throw new IllegalArgumentException("Empty query type");
        }

        ResultSet rs = null;

        this.logger.debug("Establishing connection to database");
        this.connect();
        this.logger.debug("Connection established");

        try {
            this.logger.debug(String.format("Query is of type %s", query.getType().name()));
            if (query.getType().compareTo(QueryTypeC.SELECT) > 0) {
                PreparedStatement st = this.dbConnection.prepareStatement(query.getQueryString(), Statement.RETURN_GENERATED_KEYS);
                st.execute();
                rs = st.getGeneratedKeys();
            } else {
                Statement st = this.dbConnection.createStatement();
                rs = st.executeQuery(query.getQueryString());
            }
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "info"
            );
        }

        if (!this.dbConnection.isClosed()) this.disconnect();
        return rs;
    }

    /**
     * If this instance is already connected to a database, the connection is closed.
     * Afterwards a new connection to the database is created calling {@link DriverManager#getConnection(String, String, String)}
     * and the connection is saved in {@link DBConnection#dbConnection}.
     * @throws SQLException When the current status of connectivity could not be retrieved or the
     * new connection to the database could not be established
     * @throws IllegalStateException When the connection to the database is already closed when trying to close it.
     */
    private void connect() throws SQLException, IllegalStateException {
        if (this.dbConnection != null && !this.dbConnection.isClosed()) this.disconnect();
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
    private void disconnect() throws IllegalStateException, SQLException {
        this.logger.debug("Starting to disconnect from database");
        if (this.dbConnection == null || this.dbConnection.isClosed()) {
            this.logger.info("Trying to disconnect from a database to which the object is not connected");
            throw new IllegalStateException("Not connected to database");
        }
        this.dbConnection.close();
        this.logger.debug("Disconnected from database");
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
