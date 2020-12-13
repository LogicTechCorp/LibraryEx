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

package logictechcorp.libraryex.item;

import logictechcorp.libraryex.item.builder.ItemProperties;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMod extends ItemBlock
{
    private CreativeTabs creativeTab;
    private final EnumRarity rarity;

    public ItemBlockMod(Block block, ItemProperties properties)
    {
        super(block);
        this.setRegistryName(block.getRegistryName());
        this.setTranslationKey(block.getRegistryName().toString());
        this.setMaxDamage(properties.getMaxDamage());
        this.setMaxStackSize(properties.getMaxStackSize());
        this.setContainerItem(properties.getContainerItem());
        this.setCreativeTab(properties.getCreativeTab());
        this.rarity = properties.getRarity();

        if(!properties.isRepairable())
        {
            this.setNoRepair();
        }
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return this.creativeTab;
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return this.rarity;
    }

    @Override
    public Item setCreativeTab(CreativeTabs creativeTab)
    {
        this.creativeTab = creativeTab;
        return this;
    }
}
