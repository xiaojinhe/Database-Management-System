package db;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class testDatabase {

    @Test
    public void testDatabaseConstructorAndPublicMethods() {
        Database db = new Database();
        Table records = Table.readTable("examples/records");
        db.put("records", records);
        db.get("records").print();
        Table teams = Table.readTable("examples/teams");
        db.put("teams", teams);
        db.get("teams").print();
    }

    @Test
    public void testLoadPrintStoreInsertRow() {
        Database db = new Database();
        Table records = Table.readTable("records");
        db.put("records", records);
        Table teams = Table.readTable("examples/teams");
        db.put("teams", teams);
        db.transact("load records");
        db.transact("insert into records values 'Patriots',2014,12,4,0");
        db.transact("store records");
        db.transact("insert into records values 'Patriots',2015,12,4,0");
        db.transact("store records");
        System.out.println(db.transact("print records"));
        assertEquals(8, db.get("records").rowNum());
    }

    @Test
    public void testCreateNewTable() {
        Database db = new Database();
        db.transact("create table t1 (x int, y int)");
        assertEquals(2, db.get("t1").getColumnNum());
        assertEquals(0, db.get("t1").rowNum());
        db.transact("insert into t1 values 2,5");
        db.transact("insert into t1 values 8,3");
        db.transact("insert into t1 values 13,7");
        assertEquals(3, db.get("t1").rowNum());
        System.out.println(db.transact("print t1"));
        db.transact("create table t2 (x int, z int)");
        db.transact("insert into t2 values 2,4");
        db.transact("insert into t2 values 8,9");
        db.transact("insert into t2 values 10,1");
        assertEquals(3, db.get("t2").rowNum());
        System.out.println(db.transact("print t2"));
    }

    @Test
    public void testDropTable() {
        Database db = new Database();
        db.transact("create table t1 (x int, y int)");
        db.transact("insert into t1 values 2,5");
        db.transact("insert into t1 values 8,3");
        db.transact("insert into t1 values 13,7");
        db.transact("create table t2 (x int, z int)");
        db.transact("insert into t2 values 2,4");
        db.transact("insert into t2 values 8,9");
        db.transact("insert into t2 values 10,1");
        db.transact("drop table t1");
        assertEquals(1, db.size());
        System.out.println(db.getTableNames());
        db.transact("create table t1 (x int, y int)");
        db.transact("insert into t1 values 2,5");
        db.transact("insert into t1 values 8,3");
        db.transact("insert into t1 values 13,7");
        assertEquals(2, db.size());
        System.out.println(db.getTableNames());
    }

    @Test
    public void testSelect() {

    }


}

