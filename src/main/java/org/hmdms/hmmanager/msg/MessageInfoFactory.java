package org.hmdms.hmmanager.msg;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Factory class for Message Information
 */
public class MessageInfoFactory {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MessageInfoFactory.class);

    /**
     *
     * @return A default MessageInfo object
     */
    @Contract(" -> new")
    public static @NotNull MessageInfo createDefaultMessageInfo() {
        return new MessageInfo();
    }

}
