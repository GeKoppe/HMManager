package org.hmdms.hmmanager.DBComm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class DBConnection {
    private String connString;
    private String user;
    private String pw;
    private final Logger logger = LoggerFactory.getLogger(DBConnection.class);
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
}
