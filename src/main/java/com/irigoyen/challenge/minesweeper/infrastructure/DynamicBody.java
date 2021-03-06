package com.irigoyen.challenge.minesweeper.infrastructure;


import java.util.*;

/**
 * Class to read JSON data posted to rest services without needing to create an object for that.
 */
public class DynamicBody extends HashMap<String, Object> {

    public Integer getInt(String field) {
        return Utils.toInt(get(field));
    }

    public Long getLong(String field) {
        return Utils.toLong(get(field));
    }

    public Double getDouble(String field) {
        return Utils.toDouble(get(field));
    }

    public String getString(String field) {
        return Utils.toString(get(field));
    }

    public boolean getBoolean(String field) {
        return Utils.toBoolean(get(field));
    }

    public List<String> checkRequiredValues(String... keys) {
        List<String> missing = new ArrayList<>();
        for (String key : keys) {
            if (this.get(key) == null) {
                missing.add(key);
            }
        }
        return missing;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }
}