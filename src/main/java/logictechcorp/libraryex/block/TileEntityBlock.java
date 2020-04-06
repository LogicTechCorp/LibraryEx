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

package logictechcorp.libraryex.block;

import logictechcorp.libraryex.tileentity.TileEntityInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class TileEntityBlock<T extends TileEntity> extends Block
{
    private final Class<T> cls;

    public TileEntityBlock(Block.Properties properties, Class<T> cls)
    {
        super(properties);
        this.cls = cls;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof TileEntityInventory)
        {
            ((TileEntityInventory) tileEntity).dropInventoryItems(world, pos);
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        try
        {
            return this.cls.newInstance();
        }
        catch(InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity == null ? false : tileEntity.receiveClientEvent(id, param);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof INamedContainerProvider ? (INamedContainerProvider) tileEntity : null;
    }
}
