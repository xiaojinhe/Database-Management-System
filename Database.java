package db;

import java.util.*;
import static db.Utils.*;

/** Represents a collection of Tables, indexed by name.
 * @author Xiaojin He
 * */

public class Database {
    private static HashMap<String, Table> tables;
    /** An empty database. */
    public Database() {
        tables = new HashMap<>();
    }

    /** Sets or replaces the table in this, with given name and table. And the given name and table
     * should not be null.
     */
    public void put(String tableName, Table table) {
        if (tableName == null || table == null) {
            throw new IllegalArgumentException("tableName and table should not be null.");
        }
        tables.put(tableName, table);
    }

    /** Returns the table whose name is the same as the given tableName, if it exists in this.tables.
     * Otherwise, return null.
     */
    public Table get(String tableName) {
        if (tables.containsKey(tableName)) {
            return tables.get(tableName);
        } else {
            throw error("There is no table with the name: %s in the database.", tableName);
        }
    }

    public String transact(String query) {
        return CommandParse.eval(query);
    }

    public int size() {
        return tables.size();
    }

    public Set<String> getTableNames() {
        return tables.keySet();
    }

    static Table createNewTable(String name, String[] colNamesWithTypes) {
        if (colNamesWithTypes == null || colNamesWithTypes.length == 0) {
            throw error("Cannot create a table without columns provided.");
        }

        Table table = new Table(colNamesWithTypes);
        tables.put(name, table);
        return table;
    }

    static String loadTable(String name) {
        Table t = Table.readTable(name);
        tables.put(name, t);
        return "";
    }

    static String storeTable(String name) {
        if (tables.containsKey(name)) {
            Table table = tables.get(name);
            table.storeTable(name);
            return "";
        } else {
            throw error("No such table %s.", name);
        }
    }

    static String dropTable(String name) {
        if (tables.containsKey(name)) {
            tables.remove(name);
            return "";
        } else {
            throw error("Table %s is not found.", name);
        }
    }

    private static Row rowLineToRow(String rowLine) {
        String[] rowItemStr = rowLine.split(",");
        Value[] rowData = new Value[rowItemStr.length];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = new Value(CommandParse.stringToValue(rowItemStr[i]));
        }
        return new Row(rowData);
    }

    static String insertRow(String name, String rowLine) {
        if (tables.containsKey(name)) {
            Row row = rowLineToRow(rowLine);
            Table table = tables.get(name);
            if (table.add(row)) {
                return "";
            } else {
                return "The row is already in the table.";
            }
        } else {
            throw error("Table %s is not found.", name);
        }
    }

    static String printTable(String name) {
        if (tables.containsKey(name)) {
            return tables.get(name).toString();
        } else {
            throw error("Table %s is not found.", name);
        }
    }


    static Table select(String name, String[] columns, String[] tableNames, List<ConditionParse> condParse) {
        List<Table> tableList = new ArrayList<>();
        for (String t : tableNames) {
            if (tables.containsKey(t)) {
                tableList.add(tables.get(t));
            } else {
                throw error("Table %s does not exist in database", t);
            }
        }

        if (tableList.size() == 0) {
            throw error("No valid table selected.");
        }

        Table tableAll = tableList.remove(0); //get the first table in the tableLis

        if (tableList.size() != 0) {
            for (Table t : tableList) {
                tableAll = tableAll.joinTable(t); //join all the tables in the tableList
            }
        }

        if (tableAll.rowNum() == 0) {
            throw error("There is no any matched item in common columns.");
        }

        List<String> tableAllColNames = new ArrayList<>(Arrays.asList(tableAll.columnNames));
        List<OperationParse> opParses = null;

        if (columns[0].equals("*")) {
            columns = tableAllColNames.toArray(new String[tableAllColNames.size()]);
        } else {
            for (int i = 0; i < columns.length; i++) {
                if (!tableAllColNames.contains(columns[i])) {
                    OperationParse op = new OperationParse(columns[i]);
                    opParses = new ArrayList<>();
                    opParses.add(op);
                    columns[i] = op.newCol;
                }
            }
        }

        Table res = tableAll.selectOp(columns, opParses);
        res = res.select(res.columnNames, condParse);

        if (name != null) {
            tables.put(name, res);
        }
        return res;
    }
}
