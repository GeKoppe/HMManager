package org.hmdms.hmmanager.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Factory class for creating user tickets
 */
public abstract class UserTicketFactory {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(UserTicketFactory.class);

    /**
     * Default constructor
     */
    public UserTicketFactory() { }

    /**
     * Creates an empty {@link UserTicket} object
     * @return A new, empty {@link UserTicket} object
     */
    public static UserTicket createDefaultTicket() {
        return new UserTicket();
    }

    /**
     * Creates a list of {@link UserTicket} objects from a {@link ResultSet}.
     * Iterates through the entire ResultSet {@param rs}. For each result, a new {@link UserTicket} object
     * is created and it's {@link UserTicket#fillFromResultSet(ResultSet)} method is called with {@param rs}.
     * Returns the list.
     * @param rs ResultSet from an SQL Select statement.
     * @return List of all instantiated {@link UserTicket} objects
     * @throws SQLException When something goes wrong during reading of {@param rs}.
     */
    public static ArrayList<UserTicket> createFromResultset(ResultSet rs) throws SQLException {
        logger.debug("Instantiating usertickets from ResultSet");
        ArrayList<UserTicket> tickets = new ArrayList<>();
        while (rs.next()) {
            UserTicket t = createDefaultTicket();
            t.fillFromResultSet(rs);
            tickets.add(t);
        }

        return tickets;
    }
}
