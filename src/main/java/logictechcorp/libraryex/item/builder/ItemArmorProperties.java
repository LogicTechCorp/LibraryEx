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

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemArmorProperties extends ItemProperties
{
    private ItemArmor.ArmorMaterial armorMaterial;
    private EntityEquipmentSlot equipmentSlot;

    public ItemArmorProperties(ItemArmor.ArmorMaterial armorMaterial, EntityEquipmentSlot equipmentSlot)
    {
        this.armorMaterial = armorMaterial;
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public ItemArmorProperties copy()
    {
        ItemArmorProperties properties = (ItemArmorProperties) super.copy();
        properties.armorMaterial = this.armorMaterial;
        properties.equipmentSlot = this.equipmentSlot;
        return properties;
    }

    public ItemArmor.ArmorMaterial getArmorMaterial()
    {
        return this.armorMaterial;
    }

    public EntityEquipmentSlot getEquipmentSlot()
    {
        return this.equipmentSlot;
    }
}
