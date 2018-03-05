package db;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static db.Utils.*;


/** A Column is effectively an index of a specific, named column
 *  in a list of Rows.  Given a sequence of [t0,...,tn] of Tables,
 *  and a column name, c, a Column can retrieve the value of that column of
 *  the first ti that contains it from an array of rows [r0,...,rn],
 *  where each ri comes from ti.
 *  @author P. N. Hilfinger
 */
class Column {

    private String colName;
    private String colType;
    private int tableNum;
    private int columnIndex;

    /** Column constructor with name and type. */
    /**Column(String name) {
        try {
            colType = name.split(" ")[1];
            colName = name;
        } catch (Exception e) {
            System.out.println("Invalid column name " + name + ", please provide column name in the format: name type.");
        }
        if (!(colType.equals("int") || colType.equals("string") || colType.equals("float"))) {
            throw error("Invalid column type: %s.", colType);
        }
    } */

    /** Selects column with name from a list of one of the given tables. */
    Column(String name, String type, Table... tables) {

        /*if (!(colType.equals("int") || colType.equals("string") || colType.equals("float"))) {
            throw error("Invalid column type: %s.", type);
        } */

        this.colName = name;
        this.colType = type;

        for (tableNum = 0; tableNum < tables.length; tableNum++) {
            columnIndex = tables[tableNum].findColIndex(colName);
            if (columnIndex != -1) {
                return;
            }
        }
        throw error("There is no column: %s", name);
    }
/**
    void addValue(Value v) {

        if (colType.equals(v.getValueType())) {
            column.add(v);
        } else if (v.NOVALUE || v.NaN) {
            column.add(v);
            v.valueType = colType;

        } else {
            System.out.println("Wrong type error: column type is " + colType +
                    " while entered value type is " + v.valueType + ".");
        }
    } */

    /** Returns the value of this Column from Rows[tableNum]. Assumes that Rows[tableNum] is from the
     * same table that is provided to the column constructor of this column.
     * rows are the arrays that the ith row from many tables.
     * This method returns only one Value from the row in the tableNum th table that was passed
     * to the constructor, despite the fact that many rows are passed to this method.
     * @param rows
     * @return
     */

    public Value getColumnValue(Row... rows) {
        return rows[tableNum].getValue(columnIndex);
    }


    public String getColName() {
        return colName;
    }

    public String getColType() {
        return colType;
    }

    @Override
    public String toString() {
        return this.colName + " " + this.colType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Column) {
            if (this.colName.equals(((Column) obj).colName) && this.colType.equals(((Column) obj).colType)) {
                return true;
            }
        }
        return false;
    }

}
