package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;
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

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
