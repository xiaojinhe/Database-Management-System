package db;

import edu.princeton.cs.introcs.In;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static db.Utils.*;

/**
 * A table in a database, which implements Iterable.
 */
class Table implements Iterable<Row> {

    /** Column titles. */
    String[] columnNames;
    /** Column array. */
    Column[] columns;
    /** Row lists. */
    List<Row> rows;
    String[] columnTypes;
    //String[] columnNamesWithTypes;

    private int iteratorRow;

    /** A new Table whose columns are provided by Column[] columns,
     *  which must be distinct (else throw an exception). */
    Table(String[] columnNamesWithTypes) {
        this.rows = new ArrayList<>();
        iteratorRow = 0;

        for (int i = 0; i < columnNamesWithTypes.length; i++) {
            for (int j = i + 1; j < columnNamesWithTypes.length; j++) {
                if (columnNamesWithTypes[i].equals(columnNamesWithTypes[j])) {
                    throw error("Duplicate column name: %s", columnNamesWithTypes[i]);
                }
            }
        }

        this.columnNames = new String[columnNamesWithTypes.length];
        this.columnTypes = new String[columnNamesWithTypes.length];
        for (int i = 0; i < columnNamesWithTypes.length; i++) {
            this.columnNames[i] = columnNamesWithTypes[i].split("\\s+")[0];
            this.columnTypes[i] = columnNamesWithTypes[i].split("\\s+")[1];
        }

        columns = new Column[columnNamesWithTypes.length];
        for (int i = 0; i < columnNamesWithTypes.length; i++) {
            columns[i] = new Column(this.columnNames[i], this.columnTypes[i], this);
        }
    }

