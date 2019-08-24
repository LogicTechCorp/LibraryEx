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

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public abstract class TileEntityInventory extends TileEntity
{
    protected ItemStackHandler inventory;
    protected Random random;

    public TileEntityInventory(TileEntityType tileEntityType, int size)
    {
        super(tileEntityType);
        this.inventory = new ItemStackHandler(size);
        this.random = new Random();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.put("Inventory", this.inventory.serializeNBT());
        return compound;
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        this.inventory.deserializeNBT(compound.getCompound("Inventory"));
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
    {
        this.read(packet.getNbtCompound());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (LazyOptional<T>) LazyOptional.of(() -> this.inventory);
        }

        return LazyOptional.empty();
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
            ItemEntity entityItem = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack.split(this.random.nextInt(21) + 10));
            entityItem.setMotion(this.random.nextGaussian() * 0.05000000074505806D, this.random.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, this.random.nextGaussian() * 0.05000000074505806D);
            world.addEntity(entityItem);
        }
    }

    public ItemStackHandler getInventory()
    {
        return this.inventory;
    }

    public Random getRandom()
    {
        return this.random;
    }
}
