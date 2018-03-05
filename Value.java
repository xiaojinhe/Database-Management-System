package db;
import org.junit.Test;

import static db.Utils.error;
import static org.junit.Assert.*;
import java.util.*;

class Value<T extends Comparable<T>> implements Comparable<Value<T>> {
    String valueType;
    boolean NaN;
    boolean NOVALUE;
    T value;

    /** Empty value constructor. */
    Value() {
    }


    Value(T val) {

        value = val;

        if (val.equals("NOVALUE")) {
            NOVALUE = true;
        }

        if (val.equals("NaN")) {
            NaN = true;
        }

        valueType = val.getClass().getSimpleName();
        if (valueType.equals("Float")) {
            valueType = "float";

        } else if (valueType.equals("Integer")) {
            valueType = "int";
        } else if (valueType.equals("String")){
            valueType = "string";
        } else {
            throw error("Invalid value type entered: %s.", val.getClass().getSimpleName());
        }
    }

    /** A value constructor takes in an int value.
    Value(int x) {
        intValue = x;
        valueType = "int";
        NaN = false;
        NOVALUE = false;
    }*/

    /** A value constructor takes in a float value.
    Value(float f) {
        floatValue = f;
        valueType = "float";
        NaN = false;
        NOVALUE = false;
    }*/

    /** A value constructor takes in a string value.
    Value(String s) {
        strValue = s;
        valueType = "string";
        NaN = false;
        NOVALUE = false;
    }*/

    /** Override the toString() to return the string representation of a value. */
    @Override
    public String toString() {
        if (NaN) {
            return "NaN";
        }
        if (NOVALUE) {
            return "NOVALUE";
        }
        return value.toString();
    }

    public String getValueType() {
        return valueType;
    }

    /** Returns the value */
    public T getValue() {
        return this.value;
    }

    Value duplicate() {
        Value res = new Value(this.value);
        res.valueType = this.valueType;
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Value that = (Value) obj;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(Value<T> o) {
        if (this.NaN && o.NaN) {
            return 0;
        } else if (this.NaN) {
            return 1;
        } else if (o.NaN) {
            return -1;
        }

        if ((this.valueType.equals("string") && !o.valueType.equals("string")) ||
                (!this.valueType.equals("string") && o.valueType.equals("string"))) {
            throw error("Cannot compare strings to either int or float types.");
        } else if (this.valueType.equals("string") && o.valueType.equals("string")) {
            return this.value.compareTo(o.value);
        }

        return Double.compare(((Number) this.value).doubleValue(), ((Number) o.value).doubleValue());
    }
}
