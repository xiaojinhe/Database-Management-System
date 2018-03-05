package db;

import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestColumn {
    @Test
    public void testToString() {
        Column a = new Column("a", "int");
        Column b = new Column("b", "string");
        Column c = new Column("c", "a");
        assertEquals("a int", a.toString());
        assertEquals("b string", b.toString());
        assertEquals(null, c.toString());
    }

    @Test
    public void testGetColNameAndColType() {
        Column a = new Column("a",  "int");
        Column b = new Column("b", "string");
        Column c = new Column("c", "a");
        assertEquals("a", a.getColName());
        assertEquals("int", a.getColType());
        assertEquals("b", b.getColName());
        assertEquals("string", b.getColType());
        assertEquals(null, c.getColName());
        assertEquals(null, c.getColType());
    }

    @Test
    public void testEquals() {


    }

}
