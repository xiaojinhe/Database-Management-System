package db;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestTableReader {

    @Test
    public void testReadTableAndPrint() {
        TableReader records = new TableReader("records");
        records.print();
    }

    @Test
    public void testOtherMethods() {
        TableReader records = new TableReader("records");
        assertEquals(5, records.getColNum());
        assertEquals("TeamName", records.getColumnName(0));
        assertEquals("'Golden Bears',2016,5,7,0", records.getNextRow(records.getColumns()).toString());
        Column[] cols1 = new Column[]{new Column("TeamName", "string", records.table),
        new Column("Ties", "int", records.table)};
        Column[] cols2 = new Column[]{new Column("TeamName", "string", records.table),
                new Column("Season", "int", records.table), new Column("Ties", "int", records.table)};
        assertEquals("'Golden Bears',0", records.getNextRow(cols1).toString());
        assertEquals("'Golden Bears',2014,0", records.getNextRow(cols2).toString());
        assertEquals(Integer.MAX_VALUE, records.getNextRow(records.getColumns()).getValue(3).getValue());
    }
}
