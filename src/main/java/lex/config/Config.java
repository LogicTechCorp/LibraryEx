package lex.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lex.util.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static lex.util.JsonUtils.*;

public class Config
{
    private final File CONFIG_FILE;
    private final boolean INNER_CONFIG;
    private final Map<String, JsonElement> ELEMENTS = new HashMap<>();
    private final Map<String, JsonElement> DEFAULT_ELEMENTS = new HashMap<>();
    private final Map<String, Config> INNER_CONFIGS = new HashMap<>();

    Config(File configFile, boolean innerConfig)
    {
        CONFIG_FILE = configFile;
        INNER_CONFIG = innerConfig;
    }

    public void add(String key, JsonElement element)
    {
        ELEMENTS.put(key, element);
    }

    public void addDefault(String key, JsonElement element)
    {
        DEFAULT_ELEMENTS.put(key, element);
    }

    public void addInnerConfig(String key, Config config)
    {
        INNER_CONFIGS.put(key, config);
    }

    public boolean has(String key)
    {
        return ELEMENTS.containsKey(key);
    }

    public boolean hasDefault(String key)
    {
        return DEFAULT_ELEMENTS.containsKey(key);
    }

    public boolean hasInnerConfig(String key)
    {
        return INNER_CONFIGS.containsKey(key);
    }

    public JsonElement get(String key)
    {
        return ELEMENTS.get(key);
    }

    public JsonElement getDefault(String key)
    {
        return DEFAULT_ELEMENTS.get(key);
    }

    public String getString(String key, String defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getString(key);
    }

    public int getInt(String key, int defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getInt(key);
    }

    public float getFloat(String key, float defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getFloat(key);
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getBoolean(key);
    }

    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue.toString().toLowerCase()));
        return getEnum(key, enumClass);
    }

    public IBlockState getBlock(String key, IBlockState defaultValue)
    {
        JsonObject object = new JsonObject();
        JsonObject properties = new JsonObject();
        object.addProperty("block", defaultValue.getBlock().getRegistryName().toString());

        for(Map.Entry<IProperty<?>, Comparable<?>> entry : defaultValue.getProperties().entrySet())
        {
            properties.addProperty(entry.getKey().getName(), entry.getValue().toString().toLowerCase());
        }

        object.add("properties", properties);
        addDefault(key, object);
        return getBlock(key);
    }

    public Config getInnerConfig(String key, JsonObject defaultValue)
    {
        addDefault(key, defaultValue);
        return getInnerConfig(key);
    }

    public String getString(String key)
    {
        if(has(key) && isString(get(key)))
        {
            return get(key).getAsJsonPrimitive().getAsString();
        }
        else if(hasDefault(key) && isString(getDefault(key)))
        {
            return getDefault(key).getAsJsonPrimitive().getAsString();
        }
        else
        {
            return "MissingNo";
        }
    }

    public int getInt(String key)
    {
        if(has(key) && isInt(get(key)))
        {
            return get(key).getAsInt();
        }
        else if(hasDefault(key) && isInt(getDefault(key)))
        {
            return getDefault(key).getAsInt();
        }
        else
        {
            return 0;
        }

    }

    public float getFloat(String key)
    {
        if(has(key) && isFloat(get(key)))
        {
            return get(key).getAsFloat();
        }
        else if(hasDefault(key) && isFloat(getDefault(key)))
        {
            return getDefault(key).getAsFloat();
        }
        else
        {
            return 0.0F;
        }
    }

    public boolean getBoolean(String key)
    {
        if(has(key) && isBoolean(get(key)))
        {
            return get(key).getAsBoolean();
        }
        else if(hasDefault(key) && isBoolean(getDefault(key)))
        {
            return getDefault(key).getAsBoolean();
        }
        else
        {
            return false;
        }
    }

    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass)
    {
        String enumIdentifier;

        if(has(key) && isString(get(key)))
        {
            enumIdentifier = get(key).getAsJsonPrimitive().getAsString();
        }
        else if(hasDefault(key) && isString(getDefault(key)))
        {
            enumIdentifier = getDefault(key).getAsJsonPrimitive().getAsString();
        }
        else
        {
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
        JsonObject object;

        if(has(key) && isObject(get(key)))
        {
            object = get(key).getAsJsonObject();
        }
        else if(hasDefault(key) && isObject(getDefault(key)))
        {
            object = getDefault(key).getAsJsonObject();
        }
        else
        {
            return Blocks.AIR.getDefaultState();
        }

        if(object.has("block"))
        {
            JsonElement blockName = object.get("block");

            if(isString(blockName))
            {
                Block block = Block.getBlockFromName(blockName.getAsJsonPrimitive().getAsString());
                IBlockState state = block.getDefaultState();

                if(object.has("properties"))
                {
                    JsonElement properties = object.get("properties");

                    if(isObject(properties))
                    {
                        for(Map.Entry<String, JsonElement> entry : properties.getAsJsonObject().entrySet())
                        {
                            IProperty property = BlockStateUtils.getProperty(state, entry.getKey());

                            if(property != null && isString(entry.getValue()))
                            {
                                Comparable propertyValue = BlockStateUtils.getPropertyValue(property, entry.getValue().getAsJsonPrimitive().getAsString());

                                if(propertyValue != null)
                                {
                                    state = state.withProperty(property, propertyValue);
                                }
                            }
                        }
                    }
                }

                return state;
            }
            else
            {
                return Blocks.AIR.getDefaultState();
            }
        }
        else
        {
            return Blocks.AIR.getDefaultState();
        }
    }

    public Config getInnerConfig(String key)
    {
        if(hasInnerConfig(key))
        {
            return INNER_CONFIGS.get(key);
        }

        Config config = null;

        if(has(key) && isObject(get(key)))
        {
            config = ConfigFactory.parseString(get(key).toString());
        }
        else if(hasDefault(key) && isObject(getDefault(key)))
        {
            config = ConfigFactory.parseString(getDefault(key).toString());
        }

        INNER_CONFIGS.put(key, config);
        return config;
    }

    protected File getFile()
    {
        return CONFIG_FILE;
    }

    protected Map<String, JsonElement> getElements()
    {
        return ELEMENTS;
    }

    protected Map<String, JsonElement> getDefaultElements()
    {
        return DEFAULT_ELEMENTS;
    }

    protected Map<String, Config> getInnerConfigs()
    {
        return INNER_CONFIGS;
    }

    public boolean isInnerConfig()
    {
        return INNER_CONFIG;
    }
}
