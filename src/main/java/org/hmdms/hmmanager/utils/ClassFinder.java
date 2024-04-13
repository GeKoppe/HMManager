package org.hmdms.hmmanager.utils;

import org.hmdms.hmmanager.msg.subscribers.Subscriber;
import org.hmdms.hmmanager.sys.cache.Cache;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Utility class for finding certain types of classes at runtime.
 */
public abstract class ClassFinder {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ClassFinder.class);

    /**
     * Default constructor
     */
    public ClassFinder() { }
    /**
     * Scans the package {@link org.hmdms.hmmanager.msg} for all classes, that extend the {@link Subscriber}.
     * Returns class definitions of those classes
     * @return All subscriber classes
     */
    public static Set<Class<? extends Subscriber>> findMessageSubscribers() {
        logger.debug("Scanning for subscriber classes");
        Reflections reflections = new Reflections("org.hmdms.hmmanager.msg.subscribers");
        return reflections.getSubTypesOf(Subscriber.class);
    }
}
