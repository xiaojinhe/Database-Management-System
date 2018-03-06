package db;

import java.util.ArrayList;
import java.util.List;

import static db.Utils.*;

/** Represents a column arithmetic expression of the form <operand0> <arithmetic operator> <operand1> as <column alias>. */
class Operation {
    private Column col1, col2;
    private Value literal;
    private String arithmeticOp;
    String aliasWithType;
    private Table table;

    /** Operation Constructor with a column and a literal. */
    Operation(OperationParse opString, Table table) {
        this.table = table;
        operationTrans(opString);
    }

    /** Parse a OperationParse into a Operation object. */
    private void operationTrans(OperationParse opString) {
        if (opString == null) {
            return;
        }

        this.col1 = table.columns[table.findColIndex(opString.col1)];
        this.arithmeticOp = opString.operator;

        int col2Index = table.findColIndex(opString.colOrLiteral);
        if (col2Index == -1) {
            this.literal = new Value(CommandParse.stringToValue(opString.colOrLiteral));
            if ((this.col1.getColType().equals("string") && !this.literal.getValueType().equals("string")) ||
                    (!this.col1.getColType().equals("string") && this.literal.getValueType().equals("string"))) {
                throw error("Cannot perform operations where one operand is a string and the other is int or float types.");
            }
            if (this.col1.getColType().equals("float") || this.literal.getValueType().equals("float")) {
                this.aliasWithType = opString.newCol + " float";
            } else {
                this.aliasWithType = opString.newCol + " " + col1.getColType();
            }
        } else {
            this.col2 = table.columns[col2Index];
            if ((this.col1.getColType().equals("string") && !this.col2.getColType().equals("string")) ||
                    (!this.col1.getColType().equals("string") && this.col2.getColType().equals("string"))) {
                throw error("Cannot perform operations where one operand is a string and the other is int or float types.");
            }
            if (col1.getColType().equals("float") || col2.getColType().equals("float")) {
                this.aliasWithType = opString.newCol + " float";
            } else {
                this.aliasWithType = opString.newCol + " " + col1.getColType();
            }
        }
    }

    /** Return a list of Operation objects by passing a list of OperationParse objects and
     * the table that the operations will be applied to. */
    static List<Operation> operationsTrans(List<OperationParse> colOperations, Table table) {
        List<Operation> operationList = new ArrayList<>();
        if (colOperations == null || colOperations.isEmpty()) {
            return null;
        }
        for (OperationParse colOp : colOperations) {
            operationList.add(new Operation(colOp, table));
        }
        return operationList;
    }

    /** Return a Value object as result of an arithmetic operation. */
    Value arithmeticOperation(Row... rows) {

        Value val1 = col1.getColumnValue(rows);
        Value val2;
        Value res;

        if (col2 != null) {
            val2 = col2.getColumnValue(rows);
        } else {
            val2 = literal;
        }

        if (col1.getColType().equals("string")) {
            if (this.arithmeticOp.equals("+")) {
                res = new Value((String) val1.value + (String) val2.value);
                return res;
            } else {
                throw error("The arithmetic operation %s cannot perform on strings. " +
                        "The only allowed operation on strings is concatenation with the operator '+'.",
                        this.arithmeticOp);
            }
        }

        double operand1 = ((Number) val1.value).doubleValue();
        double operand2 = ((Number) val2.value).doubleValue();
        Number opRes = 0;
        switch (this.arithmeticOp) {
            case "+":
                opRes = operand1 + operand2;
                break;
            case "-":
                opRes = operand1 - operand2;
                break;
            case "*":
                opRes = operand1 * operand2;
                break;
            case "/":
                opRes = operand1 / operand2;
                break;
            default:
                throw error("Invalid arithmetic operator %s.", this.arithmeticOp);
        }
        if (val1.getValueType().equals("float") || val2.getValueType().equals("float")) {
            res = new Value(opRes.floatValue());
        } else {
            res = new Value(opRes.intValue());
        }
        return res;
    }
}
