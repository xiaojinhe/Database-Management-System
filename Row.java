package db;

import java.util.Arrays;

class Row {
    private Value[] data;

    /** A Row whose column values are DATA. The array DATA must not be altered
     *  subsequently. */
    Row(Value[] data) {
        this.data = data;
    }

    /**
     * Given an array of columns (M columns) that are created from a sequence of Tables and rows that are draw
     * from those tables, constructs a new Row containing the same number of Values as M, where the ith Value
     * of this new Row is taken from the location given by the ith column.
     * If jth table is the table number corresponding to column i, then the ith Value of the newly created
     * Row should come from Rows[j]. And ith Value == Rows[j].get(columnIndex), where column is the column
     * index in Rows[j] corresponding to column i.
     */
    Row(Column[] columns, Row... rows) {
        this.data = new Value[columns.length];
        for (int i = 0; i < columns.length; i++) {
            this.data[i] = columns[i].getColumnValue(rows);
        }
    }

    /** Returns the number of columns in this Row. */
    int size() {
        return data.length;
    }

    void setValue(int i, Value val) {
        try {
            data[i] = val;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The entered index " + i + " is out of boundary.");
        }
    }

    /** Returns the Value of ith columns, where 0 <= i < size(). */
    Value getValue(int i) {
        try {
            return data[i];
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The entered index " + i + " is out of boundary.");
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Row) {
            return Arrays.equals(data, ((Row) obj).data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /** To convert the Row to database format. */
    @Override
    public String toString() {
        StringBuilder rowStr = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            rowStr.append(data[i].toString());
            if (i != data.length - 1) {
                rowStr.append(",");
            }
        }
        return rowStr.toString();
    }
}
