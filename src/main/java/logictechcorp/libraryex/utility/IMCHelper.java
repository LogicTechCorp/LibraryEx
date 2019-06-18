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

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.InterModComms;

public class IMCHelper
{
    public static void registerChiseledBlock(String group, ItemStack stack)
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("group", group);
        compound.put("stack", stack.write(new CompoundNBT()));
        InterModComms.sendTo("chisel", "add_variation", () -> compound);
    }

    public static void registerChiseledBlock(String group, BlockState state)
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("group", group);
        compound.putString("block", state.getBlock().getRegistryName().toString());
        InterModComms.sendTo("chisel", "add_variation", () -> compound);
    }
}
