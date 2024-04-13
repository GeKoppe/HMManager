package org.hmdms.hmmanager.core.user;

import org.hmdms.hmmanager.utils.LoggingUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidAlgorithmParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class UserFactory {

    private static final Logger logger = LoggerFactory.getLogger(UserFactory.class);

    /**
     * Creates an empty {@link User} object and returns it.
     * @return New empty user object
     */
    @Contract(" -> new")
    public static @NotNull User createDefaultUser() {
        return new User();
    }

    public static @NotNull ArrayList<User> createUsersFromResultSet(ResultSet rs) throws SQLException {
        if (rs == null) {
            logger.debug("No resultset given to create users from");
            throw new IllegalArgumentException("Empty parameter rs");
        }
        logger.debug("Creating users from resultset " + rs);
        ArrayList<User> users = new ArrayList<>();

        try {
            while (rs.next()) {
                User u = new User();
                u.fillFromResultSet(rs);
                users.add(u);
            }
        } catch (Exception ex) {
            LoggingUtils.logException(ex, logger);
            throw ex;
        }
        return users;
    }
}
