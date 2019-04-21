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

package logictechcorp.libraryex.item.builder;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;

public class ItemProperties
{
    private int maxDamage;
    private int maxStackSize = 64;
    private Item containerItem;
    private CreativeTabs creativeTab;
    private EnumRarity rarity = EnumRarity.COMMON;
    private boolean repairable = true;

    public ItemProperties maxDamage(int maxDamage)
    {
        this.maxDamage = maxDamage;
        this.maxStackSize = 1;
        return this;
    }

    public ItemProperties maxStackSize(int maxStackSize)
    {
        if(this.maxDamage > 0)
        {
            throw new RuntimeException("Unable to have damage AND stack size.");
        }
        else
        {
            this.maxStackSize = maxStackSize;
            return this;
        }
    }

    public ItemProperties containerItem(Item containerItem)
    {
        this.containerItem = containerItem;
        return this;
    }

    public ItemProperties creativeTab(CreativeTabs creativeTab)
    {
        this.creativeTab = creativeTab;
        return this;
    }

    public ItemProperties rarity(EnumRarity rarity)
    {
        this.rarity = rarity;
        return this;
    }

    public ItemProperties unrepairable()
    {
        this.repairable = false;
        return this;
    }

    public ItemProperties copy()
    {
        ItemProperties properties = new ItemProperties();
        properties.maxDamage = this.maxDamage;
        properties.maxStackSize = this.maxStackSize;
        properties.containerItem = this.containerItem;
        properties.creativeTab = this.creativeTab;
        properties.rarity = this.rarity;
        properties.repairable = this.repairable;
        return properties;
    }

    public int getMaxDamage()
    {
        return this.maxDamage;
    }

    public int getMaxStackSize()
    {
        return this.maxStackSize;
    }

    public Item getContainerItem()
    {
        return this.containerItem;
    }

    public CreativeTabs getCreativeTab()
    {
        return this.creativeTab;
    }

    public EnumRarity getRarity()
    {
        return this.rarity;
    }

    public boolean isRepairable()
    {
        return this.repairable;
    }
}
