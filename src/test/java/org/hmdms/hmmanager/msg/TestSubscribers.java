package org.hmdms.hmmanager.msg;

import org.hmdms.hmmanager.msg.subscribers.Subscriber;
import org.hmdms.hmmanager.utils.ClassFinder;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class TestSubscribers {

    @Test
    public void findAllSubscribers() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<Class<? extends Subscriber>> subs = ClassFinder.findMessageSubscribers();

        for (var sub : subs) {
            System.out.println(sub);
            Subscriber s = (Subscriber) sub.getConstructors()[0].newInstance();
            System.out.println(s);
        }
    }
}
