package lex.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

public class ConfigEx implements IConfigEx
{
    private final String CONFIG_NAME;
    private final Map<String, JsonElement> ELEMENTS = new HashMap<>();
    private final Map<String, JsonElement> FALLBACK_ELEMENTS = new HashMap<>();
    private final Map<String, IConfigEx> SUB_CONFIGS = new HashMap<>();
    private Logger LOGGER = LogManager.getLogger("LibEx|ConfigEx");

    public ConfigEx(String configName, File configFile)
    {
        CONFIG_NAME = configName.replace(" ", "");
        LOGGER = LogManager.getLogger(CONFIG_NAME);

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
            LOGGER.warn("The {} config is missing", CONFIG_NAME);
        }

        parse(json);
    }

    public ConfigEx(String configName, String json)
    {
        CONFIG_NAME = configName.replace(" ", "");
        LOGGER = LogManager.getLogger(CONFIG_NAME);
        parse(json);
    }

    @Override
    public void parse(String json)
    {
        if(!Strings.isBlank(json))
        {
            JsonElement root = ConfigExHelper.JSON_PARSER.parse(json);

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
            LOGGER.warn("The {} config is null or empty", CONFIG_NAME);
        }
    }

    @Override
    public boolean isValid()
    {
        return !ELEMENTS.isEmpty() || !FALLBACK_ELEMENTS.isEmpty();
    }

    @Override
    public Logger getLogger()
    {
        return LOGGER;
    }

    @Override
    public String getName()
    {
        return CONFIG_NAME;
    }

    @Override
    public void addFallback(String key, JsonElement element)
    {
        FALLBACK_ELEMENTS.put(key, element);
    }

    @Override
    public boolean has(String key)
    {
        return ELEMENTS.containsKey(key);
    }

    @Override
    public boolean hasFallback(String key)
    {
        return FALLBACK_ELEMENTS.containsKey(key);
    }

    @Override
    public JsonElement get(String key)
    {
        return ELEMENTS.get(key);
    }

    @Override
    public JsonElement getFallback(String key)
    {
        return FALLBACK_ELEMENTS.get(key);
    }

    @Override
    public Map<String, JsonElement> getAll()
    {
        return ImmutableMap.copyOf(ELEMENTS);
    }

    @Override
    public Map<String, JsonElement> getAllFallbacks()
    {
        return ImmutableMap.copyOf(FALLBACK_ELEMENTS);
    }

    @Override
    public Map<String, IConfigEx> getSubConfigs()
    {
        return ImmutableMap.copyOf(SUB_CONFIGS);
    }

    @Override
    public String getString(String key, String fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getString(key);
    }

    @Override
    public int getInt(String key, int fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getInt(key);
    }

    @Override
    public float getFloat(String key, float fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getFloat(key);
    }

    @Override
    public boolean getBoolean(String key, boolean fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue));
        return getBoolean(key);
    }

    @Override
    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E fallbackValue)
    {
        addFallback(key, new JsonPrimitive(fallbackValue.toString()));
        return getEnum(key, enumClass);
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
        addFallback(key, root);
        return getBlock(key);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public IConfigEx getSubConfig(String key)
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
            LOGGER.warn("The {} config is missing the {} sub config it is going to be created", CONFIG_NAME, key);

            IConfigEx subConfig = new ConfigEx(WordUtils.capitalize(key), new JsonObject().toString());
            SUB_CONFIGS.put(key, subConfig);
            return subConfig;
        }

        IConfigEx subConfig = new ConfigEx(WordUtils.capitalize(key), root.toString());
        SUB_CONFIGS.put(key, subConfig);
        return subConfig;
    }
}
