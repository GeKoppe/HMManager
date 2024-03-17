package org.hmdms.hmmanager.msg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.hmdms.hmmanager.sys.StateC;
import org.hmdms.hmmanager.sys.BlockingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestSubscriber extends Subscriber {
    private final TopicC topic = TopicC.TEST;

    public TestSubscriber(ConnectionFactory conn) {
        super(new String[]{"answer", "cm"}, conn);
    }


    @Override
    public TopicC getTopic() {
        return this.topic;
    }

    @Override
    public boolean notify(ArrayList<MessageInfo> mi) {
        if (!this.tryToAcquireLock("cm")) return false;
        this.currentMessages.addAll(mi);
        this.unlock("cm");
        return true;
    }

    @Override
    public void run() {
        while (!this.state.equals(StateC.STOPPED)) {
            try {
                if (!this.tryToAcquireLock("cm")) continue;
                ArrayList<MessageInfo> toDelete = new ArrayList<>();
                for (MessageInfo m : this.currentMessages) {
                    this.logger.debug("Message: " + m.toString());

                    this.logger.debug("Deleted message " + m + " from currentMessages, adding answer");
                    MessageInfo answer = MessageInfoFactory.createDefaultMessageInfo();
                    answer.setUuid(m.getUuid());
                    String answerContent = "Worked";
                    answer.setJsonMessage(new ObjectMapper().writeValueAsString(answerContent));
                    this.answerRequest(m.getMessageProps(), answer);
                    toDelete.add(m);
                    this.unlock("cm");
                    this.logger.debug("Added answer");
                }
                this.currentMessages.removeAll(toDelete);
            } catch (Exception ex) {
                this.logger.debug("Exception in subscriber run: " + ex.getMessage());
                StringBuilder stack = new StringBuilder();
                for (var stel : ex.getStackTrace()) {
                    stack.append(stel.toString());
                    stack.append("\t\n");
                }
                this.logger.debug(stack.toString());
            } finally {
                this.unlock("cm");
            }
        }
    }
}
