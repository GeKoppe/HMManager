package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.core.StateC;
import org.hmdms.hmmanager.sys.BlockingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TestSubscriber extends BlockingComponent implements ISubscriber {

    private final Logger logger = LoggerFactory.getLogger(TestSubscriber.class);
    private final TopicC topic = TopicC.TEST;
    private StateC state;
    private final ArrayList<MessageInfo> currentMessages;
    private final ArrayList<MessageInfo> answers;
    public TestSubscriber() {
        super(new String[]{"answer", "cm"});
        this.currentMessages = new ArrayList<>();
        this.answers = new ArrayList<>();

        this.state = StateC.STARTED;
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
                if (!this.tryToAcquireLock("cm")) continue;
                ArrayList<MessageInfo> toDelete = new ArrayList<>();
                for (MessageInfo m : this.currentMessages) {
                    this.logger.debug("Message: " + m.toString());

                    this.logger.debug("Deleted message " + m + " from currentMessages, adding answer");
                    MessageInfo answer = MessageInfoFactory.createDefaultMessageInfo();
                    answer.setUuid(m.getUuid());
                    if (this.answers.add(answer)) {
                        toDelete.add(m);
                    }
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

    @Override
    public ArrayList<MessageInfo> getAnswers() {
        return this.answers;
    }

    @Override
    public boolean removeAnswer(MessageInfo mi) {
        if (!this.tryToAcquireLock("answer")) return false;
        this.answers.remove(mi);
        this.logger.debug("Removed answer " + mi + " from answer cache");
        this.unlock("answer");
        return true;
    }
    @Override
    public void setState(StateC state) {
        this.state = state;
    }
}
