package com.irigoyen.challenge.minesweeper.infrastructure;

public class Utils {
    public static String toString(Object val) {
        if (val == null)
            return null;
        return val.toString();
    }

    public static Long toLong(Object value) {
        if (value == null)
            return null;
        if (value instanceof Long)
            return (Long) value;
        if (value instanceof Integer)
            return ((Integer) value).longValue();
        return Long.decode((String) value);
    }

    public static Integer toInt(Object value) {
        if (value == null)
            return null;
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Long)
            return ((Long) value).intValue();
        try {//we need this to accept values with decimals
            return toDouble(value).intValue();
        } catch (Exception e) {
            //TODO: Log errors...
            return null;
        }
    }

    public static Double toDouble(Object value) {
        if (value == null)
            return null;
        if (value instanceof Double)
            return (Double) value;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            //TODO: Log errors...
            return null;
        }
    }

    public static boolean toBoolean(Object value) {
        return toBoolean(value, false);
    }

    public static boolean toBoolean(Object value, boolean defaultValue) {
        if (value == null)
            return defaultValue;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof String)
            return ((String) value).toLowerCase().equals("true");
        return false;
    }

    public static char[] fillArray(char fillChar, int count) {
        // creates a string of 'x' repeating characters
        char[] chars = new char[count];
        for (int i = 0, len = chars.length; i < len; i++)
            chars[i] = fillChar;
        return chars;
    }
}

