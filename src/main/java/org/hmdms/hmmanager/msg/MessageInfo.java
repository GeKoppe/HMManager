package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class MessageInfo {

    private final Logger logger = LoggerFactory.getLogger(MessageInfo.class);
    private String uuid;
    private Date received;
    private String from;
    private HashMap<String,String> information;
    private boolean collected;
    private Date collectionDate;

    public MessageInfo() {
        this.logger.debug("Empty MessageInfo Object");

        this.uuid = UUID.randomUUID().toString();
        this.received = new Date();

        this.from = "";
        this.information = new HashMap<>();
        this.collected = false;
    }

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
