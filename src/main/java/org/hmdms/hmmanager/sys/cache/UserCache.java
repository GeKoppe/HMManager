package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.core.user.User;
import org.hmdms.hmmanager.core.user.UserFactory;
import org.hmdms.hmmanager.core.user.UserTicket;
import org.hmdms.hmmanager.core.user.UserTicketFactory;
import org.hmdms.hmmanager.db.DBConnection;
import org.hmdms.hmmanager.db.DBConnectionFactory;
import org.hmdms.hmmanager.db.DBQuery;
import org.hmdms.hmmanager.db.DBQueryFactory;
import org.hmdms.hmmanager.sys.exceptions.system.CachingException;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cache for all authorisation related information
 */
public abstract class UserCache extends Cache {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(UserCache.class);
    /**
     * True, if {@link UserCache#tickets} should be invalidated and refreshed.
     */
    private static boolean refreshTickets = false;
    /**
     * List of all currently issued tickets
     */
    private static final HashMap<String, UserTicket> tickets = new HashMap<>();

    /**
     * Map of all users that exist in the system. Key is user id, value is the corresponding UserObject
     */
    private static final HashMap<Integer, User> users = new HashMap<>();

    /**
     * Default constructor
     */
    public UserCache() { }
    /**
     * Adds {@param ticket} to {@link UserCache#tickets}.
     * If the exact same object already exists in {@link UserCache#tickets}, it is not added and false is returned.
     * If the ticket number already exists, the corresponding {@link UserTicket} object is removed.
     * New ticket is added at the end
     * @param ticket Ticket to be added to the cache
     * @return True, if the ticket could be added to cache
     */
    public static boolean addTicket(UserTicket ticket) {
        if (tickets.containsValue(ticket)) {
            logger.debug("Ticket still in cache, not adding");
            return false;
        }
        if (tickets.containsKey(ticket.getTicket())) {
            logger.debug("Ticket id " + ticket.getTicket() + " already in cache. As tickets are not identical, old one "
                + "is removed, new one is added"
            );
            tickets.remove(ticket.getTicket());
        }
        tickets.put(ticket.getTicket(), ticket);
        return true;
    }

    /**
     * Returns {@link UserTicket} object designated by {@param ticket} from the ticket cache or null, if no
     * ticket corresponds to {@param ticket}.
     * @param ticket Id of the ticket to be retrieved
     * @return {@link UserTicket} object corresponding to the id {@param ticket}.
     */
    public static UserTicket getTicket(String ticket) {
        return tickets.get(ticket);
    }

    // TODO implement fully
    /**
     * Invalidates all tickets, that are not valid anymore
     * @return True, if invalidation worked
     */
    public static boolean invalidateTickets() {
        if (!tryToAcquireLock("tickets")) {
            logger.info("Could not acquire lock on tickets");
            return false;
        }
        ArrayList<UserTicket> toRemove = new ArrayList<>();
        for (String key : tickets.keySet()) {
            // TODO check if ticket is still valid
        }
        unlock("tickets");
        return true;
    }

