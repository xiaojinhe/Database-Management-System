package db;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class TestOperation {

    @Test
    public void testOperationOneTable() {
        Table records = Table.readTable("examples/records");

        List<OperationParse> ops = new ArrayList<>();
        ops.add(new OperationParse("Ties + 5 as TiePlus"));
        String[] columnNames = new String[]{"TeamName", "Season", "Wins", "Losses", "TiePlus"};
        Table recordNew = records.selectOp(columnNames, ops);
        recordNew.print();

        ops.remove(0);
        ops.add(new OperationParse(records.columnNames[2] + "/" + records.columnNames[3] + " as Ratio"));
        String[] columnNames1 = new String[]{"TeamName", "Season", "Wins", "Losses", "Ties", "Ratio"};
        Table recordNew1 = records.selectOp(columnNames1, ops);
        recordNew1.print();
    }

    @Test
    public void testOperationTwoTables() {
        Table teams = Table.readTable("examples/teams");
        Table records = Table.readTable("examples/records");

        List<OperationParse> ops = new ArrayList<>();
        ops.add(new OperationParse(records.columnNames[2] + "/" + records.columnNames[3] + " as Ratio"));
        String[] newColumns = new String[] {"City", "Season", "Ratio"};
        Table res1 = teams.selectOp(records, newColumns, ops);
        res1.print();

        ops.remove(0);
        ops.add(new OperationParse("Wins - Losses as Wins-Losses"));
        String[] newColumns2 = new String[] {"TeamName", "Season", "Wins-Losses"};
        Table res2 = records.selectOp(teams, newColumns2, ops);
        res2.print();
    }
}
