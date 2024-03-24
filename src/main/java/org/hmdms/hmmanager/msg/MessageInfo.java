package org.hmdms.hmmanager.msg;

import com.rabbitmq.client.BasicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Class that represents a message in the system
 */
public class MessageInfo implements Serializable {

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
     * Information in the message
     */
    private String jsonMessage;
    /**
     * Shows, whether the message was collected by a subscriber. Used by {@link Broker} class.
     */
    private boolean collected;
    /**
     * Date when the message was collected
     */
    private Date collectionDate;
    /**
     * Properties of the rabbitmq message.
     * In order to be able to reply to the rpc message, {@link BasicProperties#getReplyTo()} must not return null or
     * an empty string
     */
    private BasicProperties messageProps;

    /**
     * Default Constructor
     */
    public MessageInfo() {
        this.logger.debug("Empty MessageInfo Object");

        this.uuid = UUID.randomUUID().toString();
        this.received = new Date();
        this.collected = false;
        this.collectionDate = null;
    }

    /**
     * Constructor for the MessageInfo object
     * @param props Properties of the amqp messsage
     */
    public MessageInfo(BasicProperties props) {
        this();
        this.messageProps = props;
    }

    /**
     * Constructor that sets the class fields {@link MessageInfo#messageProps} and {@link MessageInfo#jsonMessage}
     * @param props RabbitMQ props of the message
     * @param jsonMessage Json String of the message
     */
    public MessageInfo(BasicProperties props, String jsonMessage) {
        this(props);
        this.jsonMessage = jsonMessage;
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
                ", jsonMessage='" + jsonMessage + '\'' +
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

    /**
     * Returns rpc message properties object
     * @return rpc message properties object
     */
    public BasicProperties getMessageProps() {
        return messageProps;
    }

    /**
     * Sets rpc message properties object
     * @param messageProps rpc message properties object
     */
    public void setMessageProps(BasicProperties messageProps) {
        this.messageProps = messageProps;
    }

    /**
     * Returns the json message that is to be transmitted by this object
     * @return json message that is to be transmitted by this object
     */
    public String getJsonMessage() {
        return jsonMessage;
    }

    /**
     * Sets the json message that is to be transmitted by this object
     * @param jsonMessage json message that is to be transmitted by this object
     */
    public void setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
    }
}
