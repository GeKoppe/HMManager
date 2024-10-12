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

    private String extension;
    private Date documentDate;

    public Document() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public int getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(int documentPath) {
        this.documentPath = documentPath;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document document)) return false;
        return getElementId() == document.getElementId() && Float.compare(getVersion(), document.getVersion()) == 0 && getDocumentPath() == document.getDocumentPath() && Objects.equals(getId(), document.getId()) && Objects.equals(getExtension(), document.getExtension());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getElementId(), getVersion(), getDocumentPath(), getExtension());
    }

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
