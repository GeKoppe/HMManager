package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Used for caching system config parameters
 */
public abstract class ConfigCache extends Cache {
    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(ConfigCache.class);

    /**
     * Configuration for connecting to the systems database
     */
    private static final HashMap<String, Object> dbConfig = new HashMap<>();

    /**
     * default Constructor
     */
    public ConfigCache() { }
    /**
     * Returns a value from the db configuration set in db_config.properties
     * @param prop Prop to be retrieved
     * @return Value of the property
     */
    public static Object getDbConfigProperty(String prop) {
        if (prop == null || prop.isEmpty()) throw new IllegalArgumentException("No property given");
        if (!dbConfig.containsKey(prop)) throw new IllegalArgumentException("DBConfig does not contain key " + prop);

        return dbConfig.get(prop);
    }

    /**
     * Reads the db_config.properties file and saves it's properties in the class field {@link ConfigCache#dbConfig}
     * to let it be more accessible.
     * @throws IOException Is thrown, when reading the file failed for some reason.
     */
    public static void loadDbConfig() throws IOException {
        dbConfig.clear();
        Properties prop = new Properties();
        String propFileName = "db_config.properties";
        InputStream inputStream = ConfigCache.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        logger.info("Read the configuration file");
        try {
            dbConfig.put("username", prop.get("username").toString());
            // TODO check if password is hashed and do something with it
            dbConfig.put("password", prop.get("password").toString());
            dbConfig.put("url", prop.get("url").toString());
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    logger,
                    "warn",
                    "%s exception occured while reading the database configuration from db_config.properties: %s"
            );
            throw ex;
        }
    }
}
