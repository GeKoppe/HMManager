package org.hmdms.hmmanager.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Broker {
    private final Logger logger = LoggerFactory.getLogger(Broker.class);

    private HashMap<String, LinkedList<MessageInfo>> mq;
    private ArrayList<Observer> observers;
    public Broker () {
        logger.debug("Broker instantiated");
        this.mq = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public void addMessage(String topic, MessageInfo mi) {
        if (topic == null || topic.isEmpty()) throw new IllegalArgumentException("No topic given");
        this.logger.debug("Adding message " + mi + " to message queue");

        if (this.mq.get(topic) == null || this.mq.get(topic).isEmpty()) {
            this.logger.debug("Topic " + topic + " currently not existent in queue, adding it");
            this.mq.put(topic, new LinkedList<>());
        }

        this.mq.get(topic).add(mi);
        this.logger.debug("MessageInfo object " + mi + " added to queue for topic " + topic);
        for (var o : this.observers) {
            if (o.notify(topic)) {
                o.giveMessages(this.getMessages(topic));
            }
        }
        this.logger.debug("Notified observers of new message");
    }

    public ArrayList<MessageInfo> getMessages(String topic) {
        ArrayList<MessageInfo> msg = new ArrayList<>();

        for (var msgInf : this.mq.get(topic)) {
            if (!msgInf.isCollected()) msg.add(msgInf);
        }

        return msg;
    }

    public void addObserver(Observer o) {
        this.observers.add(o);
        this.logger.debug("Added observer");
    }

}
