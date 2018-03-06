package db;

/** Indicates some kind of user error. */
class DBException extends RuntimeException {
    /** A new exception without message. */
    DBException() {
    }

    /** A new exception with message MSG. */
    DBException(String msg) {
        super(msg);
    }
}
