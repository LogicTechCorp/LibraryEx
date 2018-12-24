/*
 * LibraryEx
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
 *
 */

package logictechcorp.libraryex.item.builder;

import net.minecraft.item.Item;

public class ItemToolBuilder extends ItemBuilder
{
    private Item.ToolMaterial toolMaterial;
    private float attackDamage;
    private float attackSpeed;

    public ItemToolBuilder(Item.ToolMaterial toolMaterial)
    {
        this.toolMaterial = toolMaterial;
    }

    public ItemToolBuilder attackDamage(float attackDamage)
    {
        this.attackDamage = attackDamage;
        return this;
    }

    public ItemToolBuilder attackSpeed(float attackSpeed)
    {
        this.attackSpeed = attackSpeed;
        return this;
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
