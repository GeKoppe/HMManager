package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DBQueryBuilder {

    /**
     * Logger for the class
     */
    private static Logger logger = LoggerFactory.getLogger(DBQueryBuilder.class);

    /**
     * Builds a select SQL Query
     * @param table Table from which data is to be retrieved
     * @param filters Filters for filtering the sql result
     * @param columns All columns that should be retrieved
     * @return The SELECT Query
     */
    @Contract(pure = true)
    public static @NotNull String buildSelect(@NotNull String table, @NotNull LinkedList<QueryArgument> filters, @NotNull LinkedList<String> columns) {
        logger.trace("Building select query with arguments: \ntable = " + table + ",\ncolumns = " + columns + ",\nfilters = " + filters);
        StringBuilder qb = new StringBuilder();
        qb.append("SELECT ");

        for (long i = 0L; i < columns.size(); i++){
            if (i != 0L) {
                qb.append(", ");
            }
            qb.append("[")
                    .append(columns.get((int) i))
                    .append("]");
        }

        qb.append(" FROM ")
                .append("[")
                .append(table)
                .append("]");

        if (!filters.isEmpty()) {
            logger.debug("Setting filters");
            buildFilterString(filters, qb);
        }
        qb.append(";");
        logger.debug("Build query \"" + qb + "\"");
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
            qb.append("[")
                    .append(a.getColumn())
                    .append("]")
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
