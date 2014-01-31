package com.home911.httpchat.client.utils;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public final class JsonUtil {

    private JsonUtil() {
    }

    public static Long getLongValue(JSONValue longField) {
        if (longField != null) {
            JSONNumber jsonNumber = longField.isNumber();
            if (jsonNumber != null) {
                return (long)jsonNumber.doubleValue();
            }
        }
        return null;
    }

    public static String getStringValue(JSONValue stringValue) {
        if (stringValue != null) {
            JSONString jsonString = stringValue.isString();
            if (jsonString != null) {
                return jsonString.stringValue();
            }
        }
        return null;
    }

    public static int getIntValue(JSONValue intValue) {
        if (intValue != null) {
            JSONNumber jsonNumber = intValue.isNumber();
            if (jsonNumber != null) {
                return (int)jsonNumber.doubleValue();
            }
        }
        return 0;
    }
}
