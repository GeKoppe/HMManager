package org.hmdms.hmmanager.db;

/**
 * Classes implementing this interface can be filled from a {@link java.sql.ResultSet}
 * and transformed into insert / update queries.
 */
public interface ISQLConvertible extends IFillable {
    /**
     * Uses all class fields and builds an insert query from them.
     * @return {@link DBQuery} object with type set to {@link QueryTypeC#INSERT} and sql query set to
     * values of instance fields.
     */
    DBQuery generateInsertQuery();
}
