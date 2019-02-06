/*
 * LibraryEx
 * Copyright (c) 2017-2019 by LogicTechCorp
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

package logictechcorp.libraryex.utility;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.json.JsonFormat;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigHelper
{
    public static <E extends Enum> E getEnum(Config config, String path, Class<? extends E> cls)
    {
        if(config.get(path) instanceof String)
        {
            String enumName = config.get(path);

            if(enumName.equalsIgnoreCase("random") || enumName.equalsIgnoreCase("rand"))
            {
                return cls.getEnumConstants()[RandomHelper.getRandom().nextInt(cls.getEnumConstants().length)];
            }
            else
            {
                for(E value : cls.getEnumConstants())
                {
                    if(value.toString().equalsIgnoreCase(enumName))
                    {
                        return value;
                    }
                }
            }
        }

        return null;
    }

    public static IBlockState getBlockState(Config config, String path)
    {
        if(config.contains(path))
        {
            ResourceLocation registryKey = null;

            if(config.get(path + ".block") instanceof String)
            {
                registryKey = new ResourceLocation(config.get(path + ".block"));
            }
            else if(config.get(path + "itemBlock") instanceof String)
            {
                registryKey = new ResourceLocation(config.get(path + ".itemBlock"));
            }

            Block block = ForgeRegistries.BLOCKS.getValue(registryKey);

            if(block != null && block != Blocks.AIR)
            {
                IBlockState state = block.getDefaultState();

                if(config.contains(path + ".properties"))
                {
                    Config properties = config.get(path + ".properties");

                    for(Config.Entry entry : properties.entrySet())
                    {
                        IProperty property = BlockStateHelper.getProperty(state, entry.getKey());

                        if(property != null)
                        {
                            Comparable propertyValue = BlockStateHelper.getPropertyValue(property, entry.getValue());

                            if(propertyValue != null)
                            {
                                state = state.withProperty(property, propertyValue);
                            }
                        }
                    }
                }

                return state;
            }
        }

        return null;
    }

    public static ItemStack getItemStack(Config config, String path)
    {
        if(config.contains(path))
        {
            ItemStack stack = ItemStack.EMPTY;

            if(config.get(path + ".item") instanceof String)
            {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(config.get(path + ".item")));

                if(item != null && item != Items.AIR)
                {
                    stack = new ItemStack(item, 1, config.getOrElse(path + ".meta", 0));
                }
            }
            else if(config.get(path + ".itemBlock") instanceof String)
            {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(config.get(path + ".itemBlock")));

                if(block != null && block != Blocks.AIR)
                {
                    IBlockState state = block.getDefaultState();
                    if(config.contains(path + ".properties"))
                    {
                        Config propertyConfig = config.get(path + ".properties");

                        for(Config.Entry entry : propertyConfig.entrySet())
                        {
                            IProperty property = BlockStateHelper.getProperty(state, entry.getKey());

                            if(property != null)
                            {
                                Comparable propertyValue = BlockStateHelper.getPropertyValue(property, entry.getValue());

                                if(propertyValue != null)
                                {
                                    state = state.withProperty(property, propertyValue);
                                }
                            }
                        }
                    }

                    stack = new ItemStack(block, 1, block.getMetaFromState(state));
                }
            }

            if(!stack.isEmpty())
            {
                int count = 0;

                if(config.contains(path + ".minCount") || config.contains(path + ".maxCount"))
                {
                    count = RandomHelper.getNumberInRange(config.getOrElse(path + ".minCount", 1), config.getOrElse(path + ".maxCount", stack.getMaxStackSize()), RandomHelper.getRandom());
                }
                else if(config.contains(path + ".count"))
                {
                    count = config.get(path + ".count");
                }

                if(count < 1)
                {
                    count = 1;
                    config.set(path + ".minCount", count);
                }
                else if(count > stack.getMaxStackSize())
                {
                    count = stack.getMaxStackSize();
                    config.set(path + ".maxCount", count);
                }

                stack.setCount(count);

                if(config.contains(path + ".displayName"))
                {
                    stack.setStackDisplayName(config.get(path + ".displayName"));
                }

                List<String> loreList = config.get(path + ".lore");

                if(loreList != null && loreList.size() > 0)
                {
                    NBTHelper.ensureTagExists(stack);
                    NBTTagList loreTagList = new NBTTagList();

                    for(String lore : loreList)
                    {
                        loreTagList.appendTag(new NBTTagString(lore));
                    }

                    NBTTagCompound displayCompound = new NBTTagCompound();
                    displayCompound.setTag("Lore", loreTagList);
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setTag("display", displayCompound);
                    NBTHelper.setTagIfNotExistent(stack, compound);
                }

                List<Config> enchantments = config.get(path + ".enchantments");

                if(enchantments != null)
                {
                    for(Config enchantmentConfig : enchantments)
                    {
                        Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchantmentConfig.get("enchantment"));

                        if(enchantment != null)
                        {
                            int enchantmentLevel = 0;

                            if(enchantmentConfig.contains("minEnchantmentLevel") || enchantmentConfig.contains("maxEnchantmentLevel"))
                            {
                                enchantmentLevel = RandomHelper.getNumberInRange(enchantmentConfig.getOrElse("minEnchantmentLevel", enchantment.getMinLevel()), enchantmentConfig.getOrElse("maxEnchantmentLevel", enchantment.getMaxLevel()), RandomHelper.getRandom());
                            }
                            else if(enchantmentConfig.contains("enchantmentLevel"))
                            {
                                enchantmentLevel = enchantmentConfig.get("enchantmentLevel");
                            }

                            if(enchantmentLevel < enchantment.getMinLevel())
                            {
                                enchantmentLevel = enchantment.getMinLevel();
                                enchantmentConfig.set("minEnchantmentLevel", enchantmentLevel);
                            }
                            else if(enchantmentLevel > enchantment.getMaxLevel())
                            {
                                enchantmentLevel = enchantment.getMaxLevel();
                                enchantmentConfig.set("maxEnchantmentLevel", enchantmentLevel);
                            }

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

            return stack;
        }

        return ItemStack.EMPTY;
    }

    public static void setBlockState(Config config, String path, IBlockState state)
    {
        if(state != null)
        {
            config.set(path + ".block", state.getBlock().getRegistryName().toString());

            if(state != state.getBlock().getDefaultState())
            {
                for(Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
                {
                    config.set(path + ".properties." + entry.getKey().getName(), entry.getValue().toString().toLowerCase());
                }
            }
        }
    }

    public static void setItemStackComplex(Config config, String path, ItemStack stack)
    {
        if(stack != null)
        {
            Item item = stack.getItem();

            if(item instanceof ItemBlock)
            {
                Config propertyConfig = JsonFormat.newConcurrentConfig();
                IBlockState state = ((ItemBlock) item).getBlock().getStateFromMeta(stack.getMetadata());

                for(Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
                {
                    propertyConfig.set(entry.getKey().getName(), entry.getValue().toString().toLowerCase());
                }

                config.set(path + ".itemBlock", stack.getItem().getRegistryName().toString());
                config.set(path + ".properties", propertyConfig);
            }
            else
            {
                config.set(path + ".item", stack.getItem().getRegistryName().toString());
                config.set(path + ".meta", stack.getMetadata());
            }

            config.set(path + ".count", stack.getCount());

            NBTTagCompound display = stack.getSubCompound("display");

            if(display != null)
            {
                if(display.hasKey("Name", 8))
                {
                    config.set(path + ".displayName", stack.getDisplayName());
                }

                List<String> lore = new ArrayList<>();

                if(display.getTagId("Lore") == 9)
                {
                    NBTTagList loreList = display.getTagList("Lore", 8);

                    if(!loreList.isEmpty())
                    {
                        for(int i = 0; i < loreList.tagCount(); i++)
                        {
                            lore.add(loreList.getStringTagAt(i));
                        }
                    }
                }

                if(lore.size() > 0)
                {
                    config.set(path + ".lore", lore);
                }
            }

            if(stack.isItemEnchanted())
            {
                List<Config> enchantmentConfigs = new ArrayList<>();

                for(Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.getEnchantments(stack).entrySet())
                {
                    Config enchantmentConfig = JsonFormat.newConcurrentConfig();
                    enchantmentConfig.set("enchantment", enchantment.getKey().getRegistryName().toString());
                    enchantmentConfig.set("enchantmentLevel", enchantment.getValue());
                    enchantmentConfigs.add(enchantmentConfig);
                }

                config.set(path + ".enchantments", enchantmentConfigs);
            }
        }
    }

    public static void setItemStackSimple(Config config, String path, ItemStack stack)
    {
        if(stack != null)
        {
            Item item = stack.getItem();

            if(item instanceof ItemBlock)
            {
                Config propertyConfig = JsonFormat.newConcurrentConfig();
                IBlockState state = ((ItemBlock) item).getBlock().getStateFromMeta(stack.getMetadata());

                for(Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
                {
                    propertyConfig.set(entry.getKey().getName(), entry.getValue().toString().toLowerCase());
                }

                config.set(path + ".itemBlock", stack.getItem().getRegistryName().toString());
                config.set(path + ".properties", propertyConfig);
            }
            else
            {
                config.set(path + ".item", stack.getItem().getRegistryName().toString());
                config.set(path + ".meta", stack.getMetadata());
            }
        }
    }
}
