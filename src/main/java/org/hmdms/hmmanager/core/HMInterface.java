package org.hmdms.hmmanager.core;

import org.hmdms.hmmanager.core.user.ExecutionContext;
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
     * Service for executing asynchronous operations
     */
    private final ExecutorService ex = Executors.newFixedThreadPool(10);

    private ExecutionContext ec;

    public HMInterface(ExecutionContext ec) {
        this.ec = ec;
    }

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
     * Retrieves Element the given id refers to.
     * Fills Element object with all information that is specified in param information. If {@link ElementC#ALL_ELEMENT}
     * is given, everything is filled.
     * @param id ID or guid of the element to be checked out
     * @param information Amount of information to be checked out. If {@link ElementC#ALL_ELEMENT} is given,
     *                    all other entries will be ignored and the whole element object will be returned
     * @return Element designated by given 'id'
     * @throws IllegalArgumentException If no id or information is given
     */
    public @Nullable Element getElement(String id, List<ElementC> information) throws IllegalArgumentException {
        // Check, if params are filled
        if (id == null || id.isEmpty() || information == null) {
            this.logger.info("No id given");
            throw new IllegalArgumentException("No id given");
        }

        // Get element from database
        // TODO implement cache for faster retrieval
        Element el = this.getElementRecordFromDatabase(id);
        if (el == null) {
            this.logger.info("No element found");
            return null;
        }
        this.logger.debug(String.format("Built Element %s for id %s", el, id));

        // Retrieve all required information asynchronously
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

        // Wait for all futures to complete
        try {
            this.logger.debug("Waiting for all futures to complete");
            for (var c : checkouts.keySet()) {
                while (!checkouts.get(c).isDone()) {
                    this.logger.debug(String.format("Waiting for checkout %s", c));
                }
            }
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return null;
        }

        return el;
    }

    /**
     * Retrieves information about the record from the database, instantiates a new {@link Element}
     * object, calls it's {@link Element#fillFromResultSet(ResultSet)} method with result of db query
     * and returns the object.
     * @param id ID or guid of the element
     * @return Element object filled with all basic information about the object
     */
    private @Nullable Element getElementRecordFromDatabase(String id) {
        if (id == null || id.isEmpty()) {
            this.logger.info("No id in call to getElementRecordFromDatabase given");
            return null;
        }
        DBConnection conn = DBConnectionFactory.newDefaultConnection();
        DBQuery q = DBQueryFactory.createSelectQuery(String.format("SELECT * FROM \"elements\" WHERE \"id\" = '%s';", id));

        ResultSet rs;
        // Instantiate new element
        Element el = new Element();
        try {
            // Execute query
            rs = conn.execute(q);

            // Fill new element
            while (rs.next()) {
                if (!(el.getGuid() == null || el.getGuid().isEmpty())) break;
                el.fillFromResultSet(rs);
            }
        } catch (Exception ex) {
            LoggingUtils.logException(ex, this.logger);
            return null;
        }

        if (el.getGuid() == null || el.getGuid().isEmpty()) {
            this.logger.info(String.format("No records found for id %s", id));
            return null;
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
