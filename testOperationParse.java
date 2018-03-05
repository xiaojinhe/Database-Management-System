package db;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class testOperationParse {

    @Test
    public void test() {
        String a = " Wins + Losses as Total ";
        OperationParse a1 = new OperationParse(a);
        assertEquals("Wins", a1.col1);
        assertEquals("+", a1.operator);
        assertEquals("Losses", a1.colOrLiteral);
        assertEquals("Total", a1.newCol);
        System.out.println(a1);

        String b = "Wins+5 as NewWins";
        OperationParse b1 = new OperationParse(b);
        assertEquals("Wins", b1.col1);
        assertEquals("+", b1.operator);
        assertEquals("5", b1.colOrLiteral);
        assertEquals("NewWins", b1.newCol);
        System.out.println(b1);
    }

}
