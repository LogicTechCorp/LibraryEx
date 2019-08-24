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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import java.util.Random;

public class PathBlock extends Block
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    private final RegistryObject<Block> originalBlock;

    public PathBlock(Properties properties, RegistryObject<Block> originalBlock)
    {
        super(properties);
        this.originalBlock = originalBlock;
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random random)
    {
        world.setBlockState(pos, nudgeEntitiesWithNewState(state, this.originalBlock.orElse(this).getDefaultState(), world, pos));
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        if(facing == Direction.UP && !state.isValidPosition(world, currentPos))
        {
            world.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }

        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        BlockState blockstate = world.getBlockState(pos.up());
        return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock;
    }

    @Override
    public boolean func_220074_n(BlockState state)
    {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return !this.getDefaultState().isValidPosition(context.getWorld(), context.getPos()) ? Block.nudgeEntitiesWithNewState(this.getDefaultState(), Blocks.DIRT.getDefaultState(), context.getWorld(), context.getPos()) : super.getStateForPlacement(context);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }
}