    /**
     * Iterates through the entire user cache and checks, whether any of the user objects has the username given
     * in {@param name}.
     * @param name Username to be checked for existence
     * @return True, if username exists in the cache, false otherwise
     * @throws IllegalArgumentException When no or an empty {@param name} is given
     */
    public static boolean userExists(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            logger.debug("No username given");
            throw new IllegalArgumentException("No username given");
        }
        boolean result = false;
        for (Integer id : users.keySet()) {
            if (users.get(id).getUserName().equals(name)) {
                result = true;
                break;
            }
        }
        if (result) logger.debug(String.format("User with name '%s' does exist", name));
        else logger.debug(String.format("User with name '%s' does not exist", name));
        return result;
    }

    /**
     * Initializes {@link UserCache#users} by first calling {@link UserCache#initLocks()} and then
     * calling {@link UserCache#cacheUsers()}.
     * @throws CachingException If something went wrong
     */
    public static void initCache() throws CachingException {
        logger.debug("Initializing UserCache");

        initLocks();
        cacheUsers();
        logger.debug("UserCache successfully initialized");
    }

    /**
     * Initializes user cache asynchronously by submitting {@link UserCache#cacheUsers()} to
     * {@link UserCache#ex}
     * @return
     */
    public static @NotNull Future<Boolean> initCachesAsync() {
        initLocks();

        return ex.submit(() -> {
            Future<Boolean> users = ex.submit(UserCache::cacheUsers);

            try {
                while (!users.isDone()) {
                    logger.trace("Loading configs...");
                    //noinspection BusyWait
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                LoggingUtils.logException(e, logger);
                throw new RuntimeException(e);
            }
            return users.get();
        });
    }

    /**
     * Initializes all {@link ReentrantLock} objects in {@link Cache#locks}
     */
    private static void initLocks() {
        locks.put("users", new ReentrantLock());
        locks.put("tickets", new ReentrantLock());
    }

    /**
     * Retrieves all users from the database and instantiates user objects from the {@link ResultSet}
     * by calling {@link UserFactory#createUsersFromResultSet(ResultSet)}.
     * @return True, if caching worked, false if the object lock could not be retrieved
     * @throws CachingException Thrown whenever an exception occurs during caching
     */
    private static boolean cacheUsers() throws CachingException {
        logger.debug("Starting to cache users");
        if (!tryToAcquireLock("users")) {
            logger.info("Could not acquire lock on users");
            throw new CachingException("Could not acquire lock on users");
        }
        try {
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery("SELECT * FROM users");

            logger.debug("Instantiated query and connection to database for caching tickets");
            ResultSet rs = conn.execute(q);
            logger.debug("Retrieved tickets from database");
            ArrayList<User> usr = UserFactory.createUsersFromResultSet(rs);
            for (User u : usr) {
                users.put(u.getId(), u);
            }
            logger.debug("Added all users in db to cache");
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            unlock("users");
            throw new CachingException(ex, "User cache could not be initialized");
        }
        unlock("users");
        return true;
    }

    /**
     * Retrieves all tickets from the database and instantiates {@link UserTicket} objects from the {@link ResultSet}
     * by calling {@link UserTicketFactory#createFromResultSet(ResultSet)}.
     * @return True, if caching worked, false if the object lock could not be retrieved
     * @throws CachingException Thrown whenever an exception occurs during caching
     */
    private static boolean cacheTickets() throws CachingException {
        logger.debug("Caching tickets");
        if (!tryToAcquireLock("tickets")) {
            logger.debug("Could not acquire lock for tickets");
            return false;
        }
        try {
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery("SELECT * FROM ticket");

            logger.debug("Instantiated query and connection to database for caching tickets");
            ResultSet rs = conn.execute(q);
            logger.debug("Retrieved tickets from database");

            ArrayList<UserTicket> tck = UserTicketFactory.createFromResultSet(rs);
            for (var ticket : tck) {
                tickets.put(ticket.getTicket(), ticket);
            }
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            unlock("tickets");
            throw new CachingException(ex, "Cache could not be initialized");
        }
        unlock("tickets");
        return true;
    }

    /**
     * Looks up the name or id of the user in {@link UserCache#users}. Treats {@param nameOrId} as a name, if
     * {@param id} is false, as an id otherwise
     * @param nameOrId Name or id of the user that the cache should return
     * @param id True, if this method is called with an id in {@param nameOrId}, false if it is called with an id
     * @return User object with given name or id or null, if no such user exists
     * @throws IllegalArgumentException If {@param nameOrId} is empty
     * @throws IllegalStateException If the cache could not acquire the lock on the users object
     */
    public static User getUser(String nameOrId, boolean id) throws IllegalArgumentException, IllegalStateException {
        if (!tryToAcquireLock("users")) {
            logger.info("Could not acquire lock on users");
            throw new IllegalStateException("Could not acquire lock on users");
        }
        if (nameOrId == null || nameOrId.isEmpty()) {
            logger.info("No name or id given in call to method getUser()");
            unlock("users");
            throw new IllegalArgumentException("No name or id given in call to method getUser()");
        }

        User user = null;
        if (id) {
            user = users.get(nameOrId);
        } else {
            for (Integer uid : users.keySet()) {
                if (users.get(uid).getUserName().equals(nameOrId)) {
                    user = users.get(uid);
                    break;
                }
            }
        }

        if (user == null) logger.debug(String.format("User with name or id %s does not exist", nameOrId));
        else logger.debug(String.format("Found user for name or id %s", nameOrId));

        unlock("users");
        return user;
    }
}
