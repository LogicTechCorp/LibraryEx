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

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class IMCHelper
{
    public static void registerChiseledBlock(String group, ItemStack stack)
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("group", group);
        compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
        FMLInterModComms.sendMessage("chisel", "add_variation", compound);
    }

    public static void registerChiseledBlock(String group, IBlockState state)
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("group", group);
        compound.setString("block", state.getBlock().getRegistryName().toString());
        compound.setInteger("meta", 0);
        FMLInterModComms.sendMessage("chisel", "add_variation", compound);
    }
}
