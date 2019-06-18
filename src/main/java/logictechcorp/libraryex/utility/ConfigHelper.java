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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigHelper
{
    public static BlockState getBlockState(Config config, String path)
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
                return block.getDefaultState();
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
                    stack = new ItemStack(item, 1);
                }
            }
            else if(config.get(path + ".itemBlock") instanceof String)
            {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(config.get(path + ".itemBlock")));

                if(block != null && block != Blocks.AIR)
                {
                    stack = new ItemStack(block, 1);
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
                    stack.setDisplayName(new TranslationTextComponent(config.get(path + ".displayName")));
                }

                List<String> loreList = config.get(path + ".lore");

                if(loreList != null && loreList.size() > 0)
                {
                    NBTHelper.ensureTagExists(stack);
                    ListNBT loreTagList = new ListNBT();

                    for(String lore : loreList)
                    {
                        loreTagList.add(new StringNBT(lore));
                    }

                    CompoundNBT displayCompound = new CompoundNBT();
                    displayCompound.put("Lore", loreTagList);
                    CompoundNBT compound = new CompoundNBT();
                    compound.put("display", displayCompound);
                    NBTHelper.setTagIfNotExistent(stack, compound);
                }

                List<Config> enchantments = config.get(path + ".enchantments");

                if(enchantments != null)
                {
                    for(Config enchantmentConfig : enchantments)
                    {
                        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentConfig.get("enchantment")));

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

                            if(stack.getItem() instanceof EnchantedBookItem)
                            {
                                EnchantedBookItem.addEnchantment(stack, new EnchantmentData(enchantment, enchantmentLevel));
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

    public static void setBlockState(Config config, String path, BlockState state)
    {
        config.set(path + ".block", state.getBlock().getRegistryName().toString());
    }

    public static void setItemStackComplex(Config config, String path, ItemStack stack)
    {
        config.set(path + (stack.getItem() instanceof BlockItem ? ".itemBlock" : "item"), stack.getItem().getRegistryName().toString());
        config.set(path + ".count", stack.getCount());

        CompoundNBT display = stack.getChildTag("display");

        if(display != null)
        {
            if(display.contains("Name", 8))
            {
                config.set(path + ".displayName", stack.getDisplayName());
            }

            List<String> lore = new ArrayList<>();

            if(display.getTagId("Lore") == 9)
            {
                ListNBT loreList = display.getList("Lore", 8);

                if(!loreList.isEmpty())
                {
                    for(int i = 0; i < loreList.size(); i++)
                    {
                        lore.add(loreList.getString(i));
                    }
                }
            }

            if(lore.size() > 0)
            {
                config.set(path + ".lore", lore);
            }
        }

        if(stack.isEnchanted())
        {
            List<Config> enchantmentConfigs = new ArrayList<>();

            for(Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.getEnchantments(stack).entrySet())
            {
                Config enchantmentConfig = JsonFormat.newConfig(LinkedHashMap::new);
                enchantmentConfig.set("enchantment", enchantment.getKey().getRegistryName().toString());
                enchantmentConfig.set("enchantmentLevel", enchantment.getValue());
                enchantmentConfigs.add(enchantmentConfig);
            }

            config.set(path + ".enchantments", enchantmentConfigs);
        }
    }

    public static void setItemStackSimple(Config config, String path, ItemStack stack)
    {
        config.set(path + (stack.getItem() instanceof BlockItem ? ".itemBlock" : "item"), stack.getItem().getRegistryName().toString());
    }
}
