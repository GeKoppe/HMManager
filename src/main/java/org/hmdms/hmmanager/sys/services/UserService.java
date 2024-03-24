package org.hmdms.hmmanager.sys.services;

import org.hmdms.hmmanager.core.user.UserTicket;
import org.hmdms.hmmanager.core.user.UserTicketFactory;
import org.hmdms.hmmanager.db.*;
import org.hmdms.hmmanager.sys.cache.AuthCache;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * This service is used for all sorts of user actions.
 * Examples of this are authentication, user creation, user locking, user deletion etc.
 */
public abstract class UserService extends Service {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Default constructor
     */
    public UserService() { }
    /**
     * Logs the user with username {@param userName} onto the system by generating an access ticket.
     * @param userName Name of the user to login
     * @param pw Hashed password of the user to login
     * @return Ticket for the user
     * @throws SQLException When
     * @throws IOException When shit goes down
     */
    public static UserTicket login(String userName, String pw) throws SQLException, IOException {
        UserTicket ticket;
        try {
            if (!userMatchesPw(userName, pw)) {
                logger.info("Username and pw do not match, won't create a ticket");
                throw new IllegalArgumentException("Username and pw do not match");
            }
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery query = DBQueryFactory.createSelectQuery(String.format("SELECT user_id FROM user WHERE user_name = '%s'", userName));
            ResultSet rs = conn.execute(query);

            if (rs.last()) {
                if (rs.getRow() != 1) {
                    logger.debug("More than one row was fetched, cannot exactly match");
                    rs.close();
                    // TODO throw a different exception
                    throw new IllegalArgumentException("More than one user with given username present in db");
                }
            } else {
                logger.debug("No rows where fetched");
                rs.close();
                throw new IllegalArgumentException("No user with given username present in db");
            }

            rs.first();
            String userId = rs.getString(1);
            rs.close();
            ticket = createTicket(userId);
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger, "info", "%s occurred while trying to authenticate a user: %s");
            throw ex;
        }

        return ticket;
    }

    /**
     * Checks, whether the combination of user and password are correct
     * @param userName Username that is trying to authenticate themselves at the system
     * @param pw Password the user provided
     * @return True, if user and password match
     */
    public static boolean userMatchesPw(String userName, String pw) {
        boolean result = false;
        logger.debug(String.format("Checking %s password against provided pw", userName));
        try {
            // Connect to the database and create a query object
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(String.format("SELECT pw FROM user WHERE user_name = '%s'", userName));

            // Execute the query
            ResultSet rs = conn.execute(q);

            // Check, if there are 0 or more than 1 rows and return false, as something went wrong
            // TODO maybe better to throw ex here
            if (rs.last()) {
                if (rs.getRow() != 1) {
                    logger.debug("Found more than 1 row, cannot exactly match");
                    rs.close();
                    return false;
                }
            } else {
                logger.debug("No rows were fetched");
                rs.close();
                return false;
            }

            // Set cursor to first row and check, whether the value in the pw column matches the supplied pw
            rs.first();
            if (rs.getString(1).equals(pw)) result = true;
            rs.close();
            if (result) logger.debug("Given username and pw match");
            else logger.debug("Given username and pw don't match");
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger, "info");
        }
        return result;
    }

    /**
     * Creates a ticket for the user corresponding to {@param userId}.
     * @param userId Id corresponding to the user for whom a ticket should be issued
     * @return New {@link UserTicket} object for the user designated by {@param userId}
     * @throws IllegalArgumentException Thrown, when no {@param userId} is given.
     */
    private static UserTicket createTicket(String userId) throws IllegalArgumentException {
        if (userId == null || userId.isEmpty()) {
            logger.debug("No user given, cannot create ticket");
            throw new IllegalArgumentException("No userid given");
        }
        logger.debug(String.format("Creating ticket for userId %s", userId));
        try {
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery query = DBQueryFactory.createInsertQuery(
                    String.format(
                            "INSERT INTO ticket (ticket, user_id, issued_at,valid_thru) VALUES "
                            + "('%s', '%s', '%x', '%x');",
                            userId,
                            generateTicket(),
                            new Date().getTime(),
                            new Date().getTime() + 100000
                    )
            );
            conn.execute(query);
        } catch (Exception ex) {

        }
        return new UserTicket();
    }

    /**
     * Generates a new random uuid for a new ticket.
     * @return new ticket id
     */
    private static String generateTicket() {
        return UUID.randomUUID().toString();
    }

    /**
     * Checks, whether ticket {@param ticket} is still valid.
     * Calls {@link AuthCache#getTicket(String)} with {@param ticket} and checks, whether the current
     * date is after the that tickets valid_thru date.
     * @param ticket Id of the ticket to be checked
     * @return True, if ticket is still valid
     */
    public static boolean ticketValid(String ticket) {
        boolean valid = false;
        UserTicket t = AuthCache.getTicket(ticket);
        if (t.getValidThru().compareTo(new Date()) > 0) valid = true;
        return valid;
    }

    // TODO implement fully
    /**
     * Refreshes the ticket cache
     * @throws Exception Any exception thrown during the instantiation of new tickets
     */
    private static void refreshTicketCache() throws Exception {
        logger.debug("Refreshing ticket cache");
        try {
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(
                    "SELECT ticket, user_id, issued_at, valid_thru FROM ticket"
            );
            ResultSet rs = conn.execute(q);
            ArrayList<UserTicket> tickets = UserTicketFactory.createFromResultset(rs);
            rs.close();
            AuthCache.invalidateTickets();
            for (UserTicket t : tickets) {
                AuthCache.addTicket(t);
            }
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    logger,
                    "warn",
                    "%s occurred while trying to refresh ticket cache: %s"
            );
            throw ex;
        }

        logger.debug("Ticket cache refreshed");
    }
}
