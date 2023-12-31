package org.hmdms.hmmanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

public class DBConnection {
    private String connString;
    private String user;
    private String pw;
    private final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private Connection conn;
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
}
