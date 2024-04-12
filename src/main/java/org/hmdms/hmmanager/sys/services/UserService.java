package org.hmdms.hmmanager.sys.services;

import org.hmdms.hmmanager.core.user.User;
import org.hmdms.hmmanager.core.user.UserTicket;
import org.hmdms.hmmanager.core.user.UserTicketFactory;
import org.hmdms.hmmanager.db.*;
import org.hmdms.hmmanager.sys.cache.UserCache;
import org.hmdms.hmmanager.sys.exceptions.auth.UserNotFoundException;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
    public static UserTicket login(String userName, String pw) throws SQLException, IOException, UserNotFoundException {
        UserTicket ticket;
        try {
            if (!userMatchesPw(userName, pw)) {
                logger.info("Username and pw do not match, won't create a ticket");
                throw new IllegalArgumentException("Username and pw do not match");
            }
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery query = DBQueryFactory.createSelectQuery(String.format("SELECT user_id FROM users WHERE user_name = '%s'", userName));
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
                throw new UserNotFoundException("No user with given username present in db");
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
    public static boolean userMatchesPw(String userName, String pw) throws UserNotFoundException, SQLException, IOException {
        boolean result = false;
        logger.debug(String.format("Checking %s password against provided pw", userName));
        try {
            // Connect to the database and create a query object
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(String.format("SELECT pw FROM users WHERE user_name = '%s'", userName));

            // Execute the query
            ResultSet rs = conn.execute(q);

            // Check, if there are 0 or more than 1 rows and return false, as something went wrong
            if (rs.last()) {
                if (rs.getRow() != 1) {
                    logger.debug("Found more than 1 row, cannot exactly match");
                    rs.close();
                    return false;
                }
            } else {
                logger.debug("No rows were fetched");
                rs.close();
                throw new UserNotFoundException(String.format("User with name '%s' does not exist", userName));
            }

            // Set cursor to first row and check, whether the value in the pw column matches the supplied pw
            rs.first();
            if (rs.getString(1).equals(pw)) result = true;
            rs.close();
            if (result) logger.debug("Given username and pw match");
            else logger.debug("Given username and pw don't match");
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger, "info");
            throw ex;
        }
        return result;
    }

    public static User addUser(String userName, String pw) throws IllegalArgumentException {
        if (userName == null || userName.isEmpty()) {
            logger.debug("No username given");
            throw new IllegalArgumentException("No username given");
        }

        if (pw == null || pw.isEmpty()) {
            logger.debug("No password given");
            throw new IllegalArgumentException("No password given");
        }


    }

    private static boolean userExists(String userName) throws IllegalArgumentException {
        if (userName == null || userName.isEmpty()) {
            logger.debug("No username given");
            throw new IllegalArgumentException("No username given");
        }

    }

    private static boolean userExists(@NotNull User user) throws IllegalArgumentException {
        return userExists(user.getUserName());
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
        Date nowDate;
        Date expiryDate;
        String ticketId = generateTicket();
        try {
            nowDate = new Date();
            expiryDate = new Date();
            expiryDate.setTime(nowDate.getTime() + 100000);
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            // TODO generalise simepledateformat
            DBQuery query = DBQueryFactory.createInsertQuery(
                    String.format(
                            "INSERT INTO tickets (ticket, user_id, issued_at,valid_thru) VALUES "
                            + "('%s', '%s', '%s', '%s');",
                            ticketId,
                            userId,
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiryDate)
                    )
            );
            conn.execute(query);
        } catch (Exception ex) {
            return null;
        }
        UserTicket ticket = UserTicketFactory.createDefaultTicket();
        ticket.setUserId(userId);
        ticket.setTicket(ticketId);
        ticket.setIssuedAt(nowDate);
        ticket.setValidThru(expiryDate);

        return ticket;
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
     * Calls {@link UserCache#getTicket(String)} with {@param ticket} and checks, whether the current
     * date is after the that tickets valid_thru date.
     * @param ticket Id of the ticket to be checked
     * @return True, if ticket is still valid
     */
    public static boolean ticketValid(String ticket) {
        boolean valid = false;
        UserTicket t = UserCache.getTicket(ticket);
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
                    "SELECT ticket, user_id, issued_at, valid_thru FROM tickets"
            );
            ResultSet rs = conn.execute(q);
            ArrayList<UserTicket> tickets = UserTicketFactory.createFromResultset(rs);
            rs.close();
            UserCache.invalidateTickets();
            for (UserTicket t : tickets) {
                UserCache.addTicket(t);
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
