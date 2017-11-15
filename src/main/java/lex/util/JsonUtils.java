package lex.util;

import com.google.gson.JsonElement;

public class JsonUtils
{
    public static boolean isString(JsonElement element)
    {
        return !isNull(element) && isPrimitive(element) && element.getAsJsonPrimitive().isString();
    }

    public static boolean isInt(JsonElement element)
    {
        return !isNull(element) && isPrimitive(element) && element.getAsJsonPrimitive().isNumber();
    }

    public static boolean isFloat(JsonElement element)
    {
        return !isNull(element) && isPrimitive(element) && element.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBoolean(JsonElement element)
    {
        return !isNull(element) && isPrimitive(element) && element.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isPrimitive(JsonElement element)
    {
        return !isNull(element) && element.isJsonPrimitive();
    }

    public static boolean isObject(JsonElement element)
    {
        return !isNull(element) && element.isJsonObject();
    }

    public static boolean isNull(JsonElement element)
    {
        return element == null || element.isJsonNull();
    }
}