    /** A new Table whose Columns are give by an array. */
    Table(Column[] columns) {
        this.rows = new ArrayList<>();
        this.columns = columns;
        this.columnNames = new String[columns.length];
        this.columnTypes = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            this.columnNames[i] = columns[i].getColName();
            this.columnTypes[i] = columns[i].getColType();
        }
        iteratorRow = 0;
    }

    /** A new Table whose Columns are give by a list. */
    Table(List<Column> columns) {
        this(columns.toArray(new Column[columns.size()]));
    }

    /** Return the number of columns in this table. */
    int getColumnNum() {
        return this.columns.length;
    }

    /** Returns columns of the table read. */
    Column[] getColumns() {
        return this.columns;
    }

    /** Returns the name of ith column in the Table, where 0 <= i < columnNames.length. */
    String getColName(int i) {
        if (i > this.columns.length || i < 0) {
            throw new IndexOutOfBoundsException("The given index " + i + " is out of range of columns.");
        }
        return columnNames[i];
    }

    /** Returns the index of the column whose name is the same as the parameter entered, or -1 if
     *  there isn't one. */
    int findColIndex(String name) {
        for (int index = 0; index < columnNames.length; index++) {
            if (name.equals(columnNames[index])) {
                return index;
            }
        }
        return -1;
    }

    /** Return the number of Rows in the table. */
    int rowNum() {
        return rows.size();
    }

    /** Returns an iterator that returns the rows. */
    @Override
    public Iterator<Row> iterator() {
        return rows.iterator();
    }

    /** Returns true if there is any Row data in the table, or false. */
    boolean hasRow() {
        return this.rows != null;
    }

    public Row getIthRow(int i) {
        Row ith = null;
        if (i < rows.size() && i >= 0) {
            ith = rows.get(i);
        }
        return ith;
    }

    public Row getNextRow(Column[] cols) {
        Row returnRow = null;
        if (iteratorRow < rowNum()) {
            Value[] res = new Value[cols.length];
            for (int i = 0; i < cols.length; i++) {
                int index = findColIndex(cols[i].getColName());
                Value add = rows.get(iteratorRow).getValue(index);
                res[i] = new Value(add.value);
                if (add.NOVALUE) {
                    res[i].NOVALUE = true;
                }
                if (add.NaN) {
                    res[i].NaN = true;
                }
            }
            returnRow = new Row(res);
            iteratorRow++;
        }
        return returnRow;
    }

    /**
     * Add a Row to the table if there is no equal row exists. Returns true if the Row is successfully added,
     * or false. */
    boolean add(Row row) {
        if (this.rows.contains(row)) {
            return false;
        }

        if (row.size() != this.getColumnNum()) {
            throw error("The items of the row is %s does not match the number of columns: %s."
                    , row.size(), this.getColumnNum());
        }

        int rowLength = row.size();
        for (int i = 0; i < rowLength; i++) {
            if (row.getValue(i).NOVALUE || row.getValue(i).NaN) {
                row.getValue(i).valueType = columns[i].getColType();
                if (columns[i].getColType().equals("int")) {
                    if (row.getValue(i).NOVALUE) {
                        row.getValue(i).value = 0;
                    } else {
                        row.getValue(i).value = Integer.MAX_VALUE;
                    }
                } else if (columns[i].getColType().equals("string")) {
                    if (row.getValue(i).NOVALUE) {
                        row.getValue(i).value = 0;
                    } else {
                        row.getValue(i).value = "";
                    }
                } else {
                    if (row.getValue(i).NOVALUE) {
                        row.getValue(i).value = (float) 0.0;
                    } else {
                        row.getValue(i).value = Float.MAX_VALUE;
                    }
                }
                continue;
            }
            if (!columns[i].getColType().equals(row.getValue(i).getValueType())) {
                throw error("Wrong type error: column type is %s while entered value type is %s.",
                        columns[i].getColType(), row.getValue(i).getValueType());
            }
        }

        this.rows.add(row);
        return true;
    }

    /** Reads the contents of the file NAME.tbl, and returns as a Table.
     *  Format errors in the file cause a DBException. */
    static Table readTable(String name) {
        Table table;
        table = null;
        try (Scanner input = new Scanner(new File(name + ".tbl"))) {
            String colNames = input.nextLine();
            if (colNames == null) {
                throw error("Empty file: %s.tbl", name);
            }

            String[] columnNamesWithTypes = colNames.split(",");

            table = new Table(columnNamesWithTypes);
            while (input.hasNext()) {
                String[] newLine = input.nextLine().split(",");
                Value[] row = new Value[newLine.length];
                for (int i = 0; i < row.length; i++) {
                    Value val;
                    switch (table.columns[i].getColType()) {
                        case "int":
                            switch (newLine[i]) {
                                case "NaN":
                                    val = new Value(Integer.MAX_VALUE);
                                    val.NaN = true;
                                    break;
                                case "NOVALUE":
                                    val = new Value(0);
                                    val.NOVALUE = true;
                                    break;
                                default:
                                    val = new Value(Integer.parseInt(newLine[i]));
                                    break;
                            }
                            break;
                        case "string":
                            if (newLine[i].equals("NOVALUE")) {
                                val = new Value("");
                                val.NOVALUE = true;
                            } else {
                                val = new Value(newLine[i]);
                            }
                            break;
                        default:
                            switch (newLine[i]) {
                                case "NaN":
                                    val = new Value(Float.MAX_VALUE);
                                    val.NaN = true;
                                    break;
                                case "NOVALUE":
                                    val = new Value((float) 0.0);
                                    val.NOVALUE = true;
                                    break;
                                default:
                                    val = new Value(Float.parseFloat(newLine[i]));
                                    break;
                            }
                            break;
                    }
                    row[i] = val;
                }
                table.add(new Row(row));
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.", name);
        }
        return table;
    }

    /** Store the contents of the table into a file with name. Any I/O errors
     *  cause a DBException. */
    void storeTable(String name) {
        try (FileWriter output = new FileWriter(new File(name + ".tbl"))){
            for (int i = 0; i < columns.length; i++) {
                output.write(this.columns[i].toString());
                if (i != columns.length - 1) {
                    output.write(",");
                }
            }
            output.write("\n");

            for (Row row : rows) {
                output.write(row.toString() + "\n");
            }

        } catch (IOException e) {
            throw error("Have trouble writing to %s.tbl.", name);
        }
    }

    /** Print the contents of the tale on the standard output. */
    void print() {
        for (int i = 0; i < columns.length; i++) {
            System.out.print(this.columns[i].toString());
            if (i != columns.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println();

        for (Row row : rows) {
            System.out.println(row);
        }
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < columns.length; i++) {
            res += this.columns[i].toString();
            if (i != columns.length - 1) {
                res += ",";
            }
        }
        res += "\n";

        for (Row row : rows) {
            res += row.toString() + "\n";
        }
        return res;
    }

    /** Returns a new Table selected from rows of the table after performed given arithmetic operation,
     * with the given colunms.

    Table selectOp(String[] columnNames, Operation op) {
        Table res = new Table(columnNames);

        for (Row row : this.rows) {
            Value[] data = new Value[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                int index = this.findColIndex(res.columnNames[i]);
                if (index != -1) {
                    data[i] = this.columns[index].getColumnValue(row);
                } else {
                    data[i] = op.arithmeticOperation(row);
                }
            }
            Row newRow = new Row(data);
            res.add(newRow);
        }
        return res;
    }
     */

    /** Returns a new Table selected from rows of the table after performed given arithmetic operation,
     * with the given colunms.
     */


    Table selectOp(String[] newColumnNames, List<OperationParse> ops) {

        List<Operation> operationList = Operation.operationsTrans(ops, this);

        String[] newColNameWithType = new String[newColumnNames.length];

        for (int i = 0; i < newColumnNames.length; i++) {
            int index = this.findColIndex(newColumnNames[i]);
            if (index != -1) {
                newColNameWithType[i] = this.columns[index].toString();
            } else {
                for (Operation op : operationList) {
                    if (newColumnNames[i].equals(op.aliasWithType.split(" ")[0])) {
                        newColNameWithType[i] = op.aliasWithType;
                    }
                }
            }
        }
        Table res = new Table(newColNameWithType);

        for (Row row : this.rows) {
            Value[] data = new Value[res.columns.length];
            for (int i = 0; i < res.columns.length; i++) {
                int index = this.findColIndex(newColumnNames[i]);
                if (index != -1) {
                    data[i] = this.columns[index].getColumnValue(row);
                } else {
                    for (Operation op : operationList) {
                        if (newColumnNames[i].equals(op.aliasWithType.split(" ")[0])) {
                            data[i] = op.arithmeticOperation(row);

                        }
                    }
                }
            }
            Row newRow = new Row(data);
            res.add(newRow);
        }
        return res;
    }

    /** List<Operation> operationParse(List<OperationParse> colOperations) {
        List<Operation> operationList = new ArrayList<>();
        if (colOperations == null || colOperations.isEmpty()) {
            return null;
        }
        for (OperationParse colOp : colOperations) {
            Column col1 = this.columns[findColIndex(colOp.col1)];
            int col2Index = findColIndex(colOp.colOrLiteral);
            Operation op;
            String newCol;
            if (col2Index == -1) {
                Value val = new Value(CommandTransaction.stringToValue(colOp.colOrLiteral));
                if (col1.getColType().equals("float") || val.getValueType().equals("float")) {
                    newCol = colOp.newCol + " float";
                } else {
                    newCol = colOp.newCol + " " + col1.getColType();
                }
                op = new Operation(col1, colOp.operator, val, newCol);
            } else {
                Column col2 = this.columns[col2Index];
                if (col1.getColType().equals("float") || col2.getColType().equals("float")) {
                    newCol = colOp.newCol + " float";
                } else {
                    newCol = colOp.newCol + " " + col1.getColType();
                }
                op = new Operation(col1, colOp.operator, col2, newCol);
            }
            operationList.add(op);
        }
        return operationList;
    } */

    List<Condition> conditionParse(List<ConditionParse> conditions) {
        List<Condition> conditionList = new ArrayList<>();
        if (conditions == null || conditions.isEmpty()) {
            return null;
        }
        for (ConditionParse c : conditions) {
            Column col1 = this.columns[findColIndex(c.col1)];
            int col2Index = findColIndex(c.colOrLiteral);
            Condition condition;
            if (col2Index == -1) {
                Value val = new Value(CommandTransaction.stringToValue(c.colOrLiteral));
                condition = new Condition(col1, c.comparison, val);
            } else {
                Column col2 = this.columns[col2Index];
                condition = new Condition(col1, c.comparison, col2);
            }
            conditionList.add(condition);
        }
        return conditionList;
    }

    /** Returns a new Table selected from rows of the table that satisfy the given conditions,
     * with the given colunms.
     */
    Table select(String[] columnNames, List<ConditionParse> conditions) {
        List<Column> allCols = getAllColumns(columnNames, this);

        List<Condition> conditionList = this.conditionParse(conditions);
        Table res = new Table(allCols);
        for (Row row : this.rows) {
            if ( conditions == null || conditions.isEmpty() || Condition.test(conditionList, row)) {
                Row newRow = new Row(res.columns, row);
                res.add(newRow);
            }
        }
        return res;
    }

    /** Returns true if the column lists common1 from row1 and common2 from row2 all have identical values. Assumes
     * that common1 and common2 have the same number of elements and the same names, that the columns in common1
     * apply to this table, those in common2 to another, and that row1 and row2 come, respectively, from those
     * tables.
     */
    private static boolean join(List<Column> commonCol1, List<Column> commonCol2, Row row1, Row row2) {
        for (int i = 0; i < commonCol1.size(); i++) {
            Column col1 = commonCol1.get(i);
            Value col1Value = col1.getColumnValue(row1);
            Column col2 = commonCol2.get(i);
            Value col2Value = col2.getColumnValue(row2);
            if (!col1Value.equals(col2Value)) {
                return false;
            }
        }
        return true;
    }

    Table selectOp(Table table2, String[] newColumnNames, List<OperationParse> ops) {
        Table combine = this.joinTable(table2);
        Table res = combine.selectOp(newColumnNames, ops);
        return res;

    }

    /** returns a list of columns with the same column names in String[] columnNames, from this table and table2. */
    List<Column> getAllColumns(String[] columnNames, Table... tables) {
        List<Column> allCols = new ArrayList<>();
        for (int i = 0; i < columnNames.length; i++) {
            String type = null;
            for (int j = 0; j < tables.length; j++) {
                for (String col : tables[j].columnNames) {
                    if (columnNames[i].equals(col)) {
                        type = tables[j].columnTypes[tables[j].findColIndex(col)];
                        break;
                    }
                }
                if (type != null) {
                    break;
                }
            }
            Column newCol = new Column(columnNames[i], type, tables);
            allCols.add(newCol);
        }
        return allCols;
    }

    /** returns a list of column names except the common ones, from this table and table2. */
    String[] allColumnNames(Table table2) {
        List<String> newColumns = new ArrayList<>();
        for (String col1: this.columnNames) {
            newColumns.add(col1);
        }
        for (String col2 : table2.columnNames) {
            if (!newColumns.contains(col2)) {
                newColumns.add(col2);
            }
        }
        return newColumns.toArray(new String[newColumns.size()]);
    }

    /** Returns a list of two lists of columns that are common in this table and table2. The order of the common
     * columns in both lists are the same.
     * @param table2
     * @return
     */
    List<List<Column>> commonColumns(Table table2) {
        List<List<Column>> commonCols = new ArrayList<>();
        List<Column> commonCol1 = new ArrayList<>();
        List<Column> commonCol2 = new ArrayList<>();
        commonCols.add(commonCol1);
        commonCols.add(commonCol2);
        for (int i = 0; i < this.columnNames.length; i++) {
            for (int j = 0; j < table2.columnNames.length; j++) {
                if (this.columnNames[i].equals(table2.columnNames[j])) {
                    commonCol1.add(this.columns[i]);
                    commonCol2.add(table2.columns[j]);
                }
            }
        }
        return commonCols;
    }

    /** Returns a new Table selected from pairs of rows of this table and from table2 that match on all columns with
     * identical names given and satisfy the listed conditions.
     */
    Table select(Table table2, String[] columnNames, List<ConditionParse> conditions) {

        /**List<Column> allColumns = this.allColumns(table2, columnNames);
        Table res = new Table(allColumns);

        List<List<Column>> commonCols = this.commonColumns(table2);
        List<Column> commonCol1 = commonCols.get(0);
        List<Column> commonCol2 = commonCols.get(1);

        for (Row r1 : this.rows) {
            for (Row r2 : table2.rows) {
                if (join(commonCol1, commonCol2, r1, r2)) {
                    if (conditions == null || conditions.isEmpty() || Condition.test(conditions, r1, r2)) {
                        Row combinedRow = new Row(res.columns, r1, r2);
                        res.add(combinedRow);
                    }
                }
            }
        } */
        Table combine = this.joinTable(table2);
        Table res = combine.select(columnNames, conditions);
        return res;
    }

    public Table joinTable(Table t2) {

        List<Column> newColumns = this.getAllColumns(this.allColumnNames(t2), this, t2);
        Table res = new Table(newColumns.toArray(new Column[newColumns.size()]));

        List<List<Column>> commonCols = this.commonColumns(t2);
        List<Column> commonCol1 = commonCols.get(0);
        List<Column> commonCol2 = commonCols.get(1);

        for (Row r1 : this.rows) {
            for (Row r2 : t2.rows) {
                if (join(commonCol1, commonCol2, r1, r2)) {
                        Row combinedRow = new Row(res.columns, r1, r2);
                        res.add(combinedRow);
                }
            }
        }

        Column[] joinCols = new Column[res.columns.length];

        for (int i = 0; i < joinCols.length; i++) {
            joinCols[i] = new Column(res.columnNames[i], res.columnTypes[i], res);
        }
        res.columns = joinCols;
        return res;
    }

}

