package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;


/**
 * Encapsulates one meta information of an element
 */
public class MetaKey implements IFillable, Serializable {
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

    /**
     * Returns the name of the MetaKey
     * @return Name of the MetaKey
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the MetaKey
     * @param name Name of the MetaKey to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns id of the MetaKey
     * @return id of the MetaKey
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id of the MetaKey
     * @param id Id to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     * TODO implement
     * @param rs ResultSet from which to fill the IFillable
     * @return True, if object could be filled from ResultSet, false otherwise
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
