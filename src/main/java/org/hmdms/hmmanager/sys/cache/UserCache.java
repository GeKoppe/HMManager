package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.core.user.User;
import org.hmdms.hmmanager.core.user.UserTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Cache for all authorisation related information
 */
public abstract class UserCache extends Cache {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(UserCache.class);
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
    private static final HashMap<String, User> users = new HashMap<>();

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
        for (String key : tickets.keySet()) {
            // Implement
        }
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
        for (String id : users.keySet()) {
            if (users.get(id).getUserName().equals(name)) {
                result = true;
                break;
            }
        }
        if (result) logger.debug(String.format("User with name '%s' does exist", name));
        else logger.debug(String.format("User with name '%s' does not exist", name));
        return result;
    }

    public static void initCache() {

    }
}
