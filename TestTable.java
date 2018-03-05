package db;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestTable {
    @Test
    public void testTableAndBasicMethods() {
        String[] columnNames = new String[]{"TeamName string", "Season int", "Wins int", "Losses int", "Ties int"};
        Table records = new Table(columnNames);
        assertEquals(5, records.getColumnNum());
        assertEquals("Season", records.getColName(1));
        assertEquals(0, records.rowNum());
        assertEquals("string", records.columns[0].getColType());
        assertEquals("int", records.columns[4].getColType());
        assertEquals(2, records.findColIndex("Wins"));

        Column[] columns = new Column[]{new Column("Season", "int", records), new Column("Wins", "int", records)};
        Table b = new Table(columns);
        assertEquals(2, b.getColumnNum());
        assertEquals("Season", b.getColName(0));
        assertEquals("int", b.columns[1].getColType());
    }

    @Test
    public void testRowRelatedMethodsAndPrintAndWriteTable() {
        String[] columnNames = new String[]{"TeamName string", "Season int", "Wins int", "Losses int", "Ties int"};
        Table records = new Table(columnNames);
        Row a = new Row(new Value[]{new Value("'Golden Bears'"), new Value(2016), new Value(5), new Value(7),
                new Value(0)});
        Row b = new Row(new Value[]{new Value("'Golden Bears'"), new Value(2015), new Value(8), new Value(5),
                new Value(0)});
        Row c = new Row(new Value[]{new Value("'Golden Bears'"), new Value(2014), new Value(5), new Value(7),
                new Value(0)});
        Row d = new Row(new Value[]{new Value("'Steelers'"), new Value(2015), new Value(10), new Value("NaN"),
                new Value(0)});
        Row e = new Row(new Value[]{new Value("'Steelers'"), new Value(2014), new Value(11), new Value(5),
                new Value(0)});
        Row f = new Row(new Value[]{new Value("'Steelers'"), new Value(2013), new Value(8), new Value(8),
                new Value(0)});
        Row g = new Row(new Value[]{new Value("'Golden Bears'"), new Value(2014), new Value(5), new Value(7),
                new Value(0)});
        records.add(a);
        records.add(b);
        records.add(c);
        records.add(d);
        records.add(e);
        records.add(f);
        assertEquals(6, records.rowNum());
        assertEquals(true, records.hasRow());
        assertEquals(a.toString(), records.getNextRow(records.columns).toString());
        assertEquals(b.toString(), records.getNextRow(records.columns).toString());
        assertEquals(c.toString(), records.getNextRow(records.columns).toString());
        assertEquals(d.toString(), records.getNextRow(records.columns).toString());
        assertEquals(e.toString(), records.getNextRow(records.columns).toString());
        assertEquals(f.toString(), records.getNextRow(records.columns).toString());
        assertEquals(g, records.getIthRow(2));
        assertEquals(Integer.MAX_VALUE, records.getIthRow(3).getValue(3).value);
        records.print();
        records.storeTable("records");
    }

    @Test
    public void testReadTableAndPrint() {
        Table records = Table.readTable("records");
        records.print();
        assertEquals(5, records.getColumnNum());
        assertEquals("'Golden Bears',2016,5,7,0", records.getNextRow(records.getColumns()).toString());
        Column[] cols1 = new Column[]{new Column("TeamName", "string", records),
                new Column("Ties", "int", records)};
        Column[] cols2 = new Column[]{new Column("TeamName", "string", records),
                new Column("Season", "int", records), new Column("Ties", "int", records)};
        assertEquals("'Golden Bears',0", records.getNextRow(cols1).toString());
        assertEquals("'Golden Bears',2014,0", records.getNextRow(cols2).toString());
        assertEquals(Integer.MAX_VALUE, records.getNextRow(records.getColumns()).getValue(3).getValue());
    }

    @Test
    public void testTableToString() {
        Table records = Table.readTable("records");

        System.out.println(records);
    }

    @Test
    public void testSelectOneTable() {
        Table records = Table.readTable("records");
        List<ConditionParse> conditions = new ArrayList<>();
        conditions.add(new ConditionParse(records.columnNames[1] + " > 2000" ));
        Table res1 = records.select(records.columnNames, conditions);
        res1.print();
        conditions.remove(0);
        conditions.add(new ConditionParse(res1.columnNames[2] + "!= 5"));
        Table res2 = res1.select(records.columnNames, conditions);
        res2.print();
        String[] subColNames = new String[]{"TeamName", "Season", "Wins"};
        Table res3 = records.select(subColNames, conditions);
        res3.print();
        conditions.remove(0);
        conditions.add(new ConditionParse(records.columnNames[2] + " <= " + records.columnNames[3]));
        Table res4 = records.select(records.columnNames, conditions);
        res4.print();
    }

    @Test
    public void testSelectTwoTable() {
        Table t1 = Table.readTable("examples/t1");
        Table t2 = Table.readTable("examples/t2");
        List<String> columnOft1t2 = new ArrayList<>();
        columnOft1t2.addAll(Arrays.asList(t1.columnNames));
        for (String s2 : t2.columnNames) {
            if (!columnOft1t2.contains(s2)) {
                columnOft1t2.add(s2);
            }
        }
        Table select1 = t1.select(t2, columnOft1t2.toArray(new String[columnOft1t2.size()]), null);
        select1.print();

        Table t4 = Table.readTable("examples/t4");
        List<String> columnOft1t4 = new ArrayList<>();
        columnOft1t4.addAll(Arrays.asList(t1.columnNames));
        for (String s4 : t4.columnNames) {
            if (!columnOft1t4.contains(s4)) {
                columnOft1t4.add(s4);
            }
        }
        Table select2 = t4.select(t1, columnOft1t4.toArray(new String[columnOft1t4.size()]), null);
        select2.print();

        String[] conditionStrs = new String[] {"x >= 2 ", "b > 0"};
        List<ConditionParse> conditions = new ArrayList<>();
        for (String c : conditionStrs) {
            ConditionParse condP = new ConditionParse(c);
            conditions.add(condP);
        }
        t4.select(t1, columnOft1t4.toArray(new String[columnOft1t4.size()]), conditions).print();

        Table t5 = Table.readTable("examples/t5");
        Table t6 = Table.readTable("examples/t6");
        List<String> columnOft5t6 = new ArrayList<>();
        columnOft5t6.addAll(Arrays.asList(t5.columnNames));
        for (String s6 : t6.columnNames) {
            if (!columnOft5t6.contains(s6)) {
                columnOft5t6.add(s6);
            }
        }
        Table select3 = t5.select(t6, columnOft5t6.toArray(new String[columnOft5t6.size()]), null);
        select3.print();

        Table t7 = Table.readTable("examples/t7");
        Table t8 = Table.readTable("examples/t8");
        List<String> columnOft7t8 = new ArrayList<>();
        columnOft7t8.addAll(Arrays.asList(t7.columnNames));
        for (String s8 : t8.columnNames) {
            if (!columnOft7t8.contains(s8)) {
                columnOft7t8.add(s8);
            }
        }
        Table select4 = t7.select(t8, columnOft7t8.toArray(new String[columnOft7t8.size()]), null);
        select4.print();

        Table t9 = Table.readTable("examples/t9");
        Table t10 = Table.readTable("examples/t10");
        List<String> columnOft9t10 = new ArrayList<>();
        columnOft9t10.addAll(Arrays.asList(t9.columnNames));
        for (String s8 : t10.columnNames) {
            if (!columnOft9t10.contains(s8)) {
                columnOft9t10.add(s8);
            }
        }
        Table select5 = t9.select(t10, columnOft9t10.toArray(new String[columnOft9t10.size()]), null);
        select5.print();

    }

    @Test
    public void testJoinTable() {
        Table t1 = Table.readTable("examples/t1");
        Table t2 = Table.readTable("examples/t2");
        Table join12 = t1.joinTable(t2);
        join12.print();

        Table t4 = Table.readTable("examples/t4");
        Table join14 = t4.joinTable(t1);
        join14.print();

        List<Table> tables = new ArrayList<>();
        tables.add(t2);
        tables.add(t4);
        Table newTable = t1;
        for (Table t : tables) {
            newTable = newTable.joinTable(t);
        }
        newTable.print();

        List<Table> tables1 = new ArrayList<>();
        tables.add(t2);
        tables.add(t1);
        Table newTable1 = t4;
        for (Table t : tables) {
            newTable1 = newTable1.joinTable(t);
        }
        newTable1.print();

    }

}

