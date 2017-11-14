package lex.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface IConfigEx
{
    void parse(String json);

    String getString(String key, String fallbackValue);

    int getInt(String key, int fallbackValue);

    float getFloat(String key, float fallbackValue);

    boolean getBoolean(String key, boolean fallbackValue);

    JsonObject getJsonObject(String key, JsonObject fallbackValue);

    JsonArray getJsonArray(String key, JsonArray fallbackValue);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    JsonObject getJsonObject(String key);

    JsonArray getJsonArray(String key);

    IConfigEx getJsonObjectAsConfig(String configName, String key);

    IConfigEx getJsonArrayAsConfig(String configName, String key);

    IConfigEx getJsonObjectAsConfig(String key);

    IConfigEx getJsonArrayAsConfig(String key);
}
