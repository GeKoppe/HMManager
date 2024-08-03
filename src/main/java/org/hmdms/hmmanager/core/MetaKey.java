package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;


/**
 * Encapsulates one meta information of an element
 */
public class MetaKey implements IFillable {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MetaKey.class);
    /**
     * Name of the MetaKey
     */
    private String name = "";

    /**
     * ID of the MetaKey
     */
    private int id;
    /**
     * Default constructor
     */
    public MetaKey() { }

    /**
     * Constructor that sets all necessary information for the MetaKey
     * @param name Name of the key
     * @param id Id of the key
     */
    public MetaKey(String name, int id) {
        this.name = name;
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
