package org.hmdms.hmmanager.core.user;

import org.hmdms.hmmanager.db.IFillable;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;
import java.util.HashMap;

/**
 * Class that represents a user in the system
 */
public class User implements Serializable, IFillable {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(User.class);
    /**
     * Name of the user
     */
    private String userName;
    /**
     * ID of the user
     */
    private String id;
    /**
     * Date, at which the user was created
     */
    private Date createdAt;
    /**
     * True, if the user is locked from the system
     */
    private boolean locked;

    public User() {

    }


    /**
     * {@inheritDoc}
     * In this case, the necessary columns are:
     * user_name (String), user_id (String), locked (boolean), created_at ({@link Date})
     * @param rs
     * @return
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        boolean result = false;

        this.logger.debug("Filling object from resultset");
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            HashMap<String, Integer> columns = new HashMap<>();

            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                columns.put(rsmd.getColumnLabel(i) != null ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i), i);
            }

            boolean allColumnsExist = columns.containsKey("user_name")
                    && columns.containsKey("user_id")
                    && columns.containsKey("locked")
                    && columns.containsKey("created_at");

            if (!allColumnsExist) {
                this.logger.debug("Missing columns for filling");
                return result;
            }


            this.setUserName(rs.getString(columns.get("user_name")));
            this.setId(rs.getString(columns.get("user_id")));
            this.setLocked(rs.getBoolean(columns.get("locked")));
            this.setCreatedAt(rs.getDate(columns.get("created_at")));

            result = true;
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
        }

        return result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
