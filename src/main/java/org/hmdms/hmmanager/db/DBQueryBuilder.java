package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 */
public class DBQueryBuilder {

    /**
     * Logger for the class
     */
    private static final Logger logger = LoggerFactory.getLogger(DBQueryBuilder.class);

    /**
     * Builds a select SQL Query
     * @param table Table from which data is to be retrieved
     * @param filters Filters for filtering the sql result
     * @param columns All columns that should be retrieved
     * @return The SELECT Query
     */
    @Contract(pure = true)
    public static @NotNull String buildSelect(@NotNull String table, @NotNull LinkedList<QueryArgument> filters, @NotNull LinkedList<String> columns) {
        logger.trace("Building select query with arguments: \n\ttable = " + table + ",\n\tcolumns = " + columns + ",\n\tfilters = " + filters);
        StringBuilder qb = new StringBuilder();
        qb.append("SELECT ");

        for (long i = 0L; i < columns.size(); i++){
            if (i != 0L) {
                qb.append(", ");
            }
            qb.append(columns.get((int) i));
        }

        qb.append(" FROM ")
                .append(table);

        if (!filters.isEmpty()) {
            logger.debug("Setting filters");
            buildFilterString(filters, qb);
        }
        qb.append(";");
        logger.debug("Build query \"" + qb + "\"");
        return qb.toString();
    }

    public static String buildUpdateQuery(@NotNull String table, @NotNull LinkedList<QueryArgument> updates, @NotNull LinkedList<QueryArgument> filters) {
        StringBuilder qb = new StringBuilder();
        logger.trace("Building update query for arguments:\n\ttable: " + table + "\n\tupdates: " + updates + "\n\tfilters: " + filters);
        qb.append("UPDATE ")
                .append(table)
                .append(" SET ");

        boolean firstSet = false;
        for (var c : updates) {
            logger.debug("Adding to update: " + c);
            if (firstSet) qb.append(", ");
            qb.append(c.getColumn())
                    .append(" = ");
            if (c.getType().compareTo(QueryArgumentTypes.DATETIME) <= 0) {
                qb.append("'");
            }

            qb.append(c.getValue());

            if (c.getType().compareTo(QueryArgumentTypes.DATETIME) <= 0) {
                qb.append("'");
            }
            firstSet = true;
        }
        buildFilterString(filters, qb);
        qb.append(";");
        logger.debug("Finished updatestring: %s".formatted(qb));
        return qb.toString();
    }

    public static String buildInsertQuery(@NotNull String table, @NotNull LinkedList<String> columns, @NotNull LinkedList<QueryArgument> inserts) {
        logger.trace("Building insert query with arguments:\n\ttable: " + table + "\n\tinserts: " + inserts);
        StringBuilder qb = new StringBuilder();
        qb.append("INSERT INTO ")
                .append(table)
                .append(" (");
        return qb.toString();
    }

    /**
     * Builds String that filters SQL result. Adds a string of format " WHERE [firstFilter] = 'firstFilterValue' AND ...'
     * to the given String builder
     * @param filters All Arguments to be added to the string builder in sql style
     * @param qb String builder to which the filters should be appended
     */
    private static void buildFilterString(@NotNull LinkedList<QueryArgument> filters, @NotNull StringBuilder qb) {
        boolean firstSet = false;
        qb.append(" WHERE ");
        for (QueryArgument a : filters) {
            logger.debug("Setting filter " + a);
            if (firstSet) qb.append(" AND ");
            qb.append(a.getColumn())
                    .append(" = ");

            if (a.getType().compareTo(QueryArgumentTypes.DATETIME) <= 0) {
                qb.append("'");
            }

            qb.append(a.getValue());

            if (a.getType().compareTo(QueryArgumentTypes.DATETIME) <= 0) {
                qb.append("'");
            }
            firstSet = true;
            logger.debug("Added filter successfully");
        }
    }


}
