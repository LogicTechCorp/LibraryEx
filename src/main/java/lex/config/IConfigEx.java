package lex.config;

import com.google.gson.JsonElement;
import net.minecraft.block.state.IBlockState;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public interface IConfigEx
{
    void parse(String json);

    boolean isValid();

    Logger getLogger();

    String getName();

    void addFallback(String key, JsonElement element);

    boolean has(String key);

    boolean hasFallback(String key);

    JsonElement get(String key);

    JsonElement getFallback(String key);

    Map<String, JsonElement> getAll();

    Map<String, JsonElement> getAllFallbacks();

    Map<String, IConfigEx> getSubConfigs();

    String getString(String key, String fallbackValue);

    int getInt(String key, int fallbackValue);

    float getFloat(String key, float fallbackValue);

    boolean getBoolean(String key, boolean fallbackValue);

    <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E fallbackValue);

    IBlockState getBlock(String key, IBlockState fallbackValue);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    <E extends Enum> E getEnum(String key, Class<? extends E> enumClass);

    IBlockState getBlock(String key);

    IConfigEx getSubConfig(String key);
}
