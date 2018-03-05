package db;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestRow {

    @Test
    public void testRowAndGetValue() {
        Value a = new Value(10);
        Value b = new Value("'str'");
        Value c = new Value((float) 0.3);
        Value d = new Value("NOVALUE");
        Row row = new Row(new Value[]{a, b, c, d});
        assertEquals(new Value(10), row.getValue(0));
        assertEquals(new Value("'str'"), row.getValue(1));
        assertEquals(new Value((float) 0.3), row.getValue(2));
        assertEquals(new Value("NOVALUE"), row.getValue(3));
    }

    @Test
    public void testSize() {
        Row row = new Row(new Value[]{new Value("'str'"), new Value(5), new Value((float) 0.5), new Value("NOVALUE")});
        assertEquals(4, row.size());
        Row emptyRow = new Row(new Value[]{});
        assertEquals(0, emptyRow.size());
    }

    @Test
    public void testEquals() {
        Row a = new Row(new Value[]{new Value(10), new Value("'str'"), new Value((float) 0.3), new Value("NOVALUE")});
        Row b = new Row(new Value[]{new Value(10), new Value("'str'"), new Value((float) 0.3), new Value("NOVALUE")});
        Row c = new Row(new Value[]{new Value("'str'"), new Value(5), new Value((float) 0.5), new Value("NOVALUE")});
        Row d = null;
        Row e = new Row(new Value[]{});
        assertEquals(true, a.equals(b));
        assertEquals(false, a.equals(c));
        assertEquals(false, a.equals(d));
        assertEquals(false, a.equals(e));
    }

    @Test
    public void testToString() {
        Row row = new Row(new Value[]{new Value(10), new Value("'str'"), new Value((float) 0.3), new Value("NOVALUE")});
        assertEquals("10,'str',0.3,NOVALUE", row.toString());
        Row emptyRow = new Row(new Value[]{});
        assertEquals("", emptyRow.toString());
    }
}
