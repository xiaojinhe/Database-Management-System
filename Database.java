package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        }
        return null;
    }

    public String transact(String query) {
        String result = CommandTransaction.eval(query);
        return result;
    }

    static Table createNewTable(String name, String[] colNamesWithTypes) {
        if (colNamesWithTypes == null || colNamesWithTypes.length == 0) {
            throw error("Cannot create a table with no columns.");
        }

        for (String colName : colNamesWithTypes) {

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
            throw error("ERROR: No such table %s.", name);
        }
    }

    static String dropTable(String name) {
        if (tables.containsKey(name)) {
            tables.remove(name);
            return "";
        } else {
            throw error("ERROR: Table %s is not found.", name);
        }
    }

    private static Row rowLineToRow(String rowLine) {
        String[] rowItemStr = rowLine.split(",");
        Value[] rowData = new Value[rowItemStr.length];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = new Value(CommandTransaction.stringToValue(rowItemStr[i]));
        }
        Row row = new Row(rowData);
        return row;
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
            throw error("ERROR: Table %s is not found.", name);
        }
    }

    static String printTable(String name) {
        if (tables.containsKey(name)) {
            return tables.get(name).toString();
        } else {
            throw error("ERROR: Table %s is not found.", name);
        }
    }


   static Table select(String name, String[] columnNames, /*String[] columnWithAlias, String[] opColNames, String[] opColAlias,*/
                        String[] tableNames, String[] conditionStrs) {
       /** List<Table> tableList = new ArrayList<>();
        for (String t : tableNames) {
            if (tables.containsKey(t)) {
                tableList.add(tables.get(t));
            } else {
                throw error("ERROR: Table %s does not exist in db", t);
            }
        }
        Table table1;
        if (tableList.size() >= 1) {
            table1 = tableList.remove(0);
        } else {
            throw error("ERROR: No valid table selected.");
        }

        for (Table t : tableList) {
            List<String> colNamesForTwo = new ArrayList<>();
            List<String> colAliasForTwo = new ArrayList<>();
            List<String> opColNamesForTwo = new ArrayList<>();
            List<String> opColAliasForTwo = new ArrayList<>();

            for (String columnName : columnNames) {
                for (String table1ColName : table1.columnNames) {
                    if (columnName.equals(table1ColName)) {
                        colNamesForTwo.add(columnName);
                    }
                }
                for (String tColName : t.columnNames) {
                    if (columnName.equals(tColName)) {
                        colNamesForTwo.add(columnName);
                    }
                }
            }

            for (int i = 0; i < opColAlias.length; i++) {
                int j = i * 2;
                for (String colForTwo : colNamesForTwo) {
                    if (opColNames[j].equals(colForTwo)) {

                    }
                }
            }

            for (String col : columnWithAlias) {
                for (String table1ColName : table1.columnNames) {
                    if (columnName.equals(table1ColName)) {
                        colNamesForTwo.add(columnName);
                    }
                }
                for (String tColName : t.columnNames) {
                    if (columnName.equals(tColName)) {
                        colNamesForTwo.add(columnName);
                    }
                }
            }
            table1 = table1.selectOp(t, colNamesForTwo.toArray(), )

        }*/
        return null;

    }

}
