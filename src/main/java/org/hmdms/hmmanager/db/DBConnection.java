package org.hmdms.hmmanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
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
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    /**
     * Database connection object
     */
    private Connection conn;
    private String query;

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
            this.connString = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.pw = props.getProperty("db.pw");
        } catch (Exception ex) {
            this.logger.debug(ex.getClass().getName() + " during instanciation of DBConnection: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Instanciates custom database connection
     * @param jdbc Connection string to databse
     * @param user User for databse connection
     * @param pw Password for database connection
     */
    public DBConnection(String jdbc, String user, String pw) {
        this.connString = jdbc;
        this.user = user;
        this.pw = pw;
    }

    /**
     * Instanciates an {@link java.sql.Connection} object in object scope
     * @return True, if connection succeded, false otherwise
     */
    public boolean connect() {
        try {
            this.conn = DriverManager.getConnection(this.connString);
            logger.debug("Connected to database");
        } catch (Exception ex) {
            logger.info(ex.getClass().getName() + " in method connect: " + ex.getMessage());
            logger.debug(Arrays.toString(ex.getStackTrace()));
            return false;
        }
        return true;
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
        this.conn.commit();
        this.conn.close();
        this.logger.debug("Closed connection to database");
        return true;
    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public ResultSet executeQuery() throws SQLException, NullPointerException, IllegalArgumentException {
        if (this.query == null || this.query.equals("")) {
            this.logger.debug("Cannot execute empty query in class in Object" + this);
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

    /**
     *
     * @param query Query to be executed
     * @return
     * @throws SQLException
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public ResultSet executeQuery(String query) throws SQLException, NullPointerException, IllegalArgumentException {
        this.setQuery(query);
        ResultSet rs;

        return this.executeQuery();
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

    /**
     * @return String representation of class instance
     */
    @Override
    public String toString() {
        return new StringBuilder().
                append("DBConnection{").
                append("connString='").
                append(connString).
                append('\'').
                append(", user='").
                append(user).
                append('\'').
                append('}').
                toString();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
