package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Base class for all elements in the system.
 */
public class Element implements IFillable, Serializable {
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

    /**
     * Gets the GUID of the element.
     *
     * @return the GUID of this element.
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the GUID of the element.
     *
     * @param guid the new GUID to set.
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * Gets the name of the element.
     *
     * @return the name of the element.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the element.
     *
     * @param name the new name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the parent element.
     *
     * @return the parent ID of this element.
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * Sets the ID of the parent element.
     *
     * @param parentId the new parent ID to set.
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the ID of the element.
     *
     * @return the ID of the element.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the element.
     *
     * @param id the new ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the {@link MetaSet} associated with this element.
     *
     * @return the metaSetId of the element.
     */
    public int getMetaSetId() {
        return metaSetId;
    }

    /**
     * Sets the ID of the {@link MetaSet} associated with this element.
     *
     * @param metaSetId the new metaSetId to set.
     */
    public void setMetaSetId(int metaSetId) {
        this.metaSetId = metaSetId;
    }

    /**
     * Gets the metadata of the element.
     *
     * @return the list of {@link MetaValue} objects associated with this element.
     */
    public ArrayList<MetaValue> getMetadata() {
        return metadata;
    }

    /**
     * Gets the element type of this object.
     *
     * @return the element type as an integer.
     */
    public int getElementType() {
        return elementType;
    }

    /**
     * Sets the element type of this object.
     *
     * @param elementType the new element type to set.
     */
    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    /**
     * Gets the document ID if this element refers to a document.
     *
     * @return the document ID associated with this element.
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the document ID for this element.
     *
     * @param documentId the new document ID to set.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Gets the internal creation date of the element.
     *
     * @return the internal date when the element was first created.
     */
    public Date getInternalDate() {
        return internalDate;
    }

    /**
     * Sets the internal creation date of the element.
     *
     * @param internalDate the new internal date to set.
     */
    public void setInternalDate(Date internalDate) {
        this.internalDate = internalDate;
    }

    /**
     * Gets the creator ID of the element.
     *
     * @return the creator ID associated with this element.
     */
    public int getCreator() {
        return creator;
    }

    /**
     * Sets the creator ID of the element.
     *
     * @param creator the new creator ID to set.
     */
    public void setCreator(int creator) {
        this.creator = creator;
    }

    /**
     * Gets the references of the element.
     *
     * @return an array of parent IDs this element references.
     */
    public int[] getReferences() {
        return references;
    }

    /**
     * Gets the document object that this element represents.
     *
     * @return the {@link Document} object associated with this element.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the document object that this element represents.
     *
     * @param document the new {@link Document} object to set.
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(getGuid(), getId());
    }

    /**
     * {@inheritDoc}
     * In this case, the necessary columns are:
     * user_name (String), user_id (String), locked (boolean), created_at ({@link Date})
     * @param rs ResultSet from which to fill the object.
     *           Cursor of the resultset must be on the row, with which the user should be filled
     * @return True, if filling was successful, false otherwise
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        boolean result = false;

        this.logger.debug("Filling object from resultset");
        try {
            // Get metadata from the resultset and read the column names / labels
            ResultSetMetaData rsmd = rs.getMetaData();
            HashMap<String, Integer> columns = new HashMap<>();

            // Put column names in a hashmap with their respective column numbers be able to access them easier
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columns.put(rsmd.getColumnLabel(i) != null ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i), i);
            }

            // Check, if all necessary columns are existent in the resultset
            boolean allColumnsExist = columns.containsKey("id")
                    && columns.containsKey("guid")
                    && columns.containsKey("name");

            // If a column is missing, return false
            if (!allColumnsExist) {
                this.logger.debug("Missing columns for filling");
                return result;
            }

            // Set class value from resultset
            this.setId(rs.getInt(columns.get("id")));
            this.setGuid(rs.getString(columns.get("guid")));
            this.setName(rs.getString(columns.get("name")));

            // Set result to true to show, that the filling worked
            result = true;
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return false;
        }

        return result;
    }
}
