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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorHelper
{
    public static boolean isWearingFullArmorSet(PlayerEntity player, IArmorMaterial material)
    {
        Iterable<ItemStack> armor = player.getArmorInventoryList();
        List<IArmorMaterial> armorMaterials = new ArrayList<>();

        for(ItemStack testStack : armor)
        {
            if(testStack == ItemStack.EMPTY || !(testStack.getItem() instanceof ArmorItem))
            {
                return false;
            }

            armorMaterials.add(((ArmorItem) testStack.getItem()).getArmorMaterial());
        }

        for(IArmorMaterial testMaterial : armorMaterials)
        {
            if(testMaterial != material)
            {
                return false;
            }
        }

        return true;
    }

}
