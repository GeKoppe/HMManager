package org.hmdms.hmmanager.sys.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

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
    private static HashMap<String, Object> dbConfig = new HashMap<>();

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
}
