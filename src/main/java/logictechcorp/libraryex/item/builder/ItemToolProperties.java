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

import net.minecraft.item.Item;

public class ItemToolProperties extends ItemProperties
{
    private Item.ToolMaterial toolMaterial;
    private float attackDamage;
    private float attackSpeed;

    public ItemToolProperties(Item.ToolMaterial toolMaterial)
    {
        this.toolMaterial = toolMaterial;
    }

    public ItemToolProperties(Item.ToolMaterial toolMaterial, float attackDamage, float attackSpeed)
    {
        this.toolMaterial = toolMaterial;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    @Override
    public ItemProperties maxStackSize(int maxStackSize)
    {
        return super.maxStackSize(1);
    }

    @Override
    public ItemProperties maxDamage(int maxDamage)
    {
        return super.maxDamage(this.toolMaterial.getMaxUses());
    }

    @Override
    public ItemToolProperties copy()
    {
        ItemToolProperties properties = (ItemToolProperties) super.copy();
        properties.toolMaterial = this.toolMaterial;
        properties.attackDamage = this.attackDamage;
        properties.attackSpeed = this.attackSpeed;
        return properties;
    }

    public Item.ToolMaterial getToolMaterial()
    {
        return this.toolMaterial;
    }

    public float getAttackDamage()
    {
        return this.attackDamage;
    }

    public float getAttackSpeed()
    {
        return this.attackSpeed;
    }
}
