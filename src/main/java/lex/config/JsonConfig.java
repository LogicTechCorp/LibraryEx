package lex.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
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

import static lex.util.JsonUtils.*;

public class JsonConfig
{
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String CONFIG_NAME;
    private final File CONFIG_FILE;
    private final boolean IS_SUB_CONFIG;
    private final Map<String, JsonElement> ELEMENTS = new HashMap<>();
    private final Map<String, JsonElement> FALLBACK_ELEMENTS = new HashMap<>();
    private final Map<String, JsonConfig> SUB_CONFIGS = new HashMap<>();
    private Logger LOGGER = LogManager.getLogger("LibEx|JsonConfig");

    public JsonConfig(String configName, File configFile)
    {
        CONFIG_NAME = configName;
        CONFIG_FILE = !Files.getFileExtension(configFile.getPath()).equals("json") ? new File(configFile.getPath() + ".json") : configFile;
        IS_SUB_CONFIG = false;

        String json = null;

        if(configFile.exists())
        {
            LOGGER.info("Found the {} config", CONFIG_NAME);

            try
            {
                json = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                LOGGER.warn("Error reading the {} config \n" + e.getMessage(), CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config is missing, it will be created", CONFIG_NAME);
        }

        parse(json);
    }

    private JsonConfig(String configName, String json)
    {
        CONFIG_NAME = configName;
        CONFIG_FILE = null;
        IS_SUB_CONFIG = true;
        parse(json);
    }

    private void parse(String json)
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
                        ELEMENTS.put(entry.getKey(), entry.getValue());
                    }
                }
                else
                {
                    LOGGER.warn("The {} config's root element is not a json object", CONFIG_NAME);
                }
            }
            else
            {
                LOGGER.warn("The {} config's root element is null", CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config is empty, it may be populated with values by a mod", CONFIG_NAME);
        }
    }

    public JsonElement compose()
    {
        JsonObject root = new JsonObject();

        for(Map.Entry<String, JsonElement> entry : getAll().entrySet())
        {
            root.add(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String, JsonElement> entry : getAllFallbacks().entrySet())
        {
            if(!root.has(entry.getKey()))
            {
                root.add(entry.getKey(), entry.getValue());
            }
        }

        for(Map.Entry<String, JsonConfig> entry : getSubConfigs().entrySet())
        {
            root.add(entry.getKey(), entry.getValue().compose());
        }

        return root;
    }

    public void save()
    {
        if(!IS_SUB_CONFIG)
        {
            if(isValid())
            {
                String json = GSON.toJson(compose());

                try
                {
                    FileUtils.write(CONFIG_FILE, json, Charset.defaultCharset());
                }
                catch(IOException e)
                {
                    LOGGER.warn("The {} config was unable to be written to a file\n" + e.getMessage(), CONFIG_NAME);
                }

                LOGGER.warn("The {} config has been saved", CONFIG_NAME);
            }
            else
            {
                LOGGER.warn("The {} config is not valid and it will not be written to a file", CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config cannot be saved as it is a sub config", CONFIG_NAME);
        }
    }

    public boolean isValid()
    {
        return !ELEMENTS.isEmpty() || !FALLBACK_ELEMENTS.isEmpty();
    }

    public Logger getLogger()
    {
        return LOGGER;
    }

    public String getName()
    {
        return CONFIG_NAME;
    }

    public void addFallback(String key, JsonElement element)
    {
        FALLBACK_ELEMENTS.put(key, element);
    }

    public boolean has(String key)
    {
        return ELEMENTS.containsKey(key);
    }

    public boolean hasFallback(String key)
    {
        return FALLBACK_ELEMENTS.containsKey(key);
    }

    public JsonElement get(String key)
    {
        return ELEMENTS.get(key);
    }

    public JsonElement getFallback(String key)
    {
        return FALLBACK_ELEMENTS.get(key);
    }

    public Map<String, JsonElement> getAll()
    {
        return ImmutableMap.copyOf(ELEMENTS);
    }

    public Map<String, JsonElement> getAllFallbacks()
    {
        return ImmutableMap.copyOf(FALLBACK_ELEMENTS);
    }

    public Map<String, JsonConfig> getSubConfigs()
    {
        return ImmutableMap.copyOf(SUB_CONFIGS);
    }

    public String getString(String key, String fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getString(key);
    }

    public int getInt(String key, int fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getInt(key);
    }

    public float getFloat(String key, float fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getFloat(key);
    }

    public boolean getBoolean(String key, boolean fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getBoolean(key);
    }

    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue.toString()));
        return getEnum(key, enumClass);
    }

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
        addFallback(key, root);
        return getBlock(key);
    }

    public JsonArray getArray(String key, JsonArray fallbackValue)
    {
        FALLBACK_ELEMENTS.put(key, fallbackValue);
        return getArray(key);
    }

    public String getString(String key)
    {
        if(has(key) && isString(get(key)))
        {
            return get(key).getAsString();
        }
        else if(hasFallback(key) && isString(getFallback(key)))
        {
            return getFallback(key).getAsString();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return "MissingNo";
        }
    }

    public int getInt(String key)
    {
        if(has(key) && isInt(get(key)))
        {
            return get(key).getAsInt();
        }
        else if(hasFallback(key) && isInt(getFallback(key)))
        {
            return getFallback(key).getAsInt();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return 0;
        }

    }

    public float getFloat(String key)
    {
        if(has(key) && isFloat(get(key)))
        {
            return get(key).getAsFloat();
        }
        else if(hasFallback(key) && isFloat(getFallback(key)))
        {
            return getFallback(key).getAsFloat();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return 0.0F;
        }
    }

    public boolean getBoolean(String key)
    {
        if(has(key) && isBoolean(get(key)))
        {
            return get(key).getAsBoolean();
        }
        else if(hasFallback(key) && isBoolean(getFallback(key)))
        {
            return getFallback(key).getAsBoolean();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return false;
        }
    }

    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass)
    {
        String enumIdentifier;

        if(has(key) && isString(get(key)))
        {
            enumIdentifier = get(key).getAsString();
        }
        else if(hasFallback(key) && isString(getFallback(key)))
        {
            enumIdentifier = getFallback(key).getAsString();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return null;
        }

        for(E value : enumClass.getEnumConstants())
        {
            if(value.name().equalsIgnoreCase(enumIdentifier))
            {
                return value;
            }
        }

        return null;
    }

    public IBlockState getBlock(String key)
    {
        JsonObject root;

        if(has(key) && isObject(get(key)))
        {
            root = get(key).getAsJsonObject();
        }
        else if(hasFallback(key) && isObject(getFallback(key)))
        {
            root = getFallback(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return Blocks.AIR.getDefaultState();
        }

        if(root.has("block"))
        {
            JsonElement blockName = root.get("block");

            if(isString(blockName))
            {
                Block block = Block.getBlockFromName(blockName.getAsString());
                IBlockState state = block.getDefaultState();

                if(root.has("properties"))
                {
                    JsonElement properties = root.get("properties");

                    if(isObject(properties))
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
                                    LOGGER.warn("The {} config's {}'s {} property value is not a valid property value", CONFIG_NAME, key);
                                }
                            }
                            else
                            {
                                LOGGER.warn("The {} config's {}'s {} property value is not a valid property", CONFIG_NAME, key);
                            }
                        }
                    }
                    else
                    {
                        LOGGER.warn("The {} config's {}'s properties value is not a json object", CONFIG_NAME, key);
                    }
                }

                return state;
            }
            else
            {
                LOGGER.warn("The {} config's {}'s block value is null or not a string", CONFIG_NAME, key);
                return Blocks.AIR.getDefaultState();
            }
        }
        else
        {
            LOGGER.warn("The {} config is missing the {}'s block value", CONFIG_NAME, key);
            return Blocks.AIR.getDefaultState();
        }
    }

    public JsonArray getArray(String key)
    {
        JsonElement root;

        if(has(key) && isArray(get(key)))
        {
            root = get(key).getAsJsonArray();
        }
        else if(hasFallback(key) && isArray(getFallback(key)))
        {
            root = getFallback(key).getAsJsonArray();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} sub config, it is going to be created", CONFIG_NAME, key);
            return new JsonArray();
        }

        return root.getAsJsonArray();
    }

    public JsonConfig getSubConfig(String key)
    {
        JsonElement root;

        if(has(key) && isObject(get(key)))
        {
            root = get(key).getAsJsonObject();
        }
        else if(hasFallback(key) && isObject(getFallback(key)))
        {
            root = getFallback(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config is missing the {} sub config, it is going to be created", CONFIG_NAME, key);

            JsonConfig subConfig = new JsonConfig(WordUtils.capitalize(key), new JsonObject().toString());
            SUB_CONFIGS.put(key, subConfig);
            return subConfig;
        }

        JsonConfig subConfig = new JsonConfig(WordUtils.capitalize(key), root.toString());
        SUB_CONFIGS.put(key, subConfig);
        return subConfig;
    }
}
