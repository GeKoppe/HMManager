package org.hmdms.hmmanager.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DBQueryBuilder {

    private static Logger logger = LoggerFactory.getLogger(DBQueryBuilder.class);

    @Contract(pure = true)
    public static String buildSelect(String table, @NotNull HashMap<String, QueryArgument<Object>> filters, @NotNull ArrayList<String> columns) {
        logger.trace("Building select query with arguments: \ntable = " + table + ",\ncolumns = " + columns + ",\nfilters = " + filters.toString());
        StringBuilder qb = new StringBuilder();
        qb.append("SELECT ");

        for (long i = 0L; i < columns.size(); i++){
            if (i != 0L) {
                qb.append(", ");
            }
            qb.append(columns.get((int) i));
        }

        qb.append(" FROM " + table);

        Iterator it = filters.keySet().iterator();
        if (it.hasNext()) {
            qb.append(" WHERE ");
        }
        while (it.hasNext()) {
            it.next();
            QueryArgument qa;
        }
        return qb.toString();
    }
}
