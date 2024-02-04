package org.hmdms.hmmanager.db;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class DBQueryBuilderTest {

    private final Logger logger = LoggerFactory.getLogger(DBQueryBuilderTest.class);

    @Test
    public void testBuildSelect() {
        LinkedList<QueryArgument> l = new LinkedList<>();
        l.add(new QueryArgument("1", QueryArgumentTypes.INT, "int"));
        l.add(new QueryArgument("2.5", QueryArgumentTypes.FLOAT, "float"));
        l.add(new QueryArgument("Hello World", QueryArgumentTypes.STRING, "string"));

        LinkedList<String> c = new LinkedList<>();
        c.add("int");
        c.add("float");
        c.add("string");
        c.add("othercolumn");
        long start = System.currentTimeMillis();
        String selectResult = DBQueryBuilder.buildSelect("table", l, c);
        long result = System.currentTimeMillis() - start;
        logger.info("Got string " + selectResult + "\nTime elapsed: " + result + "ms");
        assertEquals("SELECT [int], [float], [string], [othercolumn] FROM [table] WHERE [int] = 1 AND [float] = 2.5 AND [string] = 'Hello World';", selectResult);
    }

    @Test
    public void testBuildUpdate() {
        LinkedList<QueryArgument> l = new LinkedList<>();
        l.add(new QueryArgument("1", QueryArgumentTypes.INT, "int"));
        l.add(new QueryArgument("2.5", QueryArgumentTypes.FLOAT, "float"));
        l.add(new QueryArgument("Hello World", QueryArgumentTypes.STRING, "string"));

        LinkedList<QueryArgument> f = new LinkedList<>();
        f.add(new QueryArgument("17", QueryArgumentTypes.INT, "id"));

        long start = System.currentTimeMillis();
        String sResult = DBQueryBuilder.buildUpdateQuery("table", l, f);
        long result = System.currentTimeMillis() - start;

        logger.info("Got string " + sResult + "\nTime elapsed: " + result + "ms");
        assertEquals("UPDATE [table] SET [int] = 1, [float] = 2.5, [string] = 'Hello World' WHERE [id] = 17;", sResult);
    }
}
