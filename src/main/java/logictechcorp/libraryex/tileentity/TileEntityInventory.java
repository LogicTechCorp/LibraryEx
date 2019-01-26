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

package logictechcorp.libraryex.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public class TileEntityInventory extends TileEntity
{
    private ItemStackHandler inventory;
    private Random random;

    public TileEntityInventory(int size)
    {
        this.inventory = new ItemStackHandler(size);
        this.random = new Random();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("Inventory", this.inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new SPacketUpdateTileEntity(this.pos, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this.inventory : null;
    }

    public void dropInventoryItems(World world, BlockPos pos)
    {
        for(int i = 0; i < this.inventory.getSlots(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);

            if(!stack.isEmpty())
            {
                this.spawnItemStack(world, pos, stack);
            }
        }
    }

    public void spawnItemStack(World world, BlockPos pos, ItemStack stack)
    {
        double offsetX = this.random.nextFloat() * 0.8F + 0.1F;
        double offsetY = this.random.nextFloat() * 0.8F + 0.1F;
        double offsetZ = this.random.nextFloat() * 0.8F + 0.1F;

        while(!stack.isEmpty())
        {
            EntityItem entityItem = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack.splitStack(this.random.nextInt(21) + 10));
            entityItem.motionX = this.random.nextGaussian() * 0.05000000074505806D;
            entityItem.motionY = this.random.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
            entityItem.motionZ = this.random.nextGaussian() * 0.05000000074505806D;
            world.spawnEntity(entityItem);
        }
    }

    public ItemStackHandler getInventory()
    {
        return this.inventory;
    }

    public Random getRand()
    {
        return this.random;
    }
}
