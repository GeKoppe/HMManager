package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class that represents a document
 */
public class Document implements IFillable, Serializable {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Document.class);
    /**
     * ID of the document
     */
    private String id;
    /**
     * ID of the element this document is linked to
     */
    private int elementId;
    /**
     * Version of this document
     */
    private float version;
    /**
     * ID of the document path this document is in
     */
    private int documentPath;
    /**
     * File extension of the document
     */
    private String extension;
    /**
     * Date of physical file
     */
    private Date documentDate;

    /**
     * Default constructor
     */
    public Document() { }

    /**
     * Returns {@link Document#id} property
     * @return {@link Document#id} property
     */
    public String getId() {
        return id;
    }

    /**
     * Sets {@link Document#id} property of the current object
     * @param id What {@link Document#id} should be set to
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns id of the element this document is associated with
     * @return Id of the element this document is associated with
     */
    public int getElementId() {
        return elementId;
    }

    /**
     * Sets the id of the element this document is associated with
     * @param elementId id of the element this document is associated with
     */
    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    /**
     * Returns version of the document
     * @return Version of the document
     */
    public float getVersion() {
        return version;
    }

    /**
     * Sets version of the document
     * @param version version of the document
     */
    public void setVersion(float version) {
        this.version = version;
    }

    /**
     * Returns id of the document path the physical file lies in
     * @return id of the document path the physical file lies in
     */
    public int getDocumentPath() {
        return documentPath;
    }

    /**
     * Sets id of the path the physical document lies in
     * @param documentPath id of the path the physical document lies in
     */
    public void setDocumentPath(int documentPath) {
        this.documentPath = documentPath;
    }

    /**
     * Returns extension of this physical document
     * @return extension of this physical document
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets extension of the physical document
     * @param extension extension of the physical document
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Gets date of the physical document
     * @return date of the physical document
     */
    public Date getDocumentDate() {
        return documentDate;
    }

    /**
     * Sets date of the physical document
     * @param documentDate date of the physical document
     */
    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    /**
     * {@inheritDoc}
     * @param o Object to be checked for equality with this document
     * @return True, if o and this document are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document document)) return false;
        return getElementId() == document.getElementId() && Float.compare(getVersion(), document.getVersion()) == 0 && getDocumentPath() == document.getDocumentPath() && Objects.equals(getId(), document.getId()) && Objects.equals(getExtension(), document.getExtension());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getElementId(), getVersion(), getDocumentPath(), getExtension());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", elementId=" + elementId +
                ", version=" + version +
                ", documentPath=" + documentPath +
                ", extension='" + extension + '\'' +
                '}';
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
                    && columns.containsKey("element_id")
                    && columns.containsKey("document_date");

            // If a column is missing, return false
            if (!allColumnsExist) {
                this.logger.debug("Missing columns for filling");
                return result;
            }

            // Set class value from resultset
            this.setId(rs.getString(columns.get("id")));
            this.setElementId(rs.getInt(columns.get("element_id")));
            this.setDocumentDate(rs.getDate(columns.get("document_date")));

            // Set result to true to show, that the filling worked
            result = true;
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return false;
        }

        return result;
    }
}
