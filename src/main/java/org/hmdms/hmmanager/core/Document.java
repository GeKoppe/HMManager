package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.IFillable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class Document implements IFillable {
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

    public Document() { }
    

    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
