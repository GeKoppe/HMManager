package org.hmdms.hmmanager.sys.services;

import org.hmdms.hmmanager.core.user.UserTicket;
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

public abstract class AuthService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Logs the user with username {@param userName} onto the system by generating an access ticket.
     * @param userName Name of the user to login
     * @param pw Hashed password of the user to login
     * @throws SQLException
     * @throws IOException
     */
    public static UserTicket login(String userName, String pw) throws SQLException, IOException {
        UserTicket ticket = null;
        try {
            if (!userMatchesPw(userName, pw)) return null;
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
     *
     * @param userName
     * @param pw
     * @return
     */
    public static boolean userMatchesPw(String userName, String pw) {
        boolean result = false;
        logger.debug(String.format("Checking %s password against provided pw", userName));
        try {
            DBConnection conn = DBConnectionFactory.newDefaultConnection();
            DBQuery q = DBQueryFactory.createSelectQuery(String.format("SELECT pw FROM user WHERE user_name = '%s'", userName));
            ResultSet rs = conn.execute(q);
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
            rs.first();
            if (rs.getString(1).equals(pw)) result = true;
            rs.close();
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger, "info");
        }
        return result;
    }

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
                            "INSERT INTO ticket (ticket, user_id, issued_at,valid_thru) VALUES"
                    )
            );
        } catch (Exception ex) {

        }
        return new UserTicket();
    }

    private static String generateTicket(String userId) {
        return UUID.randomUUID().toString();
    }

    public static boolean ticketValid(String ticket) {
        boolean valid = false;
        ArrayList<UserTicket> tickets = AuthCache.getTickets();
        for (UserTicket t : tickets) {
            if (t.getTicket().equals(ticket)) {
                if (t.getValidThru().compareTo(new Date()) > 0) valid = true;
            }
        }
        return valid;
    }
}
