package org.hmdms.hmmanager.db;

import org.hmdms.hmmanager.sys.cache.ConfigCache;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
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
     * Other database connections can only be instantiated by using getters and setters when this constructor was used.
     */
    DBConnection() throws IllegalArgumentException {
        this.logger.debug("Instantiating new default db connection");
        try {
            this.user = (String) ConfigCache.getDbConfigProperty("username");
            this.password = (String) ConfigCache.getDbConfigProperty("password");
            this.jdbcString = (String) ConfigCache.getDbConfigProperty("url");
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
                Statement st = this.dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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

    /**
     * Gets jdbc string this object uses for db connection
     * @return jdbc string this object uses for db connection
     */
    public String getJdbcString() {
        return jdbcString;
    }

    /**
     * Sets jdbc string this object uses for db connection
     * @param jdbcString jdbc string this object uses for db connection
     */
    public void setJdbcString(String jdbcString) {
        this.jdbcString = jdbcString;
    }

    /**
     * Gets name of the user for connecting to database
     * @return name of the user for connecting to database
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets name of the user for connecting to database
     * @param user name of the user for connecting to database
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets password used for connecting to database
     * @return password used for connecting to database
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password used for connecting to database
     * @param password password used for connecting to database
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     * @param o Object to be checked for equality
     * @return True, if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBConnection that)) return false;
        return Objects.equals(dbConnection, that.dbConnection) && Objects.equals(getJdbcString(), that.getJdbcString()) && Objects.equals(getUser(), that.getUser()) && Objects.equals(getPassword(), that.getPassword());
    }

    /**
     * {@inheritDoc}
     * @return Hashcode of th object
     */
    @Override
    public int hashCode() {
        return Objects.hash(dbConnection, getJdbcString(), getUser(), getPassword());
    }
}
