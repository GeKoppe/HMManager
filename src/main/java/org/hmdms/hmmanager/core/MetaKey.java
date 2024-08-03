package org.hmdms.hmmanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Encapsulates one meta information of an element
 */
public class MetaKey {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MetaKey.class);
    /**
     * Name of the metakey
     */
    private String name = "";
    /**
     * Value of the metakey
     */
    private String value = "";
    private int id;
    /**
     * Default constructor
     */
    public MetaKey() { }

    /**
     * Constructor that sets all necessary information for the MetaKey
     * @param name Name of the key
     * @param value Value of the key
     * @param id Id of the key
     */
    public MetaKey(String name, String value, int id) {
        this.name = name;
        this.value = value;
        this.id = id;
    }

    /**
     * Sets all class information
     * @param name Name of the MetaKey
     * @param value Value of the MetaKey
     */
    public MetaKey(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Sets name of MetaKey
     * @param name name of MetaKey
     */
    public MetaKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MetaKey{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaKey metaKey)) return false;
        return getId() == metaKey.getId() && Objects.equals(getName(), metaKey.getName()) && Objects.equals(getValue(), metaKey.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue(), getId());
    }
}
