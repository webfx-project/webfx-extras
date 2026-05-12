package dev.webfx.extras.type;

import java.time.*;

/**
 * @author Bruno Salmon
 */

public enum PrimType implements Type {
    STRING,
    BOOLEAN,
    // Numbers
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    // Dates
    LOCAL_DATE,
    LOCAL_DATE_TIME,
    LOCAL_TIME,
    INSTANT,
    YEAR_MONTH;

    public boolean isString() {
        return this == STRING;
    }

    public boolean isBoolean() {
        return this == BOOLEAN;
    }

    public boolean isDate() {
        return this == LOCAL_DATE;
    }

    public boolean isNumber() {
        //return Number.class.isAssignableFrom(javaClass); // compiles with TeaVM but not GWT
        return this == BYTE || this == SHORT || this == INTEGER || this == LONG || this == FLOAT || this == DOUBLE;
    }

    // Static help methods

    public static PrimType fromObject(Object value) {
        if (value instanceof String)
            return STRING;
        if (value instanceof Boolean)
            return BOOLEAN;
        if (value instanceof Integer)
            return INTEGER;
        if (value instanceof Long)
            return LONG;
        if (value instanceof Float)
            return FLOAT;
        if (value instanceof Double)
            return DOUBLE;
        if (value instanceof Byte)
            return BYTE;
        if (value instanceof Short)
            return SHORT;
        if (value instanceof LocalDate)
            return LOCAL_DATE;
        if (value instanceof LocalDateTime)
            return LOCAL_DATE_TIME;
        if (value instanceof LocalTime)
            return LOCAL_TIME;
        if (value instanceof Instant)
            return INSTANT;
        if (value instanceof YearMonth)
            return YEAR_MONTH;
        return null;
    }

    public static PrimType getHighestType(PrimType left, PrimType right) {
        return left.ordinal() >= right.ordinal() ? left : right;
    }

}
