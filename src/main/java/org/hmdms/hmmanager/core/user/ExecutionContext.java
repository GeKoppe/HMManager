package org.hmdms.hmmanager.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ExecutionContext {
    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger(ExecutionContext.class);

    private String userId;
    private String client;
    private String ticketId;

    public ExecutionContext(String userId, String ticketId, String client) {
        this.userId = userId;
        this.ticketId = ticketId;
        this.client = client;
    }

    public ExecutionContext(String userId, String ticketId) {
        this.userId = userId;
        this.ticketId = ticketId;
    }

    public ExecutionContext(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionContext that)) return false;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getClient(), that.getClient()) && Objects.equals(getTicketId(), that.getTicketId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getClient(), getTicketId());
    }

    @Override
    public String toString() {
        return "ExecutionContext{" +
                "userId='" + userId + '\'' +
                ", client='" + client + '\'' +
                ", ticketId='" + ticketId + '\'' +
                '}';
    }
}
