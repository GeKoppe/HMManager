package org.hmdms.hmmanager.db;

import java.sql.ResultSet;

/**
 * Implemented by classes, that can be filled directly from a {@link ResultSet} object.
 */
public interface IFillable {
    /**
     * Fills the instance fields of the IFillable.
     * First checks, if all necessary columns are present in the {@link ResultSet} {@param rs}. Required columns
     * are varying depending on implementation.
     * Only the current row the resultsets cursor is on will be used, make sure to put the cursor on the correct row
     * before calling method.
     * @param rs ResultSet from which to fill the IFillable
     * @return True, if everything worked, false otherwise
     */
    boolean fillFromResultSet(ResultSet rs);
}
