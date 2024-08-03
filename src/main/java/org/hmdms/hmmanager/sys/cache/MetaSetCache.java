package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.core.MetaKey;
import org.hmdms.hmmanager.core.MetaSet;
import org.hmdms.hmmanager.db.DBConnection;
import org.hmdms.hmmanager.db.DBConnectionFactory;
import org.hmdms.hmmanager.db.DBQuery;
import org.hmdms.hmmanager.db.DBQueryFactory;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;

public class MetaSetCache extends Cache {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MetaSetCache.class);
    /**
     * Cache of all {@link MetaSet} definitions
     */
    private static final ArrayList<MetaSet> metaSetCache = new ArrayList<>();

    private static final ArrayList<String> metaKeyDefinitions = new ArrayList<>();

    private static boolean cacheInitialized = false;

    public static void initCachesAsync() {
        locks.put("metasets", new ReentrantLock());
        locks.put("metakeys", new ReentrantLock());

        Thread metaSetT = new Thread(MetaSetCache::initMetaSetCache);
        metaSetT.start();

        Thread metaKeysT = new Thread(MetaSetCache::initMetaKeyDefs);
        metaKeysT.start();
    }

    private static void initMetaKeyDefs() {
        logger.debug("Initializing cache for MetaKey Definitions");
        try {
            // TODO set the correct columns and tables
            String queryString = "SELECT * FROM metakeys";

            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(queryString);

            logger.debug(String.format("Query object %s for execution on connection %s", q, conn));
            ResultSet rs = conn.execute(q);

        } catch (Exception ex) {

        }
    }

    /**
     * Initializes
     */
    private static void initMetaSetCache() {
        try {
            // TODO set the correct columns and table
            String queryString = "SELECT * FROM metasets;";

            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(queryString);

            logger.debug(String.format("Query object %s for execution on connection %s", q, conn));
            ResultSet rs = conn.execute(q);
            logger.debug("Retrieved all MetaSet definitions");

            fillMetaSetCache(rs);
        } catch (IOException | SQLException e) {
            LoggingUtils.logException(e, logger, "warn");
            throw new RuntimeException(e);
        }
        cacheInitialized = true;
    }
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
    }

    private static void fillMetaKeyNames(ResultSet rs) {

    }
}
