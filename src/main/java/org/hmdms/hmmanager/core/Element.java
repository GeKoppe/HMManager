package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Base class for all elements in the system.
 */
public class Element implements IFillable {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Element.class);
    /**
     * Guid of the element
     */
    private String guid;
    /**
     * All parent ids of references
     */
    private int[] references;
    /**
     * Name of the element
     */
    private String name;
    /**
     * ID of the parent element
     */
    private int parentId;
    /**
     * ID of the Element
     */
    private int id;
    /**
     * ID of the {@link MetaSet} this instance is categorised as.
     */
    private int metaSetId;
    /**
     * Metadata of the Element
     */
    private final ArrayList<MetaValue> metadata = new ArrayList<>();
    /**
     * Type of this object
     */
    private int elementType;
    /**
     * ID of the document this instance is referring to, if it is of type document.
     */
    private String documentId;
    /**
     * Date when this element was first created in the system
     */
    private Date internalDate;
    /**
     * Creator of the element
     */
    private int creator;
    /**
     * Current working version of the document this element represents
     */
    private Document document;

    /**
     * Default constructor. Sets {@link Element#guid} to empty String
     */
    public Element() {

    }

    /**
     * Constructor setting {@link Element#guid}
     * @param guid Guid of the Element.
     */
    public Element(String guid) {
        this.guid = guid;
        this.logger.debug(String.format("Instantiating element for guid %s", guid));
    }

    /**
     * Sets value of MetaKey in {@link Element#metadata} designated by value given in 'name'
     * @param name Name of the {@link MetaValue} for which to set the value
     * @param value Value to be set
     * @throws IllegalArgumentException If one of the params is not given or name is a value, that does not exist
     * as a MetaKey in the current element
     */
    public void setMetaValue(String name, String value) throws IllegalArgumentException {
        // Check if all necessary params are given
        if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
            this.logger.warn("Parameters insufficiently filled to set metakey value");
            throw new IllegalArgumentException("Parameters insufficiently filled to set metakey value");
        }

        // Iterate through metadata, find the correct MetaValue and set it's value
        boolean set = false;
        for (var key : this.metadata) {
            if (key.getName().equals(name)) {
                this.logger.debug(String.format("Setting value %s on key %s for element %s", value, name, this));
                key.setValue(value);
                set = true;
                break;
            }
        }

        // If value hasn't been set, no key with given name exists
        if (!set) {
            this.logger.info(String.format("MetaValue with name %s not found", name));
            throw new IllegalArgumentException(String.format("MetaValue with name %s not found", name));
        }
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMetaSetId() {
        return metaSetId;
    }

    public void setMetaSetId(int metaSetId) {
        this.metaSetId = metaSetId;
    }

    public ArrayList<MetaValue> getMetadata() {
        return metadata;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getInternalDate() {
        return internalDate;
    }

    public void setInternalDate(Date internalDate) {
        this.internalDate = internalDate;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int[] getReferences() {
        return references;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Element element)) return false;
        return getId() == element.getId() && Objects.equals(getGuid(), element.getGuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuid(), getId());
    }

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
