package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.core.MetaKey;
import org.hmdms.hmmanager.core.MetaSet;
import org.hmdms.hmmanager.db.DBConnection;
import org.hmdms.hmmanager.db.DBConnectionFactory;
import org.hmdms.hmmanager.db.DBQuery;
import org.hmdms.hmmanager.db.DBQueryFactory;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cache for all definitions of {@link MetaSet} and {@link MetaKey}
 */
public class MetaSetCache extends Cache {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MetaSetCache.class);
    /**
     * Cache of all {@link MetaSet} definitions in order of the value returned by {@link MetaSet#getId()}-
     */
    private static final ArrayList<MetaSet> metaSetCache = new ArrayList<>();

    /**
     * Cache of all {@link MetaKey} definitions in order of the value returned by {@link MetaKey#getId()}-
     */
    private static final ArrayList<MetaKey> metaKeyDefinitions = new ArrayList<>();

    /**
     * Will initialize all caches asynchronously.
     * First adds {@link ReentrantLock} objects for metasets and metakeys in the {@link Cache#locks}
     * property. Afterward submits a task to {@link Cache#ex}, which itself will submit
     * {@link MetaSetCache#initMetaSetCache()} and {@link MetaSetCache#initMetaKeyDefs()} to {@link Cache#ex}.
     * Those tasks are awaited during the first task, of which the {@link Future} is returned.
     * @return Result of the tasks, which monitors initialization of caches
     */
    public static @NotNull Future<Boolean> initCachesAsync() {
        locks.put("metasets", new ReentrantLock());
        locks.put("metakeys", new ReentrantLock());

        return ex.submit(() -> {
            boolean success = false;

            // Submit both initialization of metakeydefs and metasetdefs and wait for them
            Future<Boolean> mk = ex.submit(MetaSetCache::initMetaKeyDefs);
            Future<Boolean> ms = ex.submit(MetaSetCache::initMetaSetCache);
            mk.wait();
            ms.wait();

            // If both tasks are finished, set cache as initialized and return true
            success = mk.isDone() && ms.isDone();

            cacheInitialized = success;
            return success;
        });
    }

    /**
     * Initializes {@link MetaSetCache#metaKeyDefinitions} by first querying the database for all
     * information and afterward calling {@link MetaSetCache#fillMetaKeys(ResultSet)} with the result
     * of the sql query.
     * @return True, if caches could be initialized, false otherwise
     */
    private static boolean initMetaKeyDefs() {
        logger.debug("Initializing cache for MetaKey Definitions");
        try {
            // TODO set the correct columns and tables
            String queryString = "SELECT * FROM metakeys";

            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(queryString);

            logger.debug(String.format("Query object %s for execution on connection %s", q, conn));
            ResultSet rs = conn.execute(q);

            fillMetaKeys(rs);
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            return false;
        }
        return true;
    }

    /**
     * Initializes {@link MetaSetCache#metaSetCache} by first querying the database for all
     * information and afterward calling {@link MetaSetCache#fillMetaSetCache(ResultSet)} with the result
     * of the sql query.
     * @return True, if caches could be initialized, false otherwise
     */
    private static boolean initMetaSetCache() {
        try {
            // TODO set the correct columns and table
            String queryString = "SELECT * FROM metasets;";

            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(queryString);

            logger.debug(String.format("Query object %s for execution on connection %s", q, conn));
            ResultSet rs = conn.execute(q);
            logger.debug("Retrieved all MetaSet definitions");

            fillMetaSetCache(rs);
        } catch (SQLException e) {
            LoggingUtils.logException(e, logger, "warn");
            return false;
        }
        return true;
    }

    /**
     * Iterates through the given {@link ResultSet} rs. Instantiates a {@link MetaSet} object for each result,
     * calls it's {@link MetaSet#fillFromResultSet(ResultSet)} with the current pointer of the result set iteration.
     * Afterward sorts the resulting list by comparing values of {@link MetaSet#getId()} and
     * adds all objects in that order to {@link MetaSetCache#metaSetCache}.
     * @param rs Result of querying the database for all MetaSets
     * @throws SQLException Thrown, when iterating through rs yields an exception
     */
    private static void fillMetaSetCache(ResultSet rs) throws SQLException {
        ArrayList<MetaSet> mSets = new ArrayList<>();
        try {
            while (rs.next()) {
                MetaSet m = new MetaSet();
                m.fillFromResultSet(rs);
                mSets.add(m);
                logger.debug(String.format("Found MetaSet %s", m));
            }
        } catch (Exception e) {
            LoggingUtils.logException(e, logger, "warn");
            throw e;
        }

        // This has been refactored by IntelliJ itself more often than I can count
        mSets.sort(Comparator.comparingInt(MetaSet::getId));

        if (!tryToAcquireLock("metasets")) {
            logger.warn("Could not acquire lock for id metasets");
            unlock("metasets");
            throw new IllegalStateException("Lock metasets is currently locked");
        }
        metaSetCache.clear();
        metaSetCache.addAll(mSets);
        unlock("metasets");
    }

    /**
     * Iterates through the given {@link ResultSet} rs. Instantiates a {@link MetaKey} object for each result,
     * calls it's {@link MetaKey#fillFromResultSet(ResultSet)} with the current pointer of the result set iteration.
     * Afterward sorts the resulting list by comparing values of {@link MetaKey#getId()} and
     * adds all objects in that order to {@link MetaSetCache#metaKeyDefinitions}.
     * @param rs Result of querying the database for all MetaSets
     * @throws SQLException Thrown, when iterating through rs yields an exception
     */
    private static void fillMetaKeys(ResultSet rs) throws SQLException {
        ArrayList<MetaKey> mSets = new ArrayList<>();
        try {
            while (rs.next()) {
                MetaKey m = new MetaKey();
                m.fillFromResultSet(rs);
                mSets.add(m);
                logger.debug(String.format("Found MetaSet %s", m));
            }
        } catch (Exception e) {
            LoggingUtils.logException(e, logger, "warn");
            throw e;
        }

        // This has been refactored by IntelliJ itself more often than I can count
        mSets.sort(Comparator.comparingInt(MetaKey::getId));

        if (!tryToAcquireLock("metakeys")) {
            logger.warn("Could not acquire lock for id metakey");
            unlock("metakeys");
            throw new IllegalStateException("Lock metakeys is currently locked");
        }
        metaKeyDefinitions.clear();
        metaKeyDefinitions.addAll(mSets);
        unlock("metakeys");
    }

    /**
     * Returns the {@link MetaSet} with given id.
     * @param id ID of the MetaSet definition.
     * @return MetaSet with given ID
     */
    public static MetaSet getMetaSetById(int id) {
        if (id < 1) {
            logger.info(String.format("Tried to query MetaSet from cache with id %s", id));
            throw new IllegalArgumentException("Invalid MetaSet id ");
        }
        return metaSetCache.get(id);
    }
}
