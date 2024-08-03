package org.hmdms.hmmanager.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ElementFactory {
    private static final Logger logger = LoggerFactory.getLogger(ElementFactory.class);

    @Contract(" -> new")
    public static @NotNull Structure getEmptyStructure() {
        return new Structure();
    }

    @Contract(" -> new")
    public static @NotNull Document getEmptyDocument() {
        return new Document();
    }
}
