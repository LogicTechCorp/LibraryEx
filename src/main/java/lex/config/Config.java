/*
 * LibEx
 * Copyright (c) 2017-2018 by MineEx
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
import lex.api.config.IConfig;
import lex.util.BlockStateHelper;
import lex.util.NBTHelper;
import lex.util.NumberHelper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lex.util.ConfigHelper.*;

public class Config implements IConfig
{
    protected static final JsonParser JSON_PARSER = new JsonParser();

    protected final Map<String, JsonElement> DATA = new LinkedHashMap<>();
    protected final Map<String, JsonElement> FALLBACK_DATA = new LinkedHashMap<>();
    protected final Map<String, IConfig> DATA_BRANCHES = new LinkedHashMap<>();

    public Config(File configFile)
    {
        String jsonString = new JsonObject().toString();

        if(configFile.exists())
        {
            try
            {
                jsonString = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        deserialize(jsonString);
    }

    public Config(String jsonString)
    {
        deserialize(jsonString);
    }

    @Override
    public void deserialize(String jsonString)
    {
        if(!Strings.isBlank(jsonString))
        {
            JsonElement element = JSON_PARSER.parse(jsonString);

            if(isObject(element))
            {
                for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
                {
                    DATA.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject object = new JsonObject();

        for(Map.Entry<String, IConfig> entry : DATA_BRANCHES.entrySet())
        {
            if(hasData(entry.getKey()))
            {
                addData(entry.getKey(), entry.getValue().serialize());
            }
            else if(hasFallbackData(entry.getKey()))
            {
                addFallbackData(entry.getKey(), entry.getValue().serialize());
            }
        }

        for(Map.Entry<String, JsonElement> entry : DATA.entrySet())
        {
            object.add(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String, JsonElement> entry : FALLBACK_DATA.entrySet())
        {
            if(!object.has(entry.getKey()))
            {
                object.add(entry.getKey(), entry.getValue());
            }
        }

        return object;
    }

    @Override
    public void addData(String key, JsonElement element)
    {
        DATA.put(key, element);
    }

    @Override
    public void addFallbackData(String key, JsonElement element)
    {
        FALLBACK_DATA.put(key, element);
    }

    @Override
    public void addDataBranch(String key, IConfig config)
    {
        DATA_BRANCHES.put(key, config);
    }

    @Override
    public boolean hasData(String key)
    {
        return DATA.containsKey(key);
    }

    @Override
    public boolean hasFallbackData(String key)
    {
        return FALLBACK_DATA.containsKey(key);
    }

    @Override
    public boolean hasDataBranch(String key)
    {
        return DATA_BRANCHES.containsKey(key);
    }

    @Override
    public JsonElement getData(String key)
    {
        return DATA.get(key);
    }

    @Override
    public JsonElement getFallbackData(String key)
    {
        return FALLBACK_DATA.get(key);
    }

    @Override
    public Map<String, JsonElement> getAllData()
    {
        return ImmutableMap.copyOf(DATA);
    }

    @Override
    public void removeData(String key)
    {
        DATA.remove(key);
    }

    @Override
    public String getString(String key, String fallbackValue)
    {
        String value = getString(key);

        if(value.equals("MissingNo"))
        {
            addFallbackData(key, new JsonPrimitive(fallbackValue));
            return fallbackValue;
        }

        return value;
    }

    @Override
    public int getInt(String key, int fallbackValue)
    {
        int value = getInt(key);

        if(value == -999)
        {
            addFallbackData(key, new JsonPrimitive(fallbackValue));
            return fallbackValue;
        }

        return value;
    }

    @Override
    public float getFloat(String key, float fallbackValue)
    {
        float value = getFloat(key);

        if(value == -999.0F)
        {
            addFallbackData(key, new JsonPrimitive(fallbackValue));
            return fallbackValue;
        }

        return value;
    }

    @Override
    public boolean getBoolean(String key, boolean fallbackValue)
    {
        boolean value = getBoolean(key);

        if(!isBoolean(getData(key)))
        {
            addFallbackData(key, new JsonPrimitive(fallbackValue));
            return fallbackValue;
        }

        return value;
    }

    @Override
    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E fallbackValue)
    {
        E value = getEnum(key, enumClass);

        if(value == null)
        {
            addFallbackData(key, new JsonPrimitive(fallbackValue.name().toLowerCase()));
            return fallbackValue;
        }

        return value;
    }

    @Override
    public ResourceLocation getResource(String key, ResourceLocation fallbackValue)
    {
        ResourceLocation value = getResource(key);

        if(value == null)
        {
            addFallbackData(key, new JsonPrimitive(fallbackValue.toString()));
            return fallbackValue;
        }

        return value;
    }

    @Override
    public IBlockState getBlock(String key, IBlockState fallbackValue)
    {
        IBlockState value = getBlock(key);

        if(value == null)
        {
            JsonObject block = new JsonObject();
            JsonObject properties = new JsonObject();
            block.addProperty("block", fallbackValue.getBlock().getRegistryName().toString());

            for(Map.Entry<IProperty<?>, Comparable<?>> entry : fallbackValue.getProperties().entrySet())
            {
                properties.addProperty(entry.getKey().getName(), entry.getValue().toString().toLowerCase());
            }

            block.add("properties", properties);
            addFallbackData(key, block);
            return fallbackValue;
        }

        return value;
    }

    @Override
    public ItemStack getItem(String key, ItemStack fallbackValue)
    {
        ItemStack value = getItem(key);

        if(value.isEmpty())
        {
            JsonObject item = new JsonObject();
            item.addProperty("item", fallbackValue.getItem().getRegistryName().toString());
            item.addProperty("meta", fallbackValue.getItemDamage());
            addFallbackData(key, item);
            return fallbackValue;
        }

        return value;
    }

    @Override
    public IConfig getDataBranch(String key, JsonObject fallbackValue)
    {
        IConfig value = getDataBranch(key);

        if(value == null)
        {
            addFallbackData(key, fallbackValue);
            return new Config(fallbackValue.toString());
        }

        return value;
    }

    @Override
    public String getString(String key)
    {
        if(isString(getData(key)))
        {
            return getData(key).getAsJsonPrimitive().getAsString();
        }
        else
        {
            return "MissingNo";
        }
    }

    @Override
    public int getInt(String key)
    {
        if(isInt(getData(key)))
        {
            return getData(key).getAsJsonPrimitive().getAsInt();
        }
        else
        {
            return -999;
        }
    }

    @Override
    public float getFloat(String key)
    {
        if(isFloat(getData(key)))
        {
            return getData(key).getAsJsonPrimitive().getAsFloat();
        }
        else
        {
            return -999.0F;
        }
    }

    @Override
    public boolean getBoolean(String key)
    {
        if(isBoolean(getData(key)))
        {
            return getData(key).getAsJsonPrimitive().getAsBoolean();
        }
        else
        {
            return false;
        }
    }

    @Override
    public <E extends Enum> E getEnum(String key, Class<? extends E> enumClass)
    {
        if(isString(getData(key)))
        {
            String enumIdentifier = getData(key).getAsJsonPrimitive().getAsString();

            for(E value : enumClass.getEnumConstants())
            {
                if(value.name().equalsIgnoreCase(enumIdentifier))
                {
                    return value;
                }
            }
        }

        return null;
    }

    @Override
    public ResourceLocation getResource(String key)
    {
        if(isString(getData(key)))
        {
            return new ResourceLocation(getString(key));
        }

        return null;
    }

    @Override
    public IBlockState getBlock(String key)
    {
        JsonObject object;

        if(isObject(getData(key)))
        {
            object = getData(key).getAsJsonObject();
        }
        else
        {
            return null;
        }

        JsonElement blockName = null;

        if(isString(object.get("block")))
        {
            blockName = object.get("block");

        }
        else if(isString(object.get("itemBlock")))
        {
            blockName = object.get("itemBlock");
        }

        if(blockName != null)
        {
            Block block = Block.getBlockFromName(blockName.getAsJsonPrimitive().getAsString());

            if(block != null)
            {
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
        }

        return null;
    }

    @Override
    public ItemStack getItem(String key)
    {
        IConfig itemConfig = getDataBranch(key);
        ItemStack stack = ItemStack.EMPTY;

        if(itemConfig != null)
        {
            ResourceLocation item = null;

            if(isString(itemConfig.getData("item")))
            {
                item = itemConfig.getResource("item");
            }
            else if(isString(itemConfig.getData("itemBlock")))
            {
                item = itemConfig.getResource("itemBlock");
            }

            if(item != null)
            {
                int meta = itemConfig.getInt("meta", 0);

                if(ForgeRegistries.ITEMS.containsKey(item))
                {
                    stack = new ItemStack(Item.getByNameOrId(item.toString()), 1, meta);
                }
                else if(ForgeRegistries.BLOCKS.containsKey(item))
                {
                    IBlockState state = getBlock(key);
                    Block block = state.getBlock();
                    stack = new ItemStack(block, 1, block.getMetaFromState(state));
                }

                if(!stack.isEmpty())
                {
                    if(isString(itemConfig.getData("displayName")))
                    {
                        stack.setStackDisplayName(itemConfig.getString("displayName"));
                    }

                    IConfig loreConfig = getDataBranch("lore");

                    if(loreConfig != null && loreConfig.getAllData().size() > 0)
                    {
                        NBTHelper.setTag(stack);
                        NBTTagList loreList = new NBTTagList();

                        for(Map.Entry<String, JsonElement> entry : loreConfig.getAllData().entrySet())
                        {
                            if(isString(entry.getValue()))
                            {
                                loreList.appendTag(new NBTTagString(entry.getValue().getAsJsonPrimitive().getAsString()));
                            }
                        }

                        NBTTagCompound displayCompound = new NBTTagCompound();
                        displayCompound.setTag("Lore", loreList);
                        NBTTagCompound compound = new NBTTagCompound();
                        compound.setTag("display", displayCompound);
                        NBTHelper.setTag(stack, compound);
                    }

                    List<IConfig> enchantmentConfigs = itemConfig.getDataBranches("enchantments");

                    if(enchantmentConfigs != null)
                    {
                        for(IConfig enchantmentConfig : enchantmentConfigs)
                        {
                            if(isString(enchantmentConfig.getData("enchantment")))
                            {
                                Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchantmentConfig.getString("enchantment"));

                                if(enchantment != null)
                                {
                                    int enchantmentLevel = NumberHelper.getNumberInRange(enchantmentConfig.getInt("minEnchantmentLevel", 1), enchantmentConfig.getInt("minEnchantmentLevel", 3), NumberHelper.getRand());

                                    if(stack.getItem() instanceof ItemEnchantedBook)
                                    {
                                        ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, enchantmentLevel));
                                    }
                                    else
                                    {
                                        stack.addEnchantment(enchantment, enchantmentLevel);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public IConfig getDataBranch(String key)
    {
        if(hasDataBranch(key))
        {
            return DATA_BRANCHES.get(key);
        }
        else if(isObject(getData(key)))
        {
            IConfig config = new Config(getData(key).toString());
            DATA_BRANCHES.put(key, config);
            return config;
        }

        return null;
    }

    @Override
    public List<IConfig> getDataBranches(String key, List<JsonObject> fallbackValue)
    {
        List<IConfig> value = getDataBranches(key);

        if(value == null)
        {
            JsonArray array = new JsonArray();
            fallbackValue.forEach(array::add);
            addFallbackData(key, array);

            List<IConfig> ret = new ArrayList<>();
            fallbackValue.forEach(k -> ret.add(new Config(k.toString())));
            return ret;
        }

        return value;
    }

    @Override
    public List<IConfig> getDataBranches(String key)
    {
        if(isArray(getData(key)))
        {
            JsonArray array = getData(key).getAsJsonArray();
            List<IConfig> subConfigs = new ArrayList<>();

            for(JsonElement element : array)
            {
                if(isObject(element))
                {
                    subConfigs.add(new Config(element.toString()));
                }
            }

            return subConfigs;
        }
        else
        {
            return null;
        }
    }

    @Override
    public List<String> getStrings(String key, List<String> fallbackValue)
    {
        List<String> value = getStrings(key);

        if(value == null)
        {
            JsonArray array = new JsonArray();
            fallbackValue.forEach(array::add);
            addFallbackData(key, array);
            return fallbackValue;
        }

        return value;
    }

    @Override
    public List<String> getStrings(String key)
    {
        if(isArray(getData(key)))
        {
            JsonArray array = getData(key).getAsJsonArray();
            List<String> strings = new ArrayList<>();

            for(JsonElement element : array)
            {
                if(isPrimitive(element))
                {
                    strings.add(element.getAsJsonPrimitive().getAsString());
                }
            }

            return strings;
        }
        else
        {
            return null;
        }
    }
}
