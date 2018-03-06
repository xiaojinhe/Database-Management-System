package db;

/** Various utilities used by other sources.
 */
class Utils {

    /** Shorthand that returns String.format(S, ARGS). */
    static String format(String s, Object ... args) {
        return String.format(s, args);
    }

    /** Return a DBException whose message is formed from S and ARGS as for
     *  String.format. */
    static DBException error(String s, Object ... args) {
        return new DBException(format(s, args));
    }

}

