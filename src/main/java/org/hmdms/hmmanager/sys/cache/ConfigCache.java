package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.sys.exceptions.system.CachingException;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used for caching system config parameters
 */
public abstract class ConfigCache extends Cache {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ConfigCache.class);

    /**
     * Configuration for connecting to the systems database
     */
    private static final HashMap<String, Object> dbConfig = new HashMap<>();

    /**
     * System configuration
     */
    private static final HashMap<String, Object> sysConfig = new HashMap<>();

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
        if (!tryToAcquireLock("db")) {
            logger.info("Cannot acquire lock on db config");
            throw new IllegalStateException("Lock still occupied");
        }

        if (!dbConfig.containsKey(prop)) throw new IllegalArgumentException("DBConfig does not contain key " + prop);

        Object val = dbConfig.get(prop);
        unlock("db");

        return val;
    }

    /**
     * Gets property from system config cached in {@link ConfigCache#sysConfig}.
     * @param prop Property name
     * @return Value associated with property name {@param prop}
     */
    public static Object getSysConfigProperty(String prop) {
        if (prop == null || prop.isEmpty()) throw new IllegalArgumentException("No property given");
        if (!tryToAcquireLock("sys")) {
            logger.info("Cannot acquire lock on system config");
            throw new IllegalStateException("Lock still occupied");
        }

        if (!sysConfig.containsKey(prop)) {
            logger.info(String.format("System config does not contain key %s", prop));
            throw new IllegalArgumentException("System config does not contain key" + prop);
        }
        Object val = sysConfig.get(prop);

        unlock("sys");
        return val;
    }

    /**
     * Reads the db_config.properties file and saves it's properties in the class field {@link ConfigCache#dbConfig}
     * to let it be more accessible.
     * @throws IOException Is thrown, when reading the file failed for some reason.
     * @throws CachingException Is thrown, when the lock on the db config object could not be acquired
     */
    private static boolean loadDbConfig() throws IOException, CachingException {
        if (!tryToAcquireLock("db")) {
            logger.warn("Could not acquire lock on sysConfig");
            throw new CachingException("Could not acquire lock on sysConfig");
        }
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
                    "%s exception occurred while reading the database configuration from db_config.properties: %s"
            );
            unlock("db");
            throw ex;
        }
        unlock("db");
        return true;
    }

    /**
     * Caches the systems config by reading the config.properties file and saving it in
     * {@link ConfigCache#sysConfig}.
     * @throws IOException Thrown when the config.properties file could not be read
     * @throws CachingException Thrown, when something went wrong during caching
     */
    private static boolean loadSysConfig() throws IOException, CachingException {
        if (!tryToAcquireLock("sys")) {
            logger.warn("Could not acquire lock on sysConfig");
            throw new CachingException("Could not acquire lock on sysConfig");
        }
        sysConfig.clear();
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = ConfigCache.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        logger.debug("Read the configuration file");

        try {
            sysConfig.put("msg.scaling.brokers", Integer.parseInt((String) prop.get("msg.scaling.brokers")));
            sysConfig.put("msg.scaling.brokers.autoScaling", Boolean.parseBoolean((String) prop.get("msg.scaling.brokers.autoScaling")));
            sysConfig.put("msg.timeout", Integer.parseInt((String) prop.get("msg.timeout")));
            sysConfig.put("mq.host", (String) prop.get("mq.host"));
            sysConfig.put("mq.hmmanager.queue.name", (String) prop.get("mq.hmmanager.queue.name"));

            logger.debug("Put all config entries into the cache");
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    logger,
                    "warn",
                    "%s exception occurred while reading the system configuration from config.properties: %s"
            );
            unlock("sys");
            throw ex;
        }
        unlock("sys");
        logger.debug("Successfully loaded system config into cache");
        return true;
    }

    /**
     * Loads both the system and the db config, after initializing reentrant locks for both.
     * Callbacks to {@link ConfigCache#loadDbConfig()} and {@link ConfigCache#loadSysConfig()}
     * @throws IOException Thrown when one of the configuration files could not be read
     * @throws CachingException Thrown when something went wrong while caching
     */
    public static void initCaches() throws IOException, CachingException {
        initLocks();
        loadSysConfig();
        loadDbConfig();
    }

    public static @NotNull Future<Boolean> initCachesAsync() {
        initLocks();

        return ex.submit(() -> {
            Future<Boolean> sys = ex.submit(ConfigCache::loadSysConfig);
            Future<Boolean> db = ex.submit(ConfigCache::loadDbConfig);
            sys.wait();
            db.wait();
            return sys.get() && db.get();
        });
    }

    private static void initLocks() {
        locks.put("sys", new ReentrantLock());
        locks.put("db",  new ReentrantLock());
    }
}
