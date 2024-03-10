package org.hmdms.hmmanager.msg;

import java.io.Serializable;

public class JmsMessage {
    private TopicC topic;

    private MessageInfo mi;

    public JmsMessage(TopicC topic, MessageInfo mi) {
        this.topic = topic;
        this.mi = mi;
    }
    public JmsMessage() {

    }

    public TopicC getTopic() {
        return topic;
    }

    public MessageInfo getMi() {
        return mi;
    }

    @Override
    public String toString() {
        return "JmsMessage{" +
                "topic=" + topic +
                ", mi=" + mi.toString() +
                '}';
    }
    public void setTopic(TopicC topic) {
        this.topic = topic;
    }

    public void setMi(MessageInfo mi) {
        this.mi = mi;
    }
}
