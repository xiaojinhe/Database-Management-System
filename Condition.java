package db;

import java.util.List;
import static db.Utils.error;

/** A single "where" condition in a "select" command. */
class Condition {

    private Column col1, col2;
    private Value literal;
    private String comparison;

    /** Condition constructor for a unary condition of the form <column name> <comparison> <Value literal>.
     * comparisons are "<", ">", "<=", ">=", "==", "!=".
     */
    Condition(Column col1, String comparison, Value literal) {
        this.col1 = col1;
        this.col2 = null;
        this.comparison = comparison;
        this.literal = literal;
    }

    /** Condition constructor for a binary condition of the form <column1 name> <comparison> <column2 name>.
     * comparisons are "<", ">", "<=", ">=", "==", "!=".
     */
    Condition(Column col1, String comparison, Column col2) {
        if ((col1.getColType().equals("string") && !col2.getColType().equals("string")) ||
                (!col1.getColType().equals("string") && col2.getColType().equals("string"))) {
            throw error("Cannot compare strings to either int or float types.");
        }
        this.col1 = col1;
        this.col2 = col2;
        this.comparison = comparison;
        this.literal = null;
    }

    /** Returns the resulted rows that pass the test, assuming that rows are from the respective tables
     * from which columns are selected.
     */
    boolean testCondition(Row... rows) {
        Value val1 = col1.getColumnValue(rows);
        int comp;
        if (col2 != null) {
            comp = val1.compareTo(col2.getColumnValue(rows));
        } else {
            comp = val1.compareTo(literal);
        }
        switch (this.comparison) {
            case "<":
                return comp < 0;
            case ">":
                return comp > 0;
            case "<=":
                return comp <= 0;
            case ">=":
                return comp >= 0;
            case "==":
                return comp == 0;
            case "!=":
                return comp != 0;
        }
        return false;
    }

    /** Returns true if and only if rows satisfies all conditions, or false. */
    static boolean test(List<Condition> conditions, Row... rows) {
        for (Condition c : conditions) {
            if (!c.testCondition(rows)) {
                return false;
            }
        }
        return true;
    }
}
