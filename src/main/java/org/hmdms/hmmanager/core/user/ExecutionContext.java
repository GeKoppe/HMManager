package org.hmdms.hmmanager.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Represents a context for execution of functions within the system
 */
public class ExecutionContext {
    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger(ExecutionContext.class);
    /**
     * Id of the user executing actions on the server
     */
    private String userId;
    /**
     * Id of the client from which the context is executed
     */
    private String client;
    /**
     * Id of the ticket this user uses
     */
    private String ticketId;

    /**
     * Constructs an execution context
     * @param userId Id of the user
     * @param ticketId Id of the session ticket
     * @param client Client this session uses
     */
    public ExecutionContext(String userId, String ticketId, String client) {
        this.userId = userId;
        this.ticketId = ticketId;
        this.client = client;
    }

    /**
     * Constructs an execution context
     * @param userId Id of the user
     * @param ticketId Id of the session ticket
     */
    public ExecutionContext(String userId, String ticketId) {
        this.userId = userId;
        this.ticketId = ticketId;
    }

    /**
     * Constructs an execution context
     * @param ticketId Id of the session ticket
     */
    public ExecutionContext(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * Returns id of the user this execution context belongs to
     * @return id of the user this execution context belongs to
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets id of the user this execution context belongs to
     * @param userId id of the user this execution context belongs to
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the client from which the session is used
     * @return client from which the session is used
     */
    public String getClient() {
        return client;
    }

    /**
     * client from which the session is used
     * @param client client from which the session is used
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Gets ticket id of the session
     * @return ticket id of the session
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Sets ticket id of the session
     * @param ticketId ticket id of the session
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * {@inheritDoc}
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionContext that)) return false;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getClient(), that.getClient()) && Objects.equals(getTicketId(), that.getTicketId());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getClient(), getTicketId());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString() {
        return "ExecutionContext{" +
                "userId='" + userId + '\'' +
                ", client='" + client + '\'' +
                ", ticketId='" + ticketId + '\'' +
                '}';
    }
}
