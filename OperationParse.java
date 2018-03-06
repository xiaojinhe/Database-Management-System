package db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static db.Utils.*;

/** A object that parses a string expression. */
class OperationParse {
    private static final Pattern OPERATOR_AS = Pattern.compile("\\s*(\\S+)\\s*([/*+-])\\s*(\\S+)\\s+as\\s+(\\S+)\\s*");

    String col1;
    String operator;
    String colOrLiteral;
    String newCol;

    OperationParse(String expr) {
        Matcher matcher;
        if ((matcher = OPERATOR_AS.matcher(expr)).matches()) {
            col1 = matcher.group(1);
            operator = matcher.group(2);
            colOrLiteral = matcher.group(3);
            newCol = matcher.group(4);
        } else {
            throw error("Given column arithmetic operation, %s, cannot be resolved.", expr);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", col1, operator, colOrLiteral, newCol);
    }
}
