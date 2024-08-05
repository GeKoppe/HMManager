package org.hmdms.hmmanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class MetaValue extends MetaKey {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MetaValue.class);
    /**
     * Value of this MetaValue
     */
    private String value;

    public MetaValue() {
        super();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaValue metaValue)) return false;
        return Objects.equals(getValue(), metaValue.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "MetaValue{" +
                "value='" + value + '\'' +
                "} " + super.toString();
    }
}
