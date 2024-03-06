package org.hmdms.hmmanager.msg;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MessageInfoFactory {

    private final Logger logger = LoggerFactory.getLogger(MessageInfoFactory.class);
    @Contract(" -> new")
    public static @NotNull MessageInfo createDefaultMessageInfo() {
        return new MessageInfo();
    }
}
