package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Base class for all elements in the system.
 */
public abstract class Element implements IFillable {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Element.class);
    /**
     * Guid of the element
     */
    protected final String guid;

    /**
     * Metadata of the Element
     */
    protected final ArrayList<MetaKey> metadata = new ArrayList<>();

    /**
     * Default constructor. Sets {@link Element#guid} to empty String
     */
    protected Element() {
        this.guid = "";
    }

    /**
     * Constructor setting {@link Element#guid}
     * @param guid Guid of the Element.
     */
    protected Element(String guid) {
        this.guid = guid;
        this.logger.debug(String.format("Instantiating element for guid %s", guid));
    }

    /**
     * Sets value of MetaKey in {@link Element#metadata} designated by value given in 'name'
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public void SetMetaKeyValue(String name, String value) throws IllegalArgumentException {
        if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
            this.logger.warn("Parameters insufficiently filled to set metakey value");
            throw new IllegalArgumentException("Parameters insufficiently filled to set metakey value");
        }

        int index = -1;

        for (int i = 0; i < this.metadata.size(); i++) {
            if (this.metadata.get(i).getName().equals(name)) {
                index = i;
                this.logger.debug(String.format("Metakey with name %s found at index %s", name, index));
                break;
            }
        }

        if (index == -1) {
            this.logger.info(String.format("No MetaKey for name %s exists on Element %s", name, this));
            throw new IllegalArgumentException(String.format("No MetaKey for name %s exists on Element %s", name, this));
        }
        this.metadata.get(index).setValue(value);
    }

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
