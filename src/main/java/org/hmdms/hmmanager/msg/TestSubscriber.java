package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.core.StateC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TestSubscriber implements ISubscriber {

    private final Logger logger = LoggerFactory.getLogger(TestSubscriber.class);
    private final TopicC topic = TopicC.TEST;
    private StateC state;
    private StateC answerState;
    private final ArrayList<MessageInfo> currentMessages;
    private final ReentrantLock cmLock = new ReentrantLock();
    private final ArrayList<MessageInfo> answers;
    private final ReentrantLock answersLock = new ReentrantLock();
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
    public boolean notify(ArrayList<MessageInfo> mi) {
        if (!this.tryToAcquireCmLock()) return false;
        this.currentMessages.addAll(mi);
        if (this.cmLock.isHeldByCurrentThread()) this.cmLock.unlock();
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
                if (!this.tryToAcquireCmLock()) continue;
                ArrayList<MessageInfo> toDelete = new ArrayList<>();
                for (MessageInfo m : this.currentMessages) {
                    this.logger.debug("Message: " + m.toString());

                    this.logger.debug("Deleted message " + m + " from currentMessages, adding answer");
                    MessageInfo answer = MessageInfoFactory.createDefaultMessageInfo();
                    answer.setUuid(m.getUuid());
                    if (this.answers.add(answer)) {
                        toDelete.add(m);
                    }
                    if (this.answersLock.isHeldByCurrentThread()) this.answersLock.unlock();
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
                if (this.cmLock.isHeldByCurrentThread()) this.cmLock.unlock();
            }
        }
    }

    @Override
    public ArrayList<MessageInfo> getAnswers() {
        return this.answers;
    }

    @Override
    public boolean removeAnswer(MessageInfo mi) {
        if (!this.tryToAcquireAnswersLock()) return false;
        this.answers.remove(mi);
        this.logger.debug("Removed answer " + mi + " from answer cache");
        if (this.answersLock.isHeldByCurrentThread()) this.answersLock.unlock();
        return true;
    }
    @Override
    public void setState(StateC state) {
        this.state = state;
    }

    private boolean tryToAcquireCmLock() {
        if (!this.cmLock.isHeldByCurrentThread()) {
            try {
                if (!this.cmLock.tryLock(200, TimeUnit.MILLISECONDS)) {
                    return false;
                }
            } catch (Exception ex) {
                this.logger.debug("Exception occurred while trying to acquire lock on object " + cmLock + ": " + ex.getMessage());
                return false;
            }
        }
        return true;
    }

    private boolean tryToAcquireAnswersLock() {
        if (!this.answersLock.isHeldByCurrentThread()) {
            try {
                if (!this.answersLock.tryLock(200, TimeUnit.MILLISECONDS)) {
                    return false;
                }
            } catch (Exception ex) {
                this.logger.debug("Exception occurred while trying to acquire lock on object " + answersLock + ": " + ex.getMessage());
                return false;
            }
        }
        return true;
    }
}
