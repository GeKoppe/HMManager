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
                this.state = StateC.WORKING;
                for (MessageInfo m : this.currentMessages) {
                    this.logger.debug("Message: " + m.toString());
                    this.currentMessages.remove(m);

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
                        this.answers.add(MessageInfoFactory.createDefaultMessageInfo());
                        this.answerState = StateC.STARTED;
                    }
                }
                this.state = StateC.STARTED;
            } catch (Exception ex) {
                this.logger.debug("Exception in subscriber run: " + ex.getMessage());
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
