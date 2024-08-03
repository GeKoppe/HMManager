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
import java.util.Objects;

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

    /**
     * Constructs an empty User object.
     */
    public User() {

    }

    /**
     * Constructor that instantiates every class field with a value
     * @param userName Name of the user
     * @param id id of the user
     * @param createdAt Date of user creation
     * @param locked True, if the user is locked from the system
     */
    public User(String userName, String id, Date createdAt, boolean locked) {
        this.userName = userName;
        this.id = id;
        this.createdAt = createdAt;
        this.locked = locked;
    }


    /**
     * {@inheritDoc}
     * In this case, the necessary columns are:
     * user_name (String), user_id (String), locked (boolean), created_at ({@link Date})
     * @param rs ResultSet from which to fill the object.
     *           Cursor of the resultset must be on the row, with which the user should be filled
     * @return True, if filling was successful, false otherwise
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        boolean result = false;

        this.logger.debug("Filling object from resultset");
        try {
            // Get metadata from the resultset and read the column names / labels
            ResultSetMetaData rsmd = rs.getMetaData();
            HashMap<String, Integer> columns = new HashMap<>();

            // Put column names in a hashmap with their respective column numbers be able to access them easier
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columns.put(rsmd.getColumnLabel(i) != null ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i), i);
            }

            // Check, if all necessary columns are existent in the resultset
            boolean allColumnsExist = columns.containsKey("user_name")
                    && columns.containsKey("user_id")
                    && columns.containsKey("locked");

            // If a column is missing, return false
            if (!allColumnsExist) {
                this.logger.debug("Missing columns for filling");
                return result;
            }

            // Set class value from resultset
            this.setUserName(rs.getString(columns.get("user_name")));
            this.setId(rs.getString(columns.get("user_id")));
            this.setLocked(rs.getBoolean(columns.get("locked")));
            this.setCreatedAt(rs.getDate(columns.get("created_at")));

            // Set result to true to show, that the filling worked
            result = true;
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return false;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * @param o Object to be checked for equality to this object
     * @return True, if the objects are the same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return isLocked() == user.isLocked() && Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getId(), user.getId()) && Objects.equals(getCreatedAt(), user.getCreatedAt());
    }

    /**
     * {@inheritDoc}
     * @return hashcode for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getId(), getCreatedAt(), isLocked());
    }

    /**
     * Returns username of the user
     * @return Returns username of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets username of this user
     * @param userName username of this user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets id of this user
     * @return id of this user
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id of this user
     * @param id id of this user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets date of user creation
     * @return date of user creation
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets date of user creation
     * @param createdAt date of user creation
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Shows, whether user is locked from the system
     * @return true if user is locked from the system
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets, whether user is locked from the system
     * @param locked True, if user is locked from the system
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Creates a string representation of this object
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", locked=" + locked +
                '}';
    }

    /**
     * Creates a new instance of this class with the same values in class fields
     * @return new instance of this class with the same values in class fields
     */
    @Override
    public User clone() {
        User u = new User();
        u.setId(this.id);
        u.setLocked(this.locked);
        u.setUserName(this.userName);
        u.setCreatedAt(this.createdAt);
        return u;
    }
}
