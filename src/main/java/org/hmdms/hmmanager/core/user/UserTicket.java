package org.hmdms.hmmanager.core.user;

import org.hmdms.hmmanager.db.IFillable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
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

    public UserTicket() { }
    public UserTicket(UserTicket ticket) {
        this.ticket = ticket.getTicket();
        this.userId = ticket.getUserId();
        this.issuedAt = ticket.getIssuedAt();
        this.validThru = ticket.getValidThru();
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getValidThru() {
        return validThru;
    }

    public void setValidThru(Date validThru) {
        this.validThru = validThru;
    }

    @Override
    public String toString() {
        return "UserTicket{" +
                "ticket='" + ticket + '\'' +
                ", userId='" + userId + '\'' +
                ", issuedAt=" + issuedAt +
                ", validThru=" + validThru +
                '}';
    }

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        // TODO implement
        return false;
    }
}
