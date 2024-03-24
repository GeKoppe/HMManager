package org.hmdms.hmmanager.core.user;

import org.hmdms.hmmanager.db.IFillable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a ticket to access the system. A ticket always belongs to a single user.
 */
public class UserTicket implements Serializable, IFillable {
    /**
     * Id of the ticket
     */
    private String ticket;
    /**
     * Owner of the ticket
     */
    private String userId;
    /**
     * Date at which the ticket was issued
     */
    private Date issuedAt;
    /**
     * Date at which the ticket will expire
     */
    private Date validThru;

    /**
     * Creates an empty UserTicket object
     */
    public UserTicket() { }

    /**
     * Returns the ticket number of this ticket
     * @return ticket number of this ticket
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * Sets ticket number for this object
     * @param ticket ticket number of this ticket
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * Gets the userId, this ticket was issued to
     * @return userId, this ticket was issued to
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the userId, this ticket was issued to
     * @param userId userId, this ticket was issued to
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the date, when this ticket was issued
     * @return date, when this ticket was issued
     */
    public Date getIssuedAt() {
        return issuedAt;
    }

    /**
     * Sets the date, when this ticket was issued
     * @param issuedAt date, when this ticket was issued
     */
    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * Gets the date until this ticket loses it's validity
     * @return date until this ticket loses it's validity
     */
    public Date getValidThru() {
        return validThru;
    }

    /**
     * Sets the date until this ticket loses it's validity
     * @param validThru date until this ticket loses it's validity
     */
    public void setValidThru(Date validThru) {
        this.validThru = validThru;
    }

    /**
     * Creates a string representation of this object
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "UserTicket{" +
                "ticket='" + ticket + '\'' +
                ", userId='" + userId + '\'' +
                ", issuedAt=" + issuedAt +
                ", validThru=" + validThru +
                '}';
    }

    /**
     * {@inheritDoc}
     * In this case the required columns are: ticket (String), user_id (String), issued_at {@link Date} and
     * valid_thru {@link Date}.
     * @param rs ResultSet from which to fill the IFillable
     * @return True, if filling worked properly
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        // TODO implement
        return false;
    }

    /**
     * {@inheritDoc}
     * @param o Object to be checked for equality
     * @return True, if all class fields are the same in both this object and {@param o}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTicket that)) return false;
        return Objects.equals(getTicket(), that.getTicket()) && Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getIssuedAt(), that.getIssuedAt()) && Objects.equals(getValidThru(), that.getValidThru());
    }

    /**
     * {@inheritDoc}
     * @return Hashcode for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTicket(), getUserId(), getIssuedAt(), getValidThru());
    }
}
