package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Class that represents a message in the system
 */
public class MessageInfo {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MessageInfo.class);
    /**
     * ID of the message. {@link Broker} uses this to identify message
     */
    private String uuid;
    /**
     * Date at which the message was received
     */
    private Date received;
    /**
     * Where the message came from
     */
    private String from;
    /**
     * Information in the message
     */
    private HashMap<String,String> information;
    /**
     * Shows, whether the message was collected by a subscriber. Used by {@link Broker} class.
     */
    private boolean collected;
    /**
     * Date when the message was collected
     */
    private Date collectionDate;

    /**
     * Default Constructor
     */
    public MessageInfo() {
        this.logger.debug("Empty MessageInfo Object");

        this.uuid = UUID.randomUUID().toString();
        this.received = new Date();

        this.from = "";
        this.information = new HashMap<>();
        this.collected = false;
    }

    /**
     * Constructor for the MessageInfo object
     * @param from Sender of the message
     * @param information Information that is transported by the message
     */
    public MessageInfo(String from, HashMap<String, String> information) {
        if (from == null || from.isEmpty()) throw new IllegalArgumentException("No sender given in from argument");
        if (information == null || information.isEmpty()) throw new IllegalArgumentException("No information about message given");

        this.uuid = UUID.randomUUID().toString();
        this.from = from;
        this.information = information;
    }

    /**
     * Gets uuid of the message
     * @return uuid of the message
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets uuid of the message
     * @param uuid uuid of the message
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets date of reception
     * @return date of reception
     */
    public Date getReceived() {
        return received;
    }

    /**
     * Sets date of reception
     * @param received date of reception
     */
    public void setReceived(Date received) {
        this.received = received;
    }

    /**
     * Gets sender of the message
     * @return Sender of the message
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets sender of the message
     * @param from sender of the message
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets information to be conveyed by the message
     * @return information to be conveyed by the message
     */
    public HashMap<String, String> getInformation() {
        return information;
    }

    /**
     * Sets information to be conveyed by the message
     * @param information information to be conveyed by the message
     */
    public void setInformation(HashMap<String, String> information) {
        this.information = information;
    }

    /**
     * Checks, whether the message has been collected by an object that works with it's information
     * @return true, if the message has been collected by an object that works with it's information, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Sets, whether the message has been collected by an object that works with it's information
     * @param collected true, if the message has been collected by an object that works with it's information, false
     *                  otherwise
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Builds String representation of the message and returns it
     * @return String representation of the message
     */
    @Override
    public String toString() {
        return "MessageInfo{" +
                "uuid='" + uuid + '\'' +
                ", received=" + received +
                ", from='" + from + '\'' +
                ", information=" + information +
                ", collected=" + collected +
                ", collectionDate=" + collectionDate +
                '}';
    }

    /**
     * Gets date of collection
     * @return date of collection
     */
    public Date getCollectionDate() {
        return collectionDate;
    }

    /**
     * Sets date of collection
     * @param collectionDate date of collection
     */
    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }
}
