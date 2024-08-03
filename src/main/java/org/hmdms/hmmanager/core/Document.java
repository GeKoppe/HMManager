package org.hmdms.hmmanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

/**
 * Class encapsulating all information for documents
 */
public class Document extends Element {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(Document.class);

    /**
     * Default constructor
     */
    public Document() {
        super();
    }
    /**
     * Constructor setting super classes {@link Element#guid} to 'guid'
     * @param guid Guid of the element
     */
    public Document(String guid) {
        super(guid);
    }

    /**
     * TODO Implement
     * @param rs ResultSet from which to fill the IFillable
     * @return
     */
    @Override
    public boolean fillFromResultSet(ResultSet rs) {
        return false;
    }
}
