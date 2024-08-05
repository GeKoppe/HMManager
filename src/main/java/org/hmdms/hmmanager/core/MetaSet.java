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

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
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
}
