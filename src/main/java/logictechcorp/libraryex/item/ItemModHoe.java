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

import logictechcorp.libraryex.item.builder.ItemToolProperties;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemModHoe extends ItemHoe
{
    private EnumRarity rarity;

    public ItemModHoe(ResourceLocation registryName, ItemToolProperties properties)
    {
        super(properties.getToolMaterial());
        this.setRegistryName(registryName);
        this.setTranslationKey(registryName.toString());
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
    public EnumRarity getRarity(ItemStack stack)
    {
        return this.rarity;
    }
}
