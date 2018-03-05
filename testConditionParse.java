package db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class testConditionParse {

    @Test
    public void test() {
        String a = "TeamName > H";
        ConditionParse a1 = new ConditionParse(a);
        assertEquals("TeamName", a1.col1);
        assertEquals(">", a1.comparison);
        assertEquals("H", a1.colOrLiteral);

        String b = "   Wins   <=   5  ";
        ConditionParse b1 = new ConditionParse(b);
        assertEquals("Wins", b1.col1);
        assertEquals("<=", b1.comparison);
        assertEquals("5", b1.colOrLiteral);
        System.out.println(b1);
    }
}
