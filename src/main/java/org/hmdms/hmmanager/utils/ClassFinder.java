package org.hmdms.hmmanager.utils;

import org.hmdms.hmmanager.msg.Subscriber;
import org.reflections.Reflections;

import java.util.Set;

public abstract class ClassFinder {

    public static Set<Class<? extends Subscriber>> findMessageSubscribers() {
        Reflections reflections = new Reflections("org.hmdms.hmmanager.msg");
        return reflections.getSubTypesOf(Subscriber.class);
    }
}
