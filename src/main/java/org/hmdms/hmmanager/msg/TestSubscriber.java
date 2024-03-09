package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.core.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.ArrayList;

public class TestSubscriber implements ISubscriber {

    private final Logger logger = LoggerFactory.getLogger(TestSubscriber.class);
    private final TopicC topic = TopicC.TEST;
    private StateC state;
    private StateC answerState;
    private final ArrayList<MessageInfo> currentMessages;
    private final ArrayList<MessageInfo> answers;
    public TestSubscriber() {
        this.currentMessages = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.answerState = StateC.STARTED;

        this.state = StateC.STARTED;
    }


    @Override
    public TopicC getTopic() {
        return this.topic;
    }

    @Override
    public void notify(ArrayList<MessageInfo> mi) {
        this.state = StateC.WORKING;
        this.currentMessages.addAll(mi);
        this.state = StateC.STARTED;
    }

    /**
     *
     * @return Current state of the subscriber
     */
    @Override
    public StateC getState() {
        return this.state;
    }

    @Override
    public void run() {
        while (!this.state.equals(StateC.STOPPED)) {
            try {
                if (this.state.equals(StateC.WORKING)) continue;

                ArrayList<MessageInfo> toDelete = new ArrayList<>();
                for (MessageInfo m : this.currentMessages) {
                    this.state = StateC.WORKING;
                    this.logger.debug("Message: " + m.toString());

                    int tries = 0;
                    while (this.answerState.equals(StateC.WORKING)) {
                        tries++;
                        if (tries == 10000) {
                            break;
                        }
                    }
                    if (tries == 10000) {
                        this.logger.debug("Currently working on answers, returning");
                    } else {
                        this.answerState = StateC.WORKING;
                        toDelete.add(m);
                        this.logger.debug("Deleted message " + m + " from currentMessages, adding answer");
                        MessageInfo answer = MessageInfoFactory.createDefaultMessageInfo();
                        answer.setUuid(m.getUuid());
                        this.answers.add(answer);
                        this.logger.debug("Added answer");
                        this.answerState = StateC.STARTED;
                    }
                }
                this.currentMessages.removeAll(toDelete);
                this.state = StateC.STARTED;
            } catch (Exception ex) {
                this.logger.debug("Exception in subscriber run: " + ex.getMessage());
                StringBuilder stack = new StringBuilder();
                for (var stel : ex.getStackTrace()) {
                    stack.append(stel.toString());
                    stack.append("\t\n");
                }
                this.logger.debug(stack.toString());
            }
        }
    }

    @Override
    public ArrayList<MessageInfo> getAnswers() {
        return this.answers;
    }

    @Override
    public boolean removeAnswer(MessageInfo mi) {
        int tries = 0;
        while (this.answerState.equals(StateC.WORKING)) {
            tries++;
            if (tries == 10000) {
                break;
            }
        }
        if (tries == 10000) {
            this.logger.debug("Currently working on answers, returning");
            return false;
        }
        this.answerState = StateC.WORKING;
        this.answers.remove(mi);
        this.logger.debug("Removed answer " + mi + " from answer cache");
        this.answerState = StateC.STARTED;
        return true;
    }
    @Override
    public void setState(StateC state) {
        this.state = state;
    }
}
