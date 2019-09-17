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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class NBTHelper
{
    public static CompoundNBT ensureTagExists(ItemStack stack)
    {
        return setTagIfNonExistent(stack, new CompoundNBT());
    }

    public static CompoundNBT setTagIfNonExistent(ItemStack stack, CompoundNBT compound)
    {
        if(stack.getTag() == null)
        {
            stack.setTag(compound);
        }
        else if(!compound.isEmpty())
        {
            stack.getTag().merge(compound);
        }
        return stack.getTag();
    }
}