package lex.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import lex.util.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ConfigEx implements IConfigEx
{
    public static final JsonParser JSON_PARSER = new JsonParser();

    private final String CONFIG_NAME;
    private final Map<String, JsonElement> JSON_ELEMENTS = new HashMap<>();
    private final Map<String, JsonElement> JSON_FALLBACK_ELEMENTS = new HashMap<>();
    private Logger LOGGER = LogManager.getLogger("LibEx|ConfigEx");

    public ConfigEx(String configNameIn, File configFile)
    {
        CONFIG_NAME = configNameIn.replace(" ", "");
        LOGGER = LogManager.getLogger("LibEx|ConfigEx-" + CONFIG_NAME);

        String json = null;

        if(configFile.exists())
        {
            LOGGER.info("Found the {} config file", CONFIG_NAME);

            try
            {
                json = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                LOGGER.warn("Error reading the {} config file \n" + e.getMessage(), CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config file is missing", CONFIG_NAME);
        }

        parse(json);
    }

    public ConfigEx(String configNameIn, String json)
    {
        CONFIG_NAME = configNameIn.replace(" ", "");
        LOGGER = LogManager.getLogger("LibEx|ConfigEx-" + CONFIG_NAME);
        parse(json);
    }

    @Override
    public void parse(String json)
    {
        if(!Strings.isBlank(json))
        {
            JsonElement root = JSON_PARSER.parse(json);

            if(root != null)
            {
                if(root.isJsonObject())
                {
                    for(Map.Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet())
                    {
                        JSON_ELEMENTS.put(entry.getKey(), entry.getValue());
                    }
                }
                else if(root.isJsonArray())
                {
                    for(int i = 0; i < root.getAsJsonArray().size(); i++)
                    {
                        JSON_ELEMENTS.put(Integer.toString(i), root.getAsJsonArray().get(i));
                    }
                }
                else
                {
                    LOGGER.warn("The {} config file's root element is not a json object or array", CONFIG_NAME);
                }
            }
            else
            {
                LOGGER.warn("The {} config file's root element is null", CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config file is null or empty", CONFIG_NAME);
        }
    }

    @Override
    public boolean isValid()
    {
        return !JSON_ELEMENTS.isEmpty();
    }

    @Override
    public void addFallbackElement(String key, JsonElement element)
    {
        JSON_FALLBACK_ELEMENTS.put(key, element);
    }

    @Override
    public Logger getLogger()
    {
        return LOGGER;
    }

    @Override
    public String getConfigName()
    {
        return CONFIG_NAME;
    }

    @Override
    public boolean hasElement(String key)
    {
        return JSON_ELEMENTS.containsKey(key);
    }

    @Override
    public boolean hasFallbackElement(String key)
    {
        return JSON_FALLBACK_ELEMENTS.containsKey(key);
    }

    @Override
    public JsonElement getElement(String key)
    {
        return JSON_ELEMENTS.get(key);
    }

    @Override
    public JsonElement getFallbackElement(String key)
    {
        return JSON_FALLBACK_ELEMENTS.get(key);
    }

    @Override
    public Map<String, JsonElement> getElements()
    {
        return ImmutableMap.copyOf(JSON_ELEMENTS);
    }

    @Override
    public Map<String, JsonElement> getFallbackElements()
    {
        return ImmutableMap.copyOf(JSON_FALLBACK_ELEMENTS);
    }

    @Override
    public String getString(String key, String fallbackValue)
    {
        if(!Strings.isBlank(fallbackValue))
        {
            addFallbackElement(key, new JsonPrimitive(fallbackValue));
        }
        return getString(key);
    }

    @Override
    public int getInt(String key, int fallbackValue)
    {
        addFallbackElement(key, new JsonPrimitive(fallbackValue));
        return getInt(key);
    }

    @Override
    public float getFloat(String key, float fallbackValue)
    {
        addFallbackElement(key, new JsonPrimitive(fallbackValue));
        return getFloat(key);
    }

    @Override
    public boolean getBoolean(String key, boolean fallbackValue)
    {
        addFallbackElement(key, new JsonPrimitive(fallbackValue));
        return getBoolean(key);
    }

    @Override
    public IBlockState getBlock(String key, IBlockState fallbackValue)
    {
        JsonObject root = new JsonObject();
        JsonObject properties = new JsonObject();
        root.addProperty("block", fallbackValue.getBlock().getRegistryName().toString());

        for(Map.Entry<IProperty<?>, Comparable<?>> entry : fallbackValue.getProperties().entrySet())
        {
            properties.addProperty(entry.getKey().getName(), entry.getValue().toString());
        }

        root.add("properties", properties);
        addFallbackElement(key, root);
        return getBlock(key);
    }

    @Override
    public JsonObject getJsonObject(String key, JsonObject fallbackValue)
    {
        addFallbackElement(key, fallbackValue);
        return getJsonObject(key);
    }

    @Override
    public JsonArray getJsonArray(String key, JsonArray fallbackValue)
    {
        addFallbackElement(key, fallbackValue);
        return getJsonArray(key);
    }

    @Override
    public String getString(String key)
    {
        if(hasElement(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isString())
        {
            return getElement(key).getAsString();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isString())
        {
            return getFallbackElement(key).getAsString();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return "MissingNo";
        }
    }

    @Override
    public int getInt(String key)
    {
        if(hasElement(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isNumber())
        {
            return getElement(key).getAsInt();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isNumber())
        {
            return getFallbackElement(key).getAsInt();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return 0;
        }

    }

    @Override
    public float getFloat(String key)
    {
        if(hasElement(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isNumber())
        {
            return getElement(key).getAsFloat();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isNumber())
        {
            return getFallbackElement(key).getAsFloat();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return 0.0F;
        }
    }

    @Override
    public boolean getBoolean(String key)
    {
        if(hasElement(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isBoolean())
        {
            return getElement(key).getAsBoolean();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isBoolean())
        {
            return getFallbackElement(key).getAsBoolean();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return false;
        }
    }

    @Override
    public IBlockState getBlock(String key)
    {
        JsonObject root;

        if(hasElement(key) && getElement(key).isJsonObject())
        {
            root = getElement(key).getAsJsonObject();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonObject())
        {
            root = getFallbackElement(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return Blocks.AIR.getDefaultState();
        }

        if(root.has("block"))
        {
            JsonElement blockName = root.get("block");

            if(blockName.isJsonPrimitive() && blockName.getAsJsonPrimitive().isString())
            {
                Block block = Block.getBlockFromName(blockName.getAsString());
                IBlockState state = block.getDefaultState();

                if(root.has("properties"))
                {
                    JsonElement properties = root.get("properties");

                    if(properties.isJsonObject())
                    {
                        for(Map.Entry<String, JsonElement> entry : properties.getAsJsonObject().entrySet())
                        {
                            IProperty property = BlockStateUtils.getProperty(state, entry.getKey());

                            if(property != null)
                            {
                                Comparable propertyValue = BlockStateUtils.getPropertyValue(property, entry.getValue().getAsString());

                                if(propertyValue != null)
                                {
                                    state = state.withProperty(property, propertyValue);
                                }
                                else
                                {
                                    LOGGER.warn("The {} config file's {}'s {} property value is not a valid property value", CONFIG_NAME, key);
                                }
                            }
                            else
                            {
                                LOGGER.warn("The {} config file's {}'s {} property value is not a valid property", CONFIG_NAME, key);
                            }
                        }
                    }
                    else
                    {
                        LOGGER.warn("The {} config file's {}'s properties value is not a json object", CONFIG_NAME, key);
                    }
                }

                return state;
            }
            else
            {
                LOGGER.warn("The {} config file's {}'s block value is not a string", CONFIG_NAME, key);
                return Blocks.AIR.getDefaultState();
            }
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {}'s block value", CONFIG_NAME, key);
            return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    public JsonObject getJsonObject(String key)
    {
        if(hasElement(key) && getElement(key).isJsonObject())
        {
            return getElement(key).getAsJsonObject();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonObject())
        {
            return getFallbackElement(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return new JsonObject();
        }
    }

    @Override
    public JsonArray getJsonArray(String key)
    {
        if(hasElement(key) && getElement(key).isJsonArray())
        {
            return getElement(key).getAsJsonArray();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonArray())
        {
            return getFallbackElement(key).getAsJsonArray();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return new JsonArray();
        }
    }

    @Override
    public IConfigEx getJsonObjectAsConfig(String key)
    {
        return getJsonObjectAsConfig(WordUtils.capitalize(key), key);
    }

    @Override
    public IConfigEx getJsonArrayAsConfig(String key)
    {
        return getJsonArrayAsConfig(WordUtils.capitalize(key), key);
    }

    @Override
    public IConfigEx getJsonObjectAsConfig(String configName, String key)
    {
        JsonElement root;

        if(hasElement(key) && getElement(key).isJsonObject())
        {
            root = getElement(key).getAsJsonObject();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonObject())
        {
            root = getFallbackElement(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return null;
        }
        return new ConfigEx(CONFIG_NAME + "." + configName, root.toString());
    }

    @Override
    public IConfigEx getJsonArrayAsConfig(String configName, String key)
    {
        JsonArray root;

        if(hasElement(key) && getElement(key).isJsonArray())
        {
            root = getElement(key).getAsJsonArray();
        }
        else if(hasFallbackElement(key) && getFallbackElement(key).isJsonArray())
        {
            root = getFallbackElement(key).getAsJsonArray();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return null;
        }
        return new ConfigEx(CONFIG_NAME + "." + configName, root.toString());
    }
}
