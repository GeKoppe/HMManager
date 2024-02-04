package org.hmdms.hmmanager.db;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DBConnectionFactoryTest {

    @Test
    public void testDefaultConn() {
        assertNotNull(DBConnectionFactory.newDefaultConnection());
    }

    @Test
    public void testCustomConn() {
        assertNotNull(DBConnectionFactory.newConnection("Hello World", "Test", "123", "test"));
    }
    @Test
    public void testClone() {
        DBConnection conn = DBConnectionFactory.newDefaultConnection();
        assertNotNull(DBConnectionFactory.cloneConnection(conn));
    }
}
