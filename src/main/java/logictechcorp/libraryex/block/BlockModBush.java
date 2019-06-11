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

import logictechcorp.libraryex.block.property.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class BlockModBush extends BlockMod implements IPlantable
{
    protected static final AxisAlignedBB BUSH_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D, 0.699999988079071D);

    public BlockModBush(ResourceLocation registryName, BlockProperties properties)
    {
        super(registryName, properties);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public abstract EnumPlantType getPlantType(IBlockAccess world, BlockPos pos);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BUSH_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        IBlockState soil = world.getBlockState(pos.down());
        return super.canPlaceBlockAt(world, pos) && this.canSustainBush(soil);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);

        if(state.getBlock() != this)
        {
            return this.getDefaultState();
        }

        return state;
    }

    protected abstract boolean canSustainBush(IBlockState state);

    protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state)
    {
        if(!this.canBlockStay(world, pos, state))
        {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        if(state.getBlock() == this)
        {
            IBlockState soil = world.getBlockState(pos.down());
            return this.canSustainBush(soil);
        }

        return this.canSustainBush(world.getBlockState(pos.down()));
    }
}
