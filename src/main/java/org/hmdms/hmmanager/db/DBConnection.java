package org.hmdms.hmmanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class DBConnection {
    /**
     * JDBC String for connection to the database
     */
    private String connString;
    /**
     * User for connecting to the databse
     */
    private String user;
    /**
     * Password for connecting to the database with user in user
     */
    private String pw;

    private final String url;
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    /**
     * Database connection object
     */
    private Connection conn;
    private String query;

    private String dbName;

    /**
     * Instanciates class with params for connection to default database of the application.
     * Params are loaded from config.properties file in resources
     * @throws Exception Thrown, when config.properties file could not be found or read
     */
    public DBConnection() throws Exception {
        this.logger.debug("Instanciating new default DBConnection");
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new Exception("No config.properties file found");
            }
            Properties props = new Properties();
            props.load(input);
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.pw = props.getProperty("db.pw");
            this.dbName = props.getProperty("db.name");
        } catch (Exception ex) {
            this.logger.debug(ex.getClass().getName() + " during instanciation of DBConnection: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Instanciates custom database connection
     * @param jdbc Connection string to database
     * @param user User for database connection
     * @param pw Password for database connection
     */
    public DBConnection(String jdbc, String user, String pw, String dbName) {
        this.url = jdbc;
        this.user = user;
        this.pw = pw;
        this.dbName = dbName;
    }

    /**
     * Instanciates an {@link java.sql.Connection} object in object scope
     * @return True, if connection succeded, false otherwise
     */
    public boolean connect() {
        try {
            if (this.connString == null) this.buildConnString();
            this.conn = DriverManager.getConnection(this.connString);
            logger.debug("Connected to database");
        } catch (Exception ex) {
            logger.info(ex.getClass().getName() + " in method connect: " + ex.getMessage());
            logger.debug(Arrays.toString(ex.getStackTrace()));
            return false;
        }
        return true;
    }

    private void buildConnString() {
        logger.debug("Instanciating connection String to database");
        String qb = this.url +
                "/" +
                this.dbName +
                "?user=" +
                this.user +
                "&password=" +
                this.pw;
        this.connString = qb;
    }

    /**
     * Closes existing connection to database
     * @return True, when disconnect was successful
     * @throws IllegalStateException Whenever there is either no connection object instanciated
     * (the connect method hasn't been called before) or the connection is already closed
     * @throws SQLException When there is an error during the disconnect from database
     */
    public boolean disconnect() throws IllegalStateException, SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            this.logger.debug("Tried to disconnect from a database to which no connection exists");
            throw new IllegalStateException("No connection to database established, cannot disconnect");
        }
        // this.conn.commit();
        this.conn.close();
        this.logger.debug("Closed connection to database");
        return true;
    }

    public ResultSet executeQuery(String query) throws SQLException, NullPointerException, IllegalArgumentException {
        this.setQuery(query);
        ResultSet rs =  this.executeQuery();
        this.disconnect();
        return rs;
    }

    public int executeUpdate(String query) throws SQLException {
        this.setQuery(query);
        int rs =  this.executeUpdate();
        this.disconnect();
        return rs;
    }

    public int executeUpdate() throws SQLException {
        if (this.query == null || this.query.isEmpty()) {
            this.logger.debug("Cannot execute empty query in class in Object" + this);
            throw new IllegalArgumentException("Query is either empty or null, must be initialized");
        }

        if (this.conn == null && !this.connect()) {
            this.logger.debug("Cannot connect to database");
            throw new NullPointerException("Connection is null");
        }

        int result;

        try (Statement st = this.conn.createStatement()) {
            result = st.executeUpdate(this.query);
        } catch (Exception ex) {
            this.logger.info(ex.getClass().getName() + " in " + this + ": " + ex.getMessage());
            this.logger.debug(Arrays.toString(ex.getStackTrace()));
            throw ex;
        }
        return result;
    }

    /**
     *
     * @return Result of the query
     * @throws SQLException If exception happens during update
     * @throws NullPointerException When there is no connection object for db connection or cannot connect
     * @throws IllegalArgumentException When an empty query is given
     */
    public ResultSet executeQuery() throws SQLException, NullPointerException, IllegalArgumentException {
        if (this.query == null || this.query.isEmpty()) {
            this.logger.debug("Cannot execute empty query in class in Object " + this);
            throw new IllegalArgumentException("Query is either empty or null, must be initialized");
        }

        if (this.conn == null && !this.connect()) {
            this.logger.debug("Cannot connect to database");
            throw new NullPointerException("Connection is null");
        }

        ResultSet rs;

        try (Statement st = this.conn.createStatement()) {
            this.logger.debug("Executing query " + this.query);
            rs = st.executeQuery(this.query);
        } catch (Exception ex) {
            this.logger.info(ex.getClass().getName() + " in " + this + ": " + ex.getMessage());
            this.logger.debug(Arrays.toString(ex.getStackTrace()));
            throw ex;
        }
        this.conn.close();
        return rs;
    }

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public Connection getConn() {
        return conn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBConnection that = (DBConnection) o;
        return Objects.equals(getConnString(), that.getConnString()) && Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConnString(), getUser());
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
