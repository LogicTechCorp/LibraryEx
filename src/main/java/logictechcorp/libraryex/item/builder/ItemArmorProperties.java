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

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;

public class ItemArmorProperties extends Item.Properties
{
    private IArmorMaterial armorMaterial;
    private EquipmentSlotType equipmentSlot;

    public ItemArmorProperties(IArmorMaterial armorMaterial, EquipmentSlotType equipmentSlot)
    {
        this.armorMaterial = armorMaterial;
        this.equipmentSlot = equipmentSlot;
    }

    public IArmorMaterial getArmorMaterial()
    {
        return this.armorMaterial;
    }

    public EquipmentSlotType getEquipmentSlot()
    {
        return this.equipmentSlot;
    }
}
