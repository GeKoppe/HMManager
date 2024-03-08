package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestSubscriber implements ISubscriber {

    private final Logger logger = LoggerFactory.getLogger(TestSubscriber.class);
    private final TopicC topic = TopicC.TEST;
    private StateC state;
    private final ArrayList<MessageInfo> currentMessages;
    private final ArrayList<MessageInfo> answers;
    public TestSubscriber() {
        this.state = StateC.INITIALIZED;
        this.currentMessages = new ArrayList<>();
        this.answers = new ArrayList<>();
    }


    @Override
    public TopicC getTopic() {
        return this.topic;
    }

    @Override
    public void notify(ArrayList<MessageInfo> mi) {
        this.currentMessages.addAll(mi);
        this.state = StateC.WORKING;
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
            for (MessageInfo m : this.currentMessages) {
                System.out.println(m.toString());
                this.currentMessages.remove(m);
                this.answers.add(MessageInfoFactory.createDefaultMessageInfo());
            }
            if (this.currentMessages.isEmpty() && !this.state.equals(StateC.STARTED)) this.state = StateC.STARTED;
        }
    }

    @Override
    public ArrayList<MessageInfo> getAnswers() {
        return this.answers;
    }
}
