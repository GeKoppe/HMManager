package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.core.user.UserTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Cache for all authorisation related information
 */
public abstract class AuthCache extends Cache {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(AuthCache.class);
    /**
     * True, if {@link AuthCache#tickets} should be invalidated and refreshed.
     */
    private static boolean refreshTickets = false;
    /**
     * List of all currently issued tickets
     */
    private static final HashMap<String, UserTicket> tickets = new HashMap<>();

    /**
     * Default constructor
     */
    public AuthCache() { }
    /**
     * Adds {@param ticket} to {@link AuthCache#tickets}.
     * If the exact same object already exists in {@link AuthCache#tickets}, it is not added and false is returned.
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
}
