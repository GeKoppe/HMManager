package org.hmdms.hmmanager.db;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DBConnectionTest {

    @Test
    public void testInsert() {
        DBConnection conn = DBConnectionFactory.newDefaultConnection();
        conn.connect();

        //String query = DBQueryBuilder.buildInsertQuery("new_test", arg);
        String query = "INSERT INTO new_test (col) VALUES (28), (35);";
        int rs;
        try {
            rs = conn.executeUpdate(query);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }

}
