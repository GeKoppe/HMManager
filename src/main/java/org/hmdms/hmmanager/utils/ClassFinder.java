package org.hmdms.hmmanager.utils;

import org.hmdms.hmmanager.msg.subscribers.Subscriber;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Utility class for finding certain types of classes at runtime.
 */
public abstract class ClassFinder {

    private static Logger logger = LoggerFactory.getLogger(ClassFinder.class);
    /**
     * Scans the package {@link org.hmdms.hmmanager.msg} for all classes, that extend the {@link Subscriber}.
     * Returns class definitions of those classes
     * @return
     */
    public static Set<Class<? extends Subscriber>> findMessageSubscribers() {
        logger.debug("Scanning for subscriber classes");
        Reflections reflections = new Reflections("org.hmdms.hmmanager.msg.subscribers");
        return reflections.getSubTypesOf(Subscriber.class);
    }
}
