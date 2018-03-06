package db;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestCondition {

    @Test
    public void testConditionConstructor() {
        TableReader records = new TableReader("records");
        Condition unary1 = new Condition(records.getColumns()[0], "<=", new Value("'H'"));
        Condition unary2 = new Condition(records.getColumns()[1], ">", new Value(2000));
        Row[] rows = new Row[records.getRowNum()];
        for (int i = 0; i < records.getRowNum(); i++) {
            rows[i] = records.getNextRow(records.getColumns());
        }
        assertEquals(true, unary1.testCondition(rows));
        assertEquals(true, unary2.testCondition(rows));
    }

}
