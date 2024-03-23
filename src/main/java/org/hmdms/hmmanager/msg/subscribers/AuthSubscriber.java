package org.hmdms.hmmanager.msg.subscribers;

import com.rabbitmq.client.ConnectionFactory;
import org.hmdms.hmmanager.msg.MessageInfo;
import org.hmdms.hmmanager.msg.TopicC;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.utils.JsonUtils;
import org.hmdms.hmmanager.utils.LoggingUtils;

import java.util.ArrayList;

public class AuthSubscriber extends Subscriber {

    public AuthSubscriber(ConnectionFactory conn) {
        super(new String[]{"cm"}, conn);
        this.topic = TopicC.AUTH;
    }

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
            if (this.currentMessages.size() > 0) {
                ArrayList<MessageInfo> toRemove = new ArrayList<>();
                for (MessageInfo mi : this.currentMessages) {
                    String message = mi.getJsonMessage();
                    try {
                        if (!JsonUtils.jsonHasProperty(message, "username")
                            || !JsonUtils.jsonHasProperty(message, "password")) {
                            this.loginFailed(mi, "No username or password given");
                        }
                    } catch (Exception ex) {
                        LoggingUtils.logException(ex, this.logger, "info");
                        this.loginFailedInternal(mi);
                    }

                    toRemove.add(mi);
                }
            }
            this.unlock("cm");
        }
    }

    private void loginFailedInternal(MessageInfo mi) {

    }

    private void loginFailed(MessageInfo mi, String reason) {

    }

    private void login(MessageInfo mi) {

    }
}
