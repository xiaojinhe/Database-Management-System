package db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import static db.Utils.*;

/** An Object that takes inputs and interprets the demands. */

public class CommandParse {

    /** Text of regular expressions. */
    private static final String REST = "\\s*(.*)\\s*",
                                COMMA = "\\s*,\\s*",
                                AND = "\\s+and\\s+",
                                INTEGER = "\\s*([+-]?\\d+)\\s*",
                                STRING = "\'.*\'",
                                FLOAT = "^\\s*([+-]?\\.\\d*)\\s*$";

    /** Syntax for value types. */
    private static final Pattern INTEGER_TYPE = Pattern.compile(INTEGER),
                                 STRING_TYPE = Pattern.compile(STRING),
                                 FLOAT_TYPE = Pattern.compile(FLOAT);

    /** Stage 1 syntax, contains command names. */
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
                                 LOAD_CMD = Pattern.compile("load " + REST),
                                 STORE_AMD = Pattern.compile("store " + REST),
                                 DROP_CMD = Pattern.compile("drop table " + REST),
                                 INSERT_CMD = Pattern.compile("insert into" + REST),
                                 PRINT_CMD = Pattern.compile("print " + REST),
                                 SELECT_AMD = Pattern.compile("select " + REST);

    /** Stage 2 syntax, contains clauses of commands. */
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*"
                                              + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
                                 SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                                              "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                                              "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                                              "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
                                 CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+" + SELECT_CLS.pattern()),
                                 INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?\\s*(?:,\\s*.+?\\s*)*)");

    /** Parse and execute one request statement from the input stream, and return appropriate result. */
    static String eval(String query) {
        try {
            Matcher matcher;
            if ((matcher = CREATE_CMD.matcher(query)).matches()) {
                return createTable(matcher.group(1));
            } else if ((matcher = LOAD_CMD.matcher(query)).matches()) {
                return loadTable(matcher.group(1));
            } else if ((matcher = STORE_AMD.matcher(query)).matches()) {
                return storeTable(matcher.group(1));
            } else if ((matcher = DROP_CMD.matcher(query)).matches()) {
                return dropTable(matcher.group(1));
            } else if ((matcher = INSERT_CMD.matcher(query)).matches()) {
                return insertRow(matcher.group(1));
            } else if ((matcher = PRINT_CMD.matcher(query)).matches()) {
                return printTable(matcher.group(1));
            } else if ((matcher = SELECT_AMD.matcher(query)).matches()) {
                return select(matcher.group(1));
            } else {
                throw error("Malformed query: %s", query);
            }
        } catch (DBException e) {
            return "" + e;
        }
    }

    /** Returns a value from the input string. */
    static Comparable stringToValue(String str) {
        Matcher matcher;
        if ((matcher = INTEGER_TYPE.matcher(str)).matches()) {
            return Integer.parseInt(matcher.group(1));
        } else if ((matcher = FLOAT_TYPE.matcher(str)).matches()) {
            return Float.parseFloat(matcher.group(1));
        } else {
            return str;
        }
    }

    /** Returns a list of ConditionParse objects from a string array. */
    private static List<ConditionParse> strToCondParse(String[] conditionStrs) {
        List<ConditionParse> condParse = new ArrayList<>();
        for (String str : conditionStrs) {
            condParse.add(new ConditionParse(str));
        }
        return condParse;
    }

    /** Create a new table according to the request. */
    private static String createTable(String expr) {
        try {
            Matcher matcher;
            if ((matcher = CREATE_NEW.matcher(expr)).matches()) {
                return createNewTable(matcher.group(1), matcher.group(2).split(COMMA));
            } else if ((matcher = CREATE_SEL.matcher(expr)).matches()) {
                String name = matcher.group(1);
                String[] columnNames = matcher.group(2).split(COMMA);
                String[] tableNames = matcher.group(3).split(COMMA);
                List<ConditionParse> condParse = null;
                if (matcher.group(4) != null) {
                    condParse = strToCondParse(matcher.group(4).split(AND));
                }
                return createSelectedTable(name, columnNames, tableNames, condParse);
            } else {
                throw error("Malformed create: %s", expr);
            }
        } catch (Exception e) {
            return "" + e;
        }
    }

    /** Create a new table with given table name and column names and types. */
    private static String createNewTable(String name, String[] columns) {
        String res;
        try {
            res = Database.createNewTable(name, columns).toString();
        } catch (DBException e) {
            res = "" + e;
        }
        return res;
    }

    /** Create a new table from existed tables via select function, by giving column names, table names,
     * new table name, and a list of ConditionParse objects.
     */
    private static String createSelectedTable(String name, String[] columnNames, String[] tableNames,
                                              List<ConditionParse> conditionParses) {
        try {
            Table res = Database.select(name, columnNames, tableNames, conditionParses);
            return "";
        } catch (DBException e) {
            return "" + e;
        }
    }

    /** Load a table, given by name, and add the table to the database. */
    private static String loadTable(String name) {
        try {
            return Database.loadTable(name);
        } catch (Exception e) {
            return "" + e;
        }
    }

    /** Save the table with the given name as a file name.tbl. */
    private static String storeTable(String name) {
        try {
            return Database.storeTable(name);
        } catch (Exception e) {
            return "" + e;
        }
    }

    /** Print the table with the given name. */
    private static String printTable(String name) {
        try {
            return Database.printTable(name);
        } catch (DBException e) {
            return "" + e;
        }

    }

    /** Insert a new row by parsing the given expression. */
    private static String insertRow(String expr) {
        try {
            Matcher matcher = INSERT_CLS.matcher(expr);
            if (matcher.matches()) {
                return Database.insertRow(matcher.group(1), matcher.group(2));
            } else {
                throw error("Malformed insert: %s.", expr);
            }
        } catch (DBException e) {
            return "" + e;
        }

    }

    /** Delete the table from the database, given by the table name. */
    private static String dropTable(String name) {
        try {
            return Database.dropTable(name);
        } catch (DBException e) {
            return "" + e;
        }
    }

    /** Parse and execute a select expression and return the result. */
    private static String select(String expr) {
        try {
            Matcher matcher = SELECT_CLS.matcher(expr);
            if (!matcher.matches()) {
                throw error("Malformed select: %s", expr);
            }

            String[] columns = matcher.group(1).split(COMMA);
            String[] tableNames = matcher.group(2).split(COMMA);
            List<ConditionParse> condParse = null;
            if (matcher.group(3) != null) {
                condParse = strToCondParse(matcher.group(3).split(AND));
            }

            Table res = Database.select(null, columns, tableNames, condParse);
            return res.toString();
        } catch (Exception e) {
            return "" + e;
        }
    }
}
