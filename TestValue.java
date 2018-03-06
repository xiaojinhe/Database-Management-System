package db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestValue {
    @Test
    public void testToString() {
        Value a = new Value(10);
        Value b = new Value("NOVALUE");
        Value c = new Value((float) 0.3);
        Value d = new Value("'str'");
        assertEquals("10", a.toString());
        assertEquals("NOVALUE", b.toString());
        assertEquals("0.3", c.toString());
        assertEquals("'str'", d.toString());
    }

    @Test
    public void testGetValueType() {
        Value a = new Value(10);
        Value b = new Value("NOVALUE");
        Value c = new Value((float) 0.3);
        assertEquals("int", a.getValueType());
        assertEquals("string", b.getValueType());
        assertEquals("float", c.getValueType());
    }

    @Test
    public void testGetValue() {
        Value a = new Value(10);
        Value b = new Value("NOVALUE");
        Value c = new Value((float) 0.3);
        assertEquals(10, a.getValue());
        assertEquals("NOVALUE", b.getValue());
        assertEquals((float) 0.3, c.getValue());
    }

    @Test
    public void testEquals() {
        Value a = new Value(10);
        Value b = new Value("NOVALUE");
        Value c = new Value((float) 0.3);
        assertEquals(10, a.getValue());
        assertEquals("NOVALUE", b.getValue());
        assertEquals((float) 0.3, c.getValue());
        assertNotEquals("NOVALUE", c.getValue());
    }

    @Test
    public void testDuplicate() {
        Value a = new Value(10);
        Value b = new Value("b");
        Value c = new Value((float) 0.3);
        Value a1 = new Value(10);
        Value b1 = new Value(b.value);
        Value c1 = new Value((float) 0.3);
        assertEquals(a1, a);
        assertEquals(b1, b);
        b.value = "str";
        assertNotEquals(b, b1);
        assertEquals(c1, c);
    }

    @Test
    public void testCompareTo() {
        Value a = new Value(Integer.MAX_VALUE);
        Value b = new Value("bird");
        Value c = new Value((float) 0.3);
        Value a1 = new Value(6);
        Value b1 = new Value("apple");
        Value c1 = new Value((float) 15.5);
        int aToa1 = a.compareTo(a1);
        int bTob1 = b.compareTo(b1);
        int cToc1 = c.compareTo(c1);
        int aToc = a.compareTo(c);
        int aToc1 = a.compareTo(c1);
        System.out.println(aToa1);
        System.out.println(bTob1);
        System.out.println(cToc1);
        System.out.println(aToc);
        System.out.println(aToc1);
        System.out.println(((Number) a.value).doubleValue());
        assertEquals(true, aToa1 > 0);
        assertEquals(true, bTob1 > 0);
        assertEquals(false, cToc1 > 0);
        assertEquals(true, aToc > 0);
        assertEquals(true, aToc1 > 0);
    }
}


