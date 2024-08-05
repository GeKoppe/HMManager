package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.db.DBConnection;
import org.hmdms.hmmanager.db.DBConnectionFactory;
import org.hmdms.hmmanager.db.DBQuery;
import org.hmdms.hmmanager.db.DBQueryFactory;
import org.hmdms.hmmanager.utils.LoggingUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// TODO implement cache for elements and documents
public class HMInterface {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(HMInterface.class);
    /**
     * ID of the user currently using the interface
     */
    private String userId;
    /**
     * ID of the ticket of the user currently using this interface.
     */
    private String ticketId;
    /**
     * Service for executing asynchronous operations
     */
    private final ExecutorService ex = Executors.newFixedThreadPool(10);
    public HMInterface() { }

    /**
     * Returns {@link Element} designated by given 'id' asynchronously.
     * Information defines, how much of the element should be returned. Submits {@link HMInterface#getElement(String, List)}
     * to {@link HMInterface#ex} and returns the resulting Future.
     * @param id ID or guid of the element to be checked out
     * @param information Amount of information to be checked out. If {@link ElementC#ALL_ELEMENT} is given,
     *                    all other entries will be ignored and the whole element object will be returned
     * @return Task that is resolving to the Element returned by {@link HMInterface#getElement(String, List)}
     */
    public Future<Element> getElementAsync(String id, List<ElementC> information) {
        return this.ex.submit(() -> this.getElement(id, information));
    }

    /**
     *
     * @param id ID or guid of the element to be checked out
     * @param information Amount of information to be checked out. If {@link ElementC#ALL_ELEMENT} is given,
     *                    all other entries will be ignored and the whole element object will be returned
     * @return Element designated by given 'id'
     * @throws IllegalArgumentException If no id or information is given
     */
    public @Nullable Element getElement(String id, List<ElementC> information) throws IllegalArgumentException {
        if (id == null || id.isEmpty() || information == null) {
            this.logger.info("No id given");
            throw new IllegalArgumentException("No id given");
        }
        Element el = new Element();

        DBConnection conn = DBConnectionFactory.newDefaultConnection();
        DBQuery q = DBQueryFactory.createSelectQuery(String.format("SELECT * FROM \"elements\" WHERE \"id\" = '%s';", id));
        ResultSet rs;
        try {
            rs = conn.execute(q);
            while (rs.next()) {
                if (!(el.getGuid() == null || el.getGuid().isEmpty())) break;
                el.fillFromResultSet(rs);
            }
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return null;
        }

        HashMap<String, Future<?>> checkouts = new HashMap<>();
        if (information.contains(ElementC.ALL_ELEMENT)) {
            checkouts.put(
                    "doc",
                    this.ex.submit(
                            () -> this.getDocumentForElementAsync(
                                    id,
                                    -1
                            )
                    )
            );
            checkouts.put(
                    "metavalues",
                    this.ex.submit(
                            () -> this.getMetaValuesForElementAsync(
                                    id,
                                    new String[]{}
                            )
                    )
            );
        }

        return el;
    }

    public Future<Document> getDocumentForElementAsync(String elementId, float version) {
        return this.ex.submit(() -> this.getDocumentForElement(elementId, version));
    }
    /**
     *
     * @param elementId
     * @param version
     * @return
     */
    public Document getDocumentForElement(String elementId, float version) throws IllegalArgumentException {
        if (elementId == null || elementId.isEmpty()) {
            this.logger.info("No id given");
            throw new IllegalArgumentException("No id given");
        }

        Document doc = new Document();
        String query = String.format("SELECT * FROM \"documents\" WHERE \"element_id\" = '%s'", elementId);
        if (version < 0) {
            this.logger.debug("Version less then 0 given, retrieving newest version");
        } else {
            query = String.format(query + " AND \"version\" = %s", version);
            this.logger.debug("Retrieving document for version " + version);
        }

        query += " ORDER BY \"version\" DESCENDING;";

        this.logger.debug(String.format("Query: %s", query));

        DBConnection conn = DBConnectionFactory.newDefaultConnection();
        DBQuery q = DBQueryFactory.createSelectQuery(query);
        ResultSet rs;

        try {
            rs = conn.execute(q);
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return null;
        }
        try {
            while (rs.next()) {
                if (!(doc.getId() == null || doc.getId().isEmpty())) break;
                doc.fillFromResultSet(rs);
            }
        } catch (Exception ex) {
            LoggingUtils.logException(
                    ex,
                    this.logger,
                    "info",
                    "%s exception occurred while trying to load document from database: %s"
            );
            return null;
        }
        return doc;
    }

    public Future<ArrayList<MetaValue>> getMetaValuesForElementAsync(String elementId, String[] keyNames) {
        return this.ex.submit(() -> this.getMetaValuesForElement(elementId, keyNames));
    }

    public ArrayList<MetaValue> getMetaValuesForElement(String elementId, String[] keyNames) {
        return new ArrayList<>();
    }



}
