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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public HashMap<String, String> getInformation() {
        return information;
    }

    public void setInformation(HashMap<String, String> information) {
        this.information = information;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "uuid='" + uuid + '\'' +
                ", received=" + received +
                ", from='" + from + '\'' +
                ", information=" + information +
                ", collected=" + collected +
                '}';
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }
}
