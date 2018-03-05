package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static db.Utils.error;

public class TableReader {
    /** Reads the contents of the file NAME.tbl, and returns as a Table.
     *  Format errors in the file cause a DBException. */
    Table table;
    public TableReader(String fileName) {
        readTable(fileName);
    }
    void readTable(String name) {

        try (Scanner input = new Scanner(new File(name + ".tbl"))) {
            String colNames = input.nextLine();
            if (colNames == null) {
                throw error("Empty file: %s.tbl", name);
            }

            String[] columnNames = colNames.split(",");
            //String[] columnTypes = new String[columnNames.length];
            //for (int i = 0; i < columnNames.length; i++) {
            //    columnTypes[i] = columnNames[i].split(" ")[1];
            //}

            table = new Table(columnNames);
            while (input.hasNext()) {
                String[] newLine = input.nextLine().split(",");
                Value[] row = new Value[newLine.length];
                for (int i = 0; i < row.length; i++) {
                    Value val;
                    if (table.columns[i].getColType().equals("int")) {
                        if (newLine[i].equals("NaN")) {
                            val = new Value(Integer.MAX_VALUE);
                            val.NaN = true;
                        } else if (newLine[i].equals("NOVALUE")) {
                            val = new Value(0);
                            val.NOVALUE = true;
                        } else {
                            val = new Value(Integer.parseInt(newLine[i]));
                        }
                    } else if (table.columns[i].getColType().equals("string")) {
                        if (newLine[i].equals("NOVALUE")) {
                            val = new Value("");
                            val.NOVALUE = true;
                        } else {
                            val = new Value(newLine[i]);
                        }
                    } else {
                        if (newLine[i].equals("NaN")) {
                            val = new Value(Float.MAX_VALUE);
                            val.NaN = true;
                        } else if (newLine[i].equals("NOVALUE")) {
                            val = new Value((float) 0.0);
                            val.NOVALUE = true;
                        } else {
                            val = new Value(Float.parseFloat(newLine[i]));
                        }
                    }
                    row[i] = val;
                }
                table.add(new Row(row));
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.", name);
        } catch (IOException e) {
            throw error("problem reading from %s.", name);
        }
    }

    /** Returns columns of the table read. */
    Column[] getColumns() {
        return table.columns;
    }

    /** Return the number of columns in the table read. */
    int getColNum() {
        return table.getColumnNum();
    }

    /** Return the number of rows in the table read. */
    int getRowNum() {
        return table.rowNum();
    }

    /** Returns the name of ith column in the Table, where 0 <= i < columnNames.length. */
    String getColumnName(int i) {
        return table.getColName(i);
    }

    /** Returns the index of the column whose name is the same as the parameter entered, or -1 if
     *  there isn't one. */
    int findColumnIndex(String name) {
        return table.findColIndex(name);
    }

    /** Adds a new row to the table read, return true if added, or false. */
    boolean addRow(Row row) {
        return table.add(row);
    }

    void print() {
        table.print();
    }

    Row getNextRow(Column[] cols) {
        return table.getNextRow(cols);
    }
}
