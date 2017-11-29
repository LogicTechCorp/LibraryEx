/*
 * LibEx
 * Copyright (c) 2017 by MineEx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lex.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import lex.util.BlockStateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lex.util.ConfigHelper.*;

public abstract class AbstractConfig implements IConfig
{
    private static final JsonParser JSON_PARSER = new JsonParser();

    protected final Map<String, JsonElement> ELEMENT_MAP = new LinkedHashMap<>();
    protected final Map<String, JsonElement> DEFAULT_ELEMENT_MAP = new LinkedHashMap<>();
    protected final Map<String, InnerConfig> INNER_CONFIGS = new LinkedHashMap<>();

    @Override
    public void parse(String jsonString)
    {
        if(!Strings.isBlank(jsonString))
        {
            JsonElement element = JSON_PARSER.parse(jsonString);

            if(isObject(element))
            {
                for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
                {
                    ELEMENT_MAP.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Override
    public JsonElement compose()
    {
        JsonObject object = new JsonObject();

        for(Map.Entry<String, InnerConfig> entry : INNER_CONFIGS.entrySet())
        {
            if(has(entry.getKey()))
            {
                add(entry.getKey(), entry.getValue().compose());
            }
            else if(hasDefault(entry.getKey()))
            {
                addDefault(entry.getKey(), entry.getValue().compose());
            }
        }

        for(Map.Entry<String, JsonElement> entry : ELEMENT_MAP.entrySet())
        {
            object.add(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String, JsonElement> entry : DEFAULT_ELEMENT_MAP.entrySet())
        {
            if(!object.has(entry.getKey()))
            {
                object.add(entry.getKey(), entry.getValue());
            }
        }

        return object;
    }

    public void add(String key, JsonElement element)
    {
        ELEMENT_MAP.put(key, element);
    }

    public void addDefault(String key, JsonElement element)
    {
        DEFAULT_ELEMENT_MAP.put(key, element);
    }

    public void addInnerConfig(String key, InnerConfig config)
    {
        INNER_CONFIGS.put(key, config);
    }

    @Override
    public boolean has(String key)
    {
        return ELEMENT_MAP.containsKey(key);
    }

    public boolean hasDefault(String key)
    {
        return DEFAULT_ELEMENT_MAP.containsKey(key);
    }

    public boolean hasInnerConfig(String key)
    {
        return INNER_CONFIGS.containsKey(key);
    }

    @Override
    public JsonElement get(String key)
    {
        return ELEMENT_MAP.get(key);
    }

    public JsonElement getDefault(String key)
    {
        return DEFAULT_ELEMENT_MAP.get(key);
    }

    @Override
    public Map<String, JsonElement> getElementMap()
    {
        return ImmutableMap.copyOf(ELEMENT_MAP);
    }

    @Override
    public String getString(String key, String defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getString(key);
    }

    @Override
    public int getInt(String key, int defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getInt(key);
    }

    @Override
    public float getFloat(String key, float defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getFloat(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue));
        return getBoolean(key);
    }

    @Override
    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E defaultValue)
    {
        addDefault(key, new JsonPrimitive(defaultValue.toString().toLowerCase()));
        return getEnum(key, enumClass);
    }

    @Override
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

    @Override
    public IConfig getInnerConfig(String key, JsonObject defaultValue)
    {
        addDefault(key, defaultValue);
        return getInnerConfig(key);
    }

    @Override
    public List<IConfig> getInnerConfigs(String key, List<JsonObject> defaultValue)
    {
        JsonArray array = new JsonArray();
        defaultValue.forEach(array::add);
        addDefault(key, array);
        return getInnerConfigs(key);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
                            IProperty property = BlockStateHelper.getProperty(state, entry.getKey());

                            if(property != null && isString(entry.getValue()))
                            {
                                Comparable propertyValue = BlockStateHelper.getPropertyValue(property, entry.getValue().getAsJsonPrimitive().getAsString());

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

    @Override
    public IConfig getInnerConfig(String key)
    {
        if(hasInnerConfig(key))
        {
            return INNER_CONFIGS.get(key);
        }

        InnerConfig config = null;

        if(has(key) && isObject(get(key)))
        {
            config = new InnerConfig(get(key).getAsJsonObject());
        }
        else if(hasDefault(key) && isObject(getDefault(key)))
        {
            config = new InnerConfig(getDefault(key).getAsJsonObject());
        }

        if(config != null)
        {
            INNER_CONFIGS.put(key, config);
        }

        return config;
    }

    @Override
    public List<IConfig> getInnerConfigs(String key)
    {
        JsonArray array = null;
        List<IConfig> innerConfigs;

        if(has(key) && isArray(get(key)))
        {
            array = get(key).getAsJsonArray();
        }
        else if(hasDefault(key) && isArray(getDefault(key)))
        {
            array = getDefault(key).getAsJsonArray();
        }

        if(!isNull(array))
        {
            innerConfigs = new ArrayList<>();

            for(JsonElement element : array)
            {
                if(isObject(element))
                {
                    innerConfigs.add(new InnerConfig(element.toString()));
                }
            }

            return innerConfigs;
        }

        return null;
    }

    @Override
    public List<String> getStrings(String key, List<String> defaultValue)
    {
        JsonArray array = new JsonArray();

        for(String string : defaultValue)
        {
            array.add(string);
        }

        addDefault(key, array);
        return getStrings(key);
    }

    @Override
    public List<String> getStrings(String key)
    {
        JsonArray array = new JsonArray();
        List<String> stringList = new ArrayList<>();

        if(has(key) && isArray(get(key)))
        {
            array = get(key).getAsJsonArray();
        }
        else if(hasDefault(key) && isArray(getDefault(key)))
        {
            array = getDefault(key).getAsJsonArray();
        }

        for(JsonElement element : array)
        {
            if(isPrimitive(element))
            {
                stringList.add(element.getAsJsonPrimitive().getAsString());
            }
        }

        return stringList;
    }
}
