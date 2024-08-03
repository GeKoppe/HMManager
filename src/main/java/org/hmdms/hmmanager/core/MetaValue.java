package org.hmdms.hmmanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MetaValue extends MetaKey {
    private final Logger logger = LoggerFactory.getLogger(MetaValue.class);
    private String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
