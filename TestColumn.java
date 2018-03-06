package db;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestColumn {
    @Test
    public void testToString() {
        String[] columnNames = new String[]{"a int", "b int", "c int"};
        Table t = new Table(columnNames);
        Column a = new Column("a", "int", t);
        Column b = new Column("b", "string", t);
        assertEquals("a int", a.toString());
        assertEquals("b string", b.toString());
    }

    @Test
    public void testGetColNameAndColType() {
        String[] columnNames = new String[]{"a int", "b int", "c float"};
        Table t = new Table(columnNames);
        Column a = new Column("a",  "int", t);
        Column b = new Column("b", "string", t);
        Column c = new Column("c", "float", t);
        assertEquals("a", a.getColName());
        assertEquals("int", a.getColType());
        assertEquals("b", b.getColName());
        assertEquals("string", b.getColType());
        assertEquals("c", c.getColName());
        assertEquals("float", c.getColType());
    }

}
