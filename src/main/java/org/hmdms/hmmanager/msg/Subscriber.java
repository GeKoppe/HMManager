package org.hmdms.hmmanager.msg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.hmdms.hmmanager.sys.BlockingComponent;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Base Class for all message subscribers
 */
public abstract class Subscriber extends BlockingComponent implements ISubscriber {
    /**
     * Logger
     */
    protected final Logger logger;
    /**
     * All currentMessages this subscriber is working on
     */
    protected final ArrayList<MessageInfo> currentMessages;
    /**
     * Topic the Subscriber subscribes to
     */
    protected TopicC topic;
    /**
     * Factory for creating connections to the rabbitmq service
     */
    private final ConnectionFactory connectionFactory;

    /**
     * Default constructor
     * @param lockIds Ids for locks the {@link BlockingComponent} should instantiate
     */
    public Subscriber(String[] lockIds, ConnectionFactory conn) {
        super(lockIds);
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.currentMessages = new ArrayList<>();
        this.state = StateC.INITIALIZED;
        this.connectionFactory = conn;
    }

    /**
     * Returns the topic the subscriber subscribes to
     * @return topic the subscriber subscribes to
     */
    @Override
    public TopicC getTopic() {
        return this.topic;
    }

    /**
     * Sets the topic the subscriber subscribes to
     * @param topic topic the subscriber subscribes to
     */
    public void setTopic(TopicC topic) {
        this.topic = topic;
    }

    protected boolean answerRequest(BasicProperties props, Serializable answerObj) {
        try (Connection conn = this.connectionFactory.newConnection(); Channel channel = conn.createChannel()) {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(props.getCorrelationId())
                    .build();

            String jsonString = new ObjectMapper().writeValueAsString(answerObj);
            channel.basicPublish(
                    "",
                    props.getReplyTo(),
                    replyProps,
                    jsonString.getBytes(StandardCharsets.UTF_8)
            );
            return true;
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger, "info", "%s occurred while trying to answer rpc request: %s");
            return false;
        }
    }
}
