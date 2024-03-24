package org.hmdms.hmmanager.msg.subscribers;

import com.fasterxml.jackson.databind.JsonNode;
import com.rabbitmq.client.ConnectionFactory;
import org.hmdms.hmmanager.core.user.UserTicket;
import org.hmdms.hmmanager.msg.MessageInfo;
import org.hmdms.hmmanager.msg.TopicC;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.sys.services.UserService;
import org.hmdms.hmmanager.utils.JsonUtils;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Subscriber that handles all sort of user interaction like logins, user creation, deletion and so on.
 */
public class UserSubscriber extends Subscriber {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(UserSubscriber.class);

    /**
     * Constructs a new {@link UserSubscriber} that connects to the RabbitMQ over the {@link ConnectionFactory}
     * defined in {@param conn}.
     * @param conn {@link ConnectionFactory} the subscriber should use to connect to the rabbitmq
     */
    public UserSubscriber(ConnectionFactory conn) {
        super(new String[]{"cm"}, conn);
        this.topic = TopicC.AUTH;
    }

    /**
     * {@inheritDoc}
     * Messages are cached. Subscriber will work on them in their own thread.
     * @param mi Messages of which the subscriber should be notified
     * @return True, if messages could be cached, false otherwise. Reasons for this could be, that the
     * cache could not be locked by this thread.
     */
    @Override
    public boolean notify(ArrayList<MessageInfo> mi) {
        this.logger.debug(String.format("Trying to add %x messages to the message queue", mi.size()));
        if (!this.tryToAcquireLock("cm")) {
            this.logger.debug("Could not acquire lock for messages and therefore could not add messages to queue");
            this.unlock("cm");
            return false;
        }

        this.currentMessages.addAll(mi);
        this.logger.debug("Added all messages to queue");

        this.unlock("cm");
        return true;
    }

    /**
     * Main logic of this object.
     * Iterates through the entire cache of messages and works all of them. After each message that was worked on,
     * the subscriber answers over the message queue opened by the requester.
     */
    @Override
    public void run() {
        if (!this.state.equals(StateC.INITIALIZED) && !this.state.equals(StateC.STARTED)) {
            this.logger.debug(String.format("Not running %s as the components state is %s", this, this.state));
            return;
        }

        this.state = StateC.WORKING;
        while (this.state.equals(StateC.WORKING)) {
            if (!this.tryToAcquireLock("cm")) continue;

            // Iterate through all messages and login the user
            if (!this.currentMessages.isEmpty()) {
                ArrayList<MessageInfo> toRemove = new ArrayList<>();
                for (MessageInfo mi : this.currentMessages) {
                    String message = mi.getJsonMessage();
                    try {
                        if (!JsonUtils.jsonHasProperty(message, "username")
                            || !JsonUtils.jsonHasProperty(message, "password")) {
                            this.loginFailed(mi, "No username or password given");
                        } else {
                            this.login(mi);
                        }

                    } catch (Exception ex) {
                        LoggingUtils.logException(ex, this.logger, "info");
                        this.loginFailedInternal(mi, String.format("%s: %s", ex.getClass().getName(), ex.getMessage()));
                    }

                    toRemove.add(mi);
                }
                this.currentMessages.removeAll(toRemove);
            }
            this.unlock("cm");
        }
    }

    /**
     * Used, when the login failed due to an internal error.
     * @param mi Original message info object
     * @param reason Reason for the failed login
     */
    private void loginFailedInternal(MessageInfo mi, String reason) {

    }

    /**
     * Used when the login failed due to a user error
     * @param mi Original message info object
     * @param reason Reason for the failed login
     */
    private void loginFailed(MessageInfo mi, String reason) {

    }

    /**
     * Logs a user into the system
     * @param mi Object containing information about whom to login.
     */
    private void login(MessageInfo mi) {
        JsonNode node;
        try {
            node = JsonUtils.getNodeFromString(mi.getJsonMessage());
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "info",
                    "%s occurred while trying to login a user: %s"
            );
            this.loginFailedInternal(mi, String.format("%s: %s", ex.getClass().getName(), ex.getMessage()));
            return;
        }

        String userName = node.get("username").toString();
        String pw = node.get("pw").toString();

        UserTicket ticket;
        try {
            ticket = UserService.login(userName, pw);
        } catch (Exception ex) {
            // TODO error handling
            return;
        }
        if (!this.answerRequest(mi.getMessageProps(), ticket)) {
            // TODO error handling
        }
    }
}
