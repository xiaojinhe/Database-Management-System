package db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static db.Utils.*;

class ConditionParse {

    /** Stage 3 syntax, parses information about conditions and operations. */
    private static final Pattern CONDITION_CMD = Pattern.compile("\\s*(\\S+)\\s*(<=|>=|==|!=|<|>)\\s*(\\S+)\\s*");
           // smallerAndEqual_CMD = Pattern.compile("([^\\s]+)\\s*<=\\s*([^\\s]+)"),
           // equal_CMD = Pattern.compile("([^\\s]+)\\s*==\\s*([^\\s]+)"),
           // notEqual_CMD = Pattern.compile("([^\\s]+)\\s*!=\\s*([^\\s]+)"),
           // larger_CMD = Pattern.compile("([^\\s]+)\\s*>\\s*([^\\s]+)"),
           // smaller_CMD = Pattern.compile("([^\\s]+)\\s*<\\s*([^\\s]+)"),
           // OPERATOR_AS = Pattern.compile("([^/\\s]+)([/*+-])([^/\\s]+)\\s+as\\s+(\\S+)");

    String col1;
    String comparison;
    String colOrLiteral;

    ConditionParse(String expr) {
        Matcher matcher;
        if ((matcher = CONDITION_CMD.matcher(expr)).matches()) {
            col1 = matcher.group(1);
            comparison = matcher.group(2);
            colOrLiteral = matcher.group(3);
        } else {
            throw error("ERROR: Given condition, %s, cannot be resolved.", expr);
        }
    }

    @Override
    public String toString() {
        return this.col1 + " " + this.comparison + " " + this.colOrLiteral;
    }
}
