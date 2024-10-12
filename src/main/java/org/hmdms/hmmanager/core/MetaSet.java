package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Definition of a MetaSet
 * Contains all information about name, id, MetaKeys and so on
 */
public class MetaSet implements IFillable, Serializable {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MetaKey.class);
    /**
     * Name of the MetaSet
     */
    private String name;
    /**
     * Id of the MetaSet
     */
    private int id;
    /**
     * Names of all MetaKeys this MetaSet uses ordered by their indices
     * for this particular MetaSet
     */
    private final ArrayList<Integer> metaKeys = new ArrayList<>();

    /**
     * {@inheritDoc}
     * @param rs ResultSet from which to fill the IFillable
     * @return
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }


    /**
     * Gets the name of the object.
     *
     * @return the name of this object.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the object.
     *
     * @param name the new name to set for this object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the object.
     *
     * @return the ID of this object.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the object.
     *
     * @param id the new ID to set for this object.
     */
    public void setId(int id) {
        this.id = id;
    }

}
