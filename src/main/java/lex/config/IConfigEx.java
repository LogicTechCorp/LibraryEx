package lex.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public interface IConfigEx
{
    void parse(String json);

    boolean isValid();

    void addFallbackElement(String key, JsonElement element);

    Logger getLogger();

    String getConfigName();

    boolean hasElement(String key);

    boolean hasFallbackElement(String key);

    JsonElement getElement(String key);

    JsonElement getFallbackElement(String key);

    Map<String, JsonElement> getElements();

    Map<String, JsonElement> getFallbackElements();

    String getString(String key, String fallbackValue);

    int getInt(String key, int fallbackValue);

    float getFloat(String key, float fallbackValue);

    boolean getBoolean(String key, boolean fallbackValue);

    IBlockState getBlock(String key, IBlockState fallbackValue);

    JsonObject getJsonObject(String key, JsonObject fallbackValue);

    JsonArray getJsonArray(String key, JsonArray fallbackValue);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    IBlockState getBlock(String key);

    JsonObject getJsonObject(String key);

    JsonArray getJsonArray(String key);

    IConfigEx getJsonObjectAsConfig(String key);

    IConfigEx getJsonArrayAsConfig(String key);

    IConfigEx getJsonObjectAsConfig(String configName, String key);

    IConfigEx getJsonArrayAsConfig(String configName, String key);
}
