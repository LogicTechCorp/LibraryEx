/*
 * Enhanced Portals
 * Copyright (c) by Alz454
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
 *
 * Original: https://github.com/enhancedportals/enhancedportals/blob/1647357d3cbed1289a653347e2107d92a2875a65/src/main/java/enhanced/portals/portal/PortalUtils.java
 * (Edited to fit in a single block class and to work as a single block)
 */

package logictechcorp.libraryex.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public abstract class ModPortalBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);

    protected static final VoxelShape X_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Y_SHAPE = Block.makeCuboidShape(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    protected static final VoxelShape Z_SHAPE = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public ModPortalBlock(Block.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random)
    {
        if(random.nextInt(100) == 0)
        {
            world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }

        for(int i = 0; i < 4; i++)
        {
            double posX = (double) ((float) pos.getX() + random.nextFloat());
            double posY = (double) ((float) pos.getY() + random.nextFloat());
            double posZ = (double) ((float) pos.getZ() + random.nextFloat());
            double speedX = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double speedY = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double speedZ = ((double) random.nextFloat() - 0.5D) * 0.5D;
            int multiplier = random.nextInt(2) * 2 - 1;

            if(world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this)
            {
                posX = (double) pos.getX() + 0.5D + 0.25D * (double) multiplier;
                speedX = (double) (random.nextFloat() * 2.0F * (float) multiplier);
            }
            else
            {
                posZ = (double) pos.getZ() + 0.5D + 0.25D * (double) multiplier;
                speedZ = (double) (random.nextFloat() * 2.0F * (float) multiplier);
            }

            world.addParticle(ParticleTypes.PORTAL, posX, posY, posZ, speedX, speedY, speedZ);
        }
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random random)
    {
        Direction.Axis axis = state.get(AXIS);

        if(axis == Direction.Axis.X)
        {
            if(world.isAirBlock(pos.down()) || world.isAirBlock(pos.up()) || world.isAirBlock(pos.north()) || world.isAirBlock(pos.south()))
            {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        else if(axis == Direction.Axis.Y)
        {
            if(world.isAirBlock(pos.north()) || world.isAirBlock(pos.south()) || world.isAirBlock(pos.west()) || world.isAirBlock(pos.east()))
            {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        else if(axis == Direction.Axis.Z)
        {
            if(world.isAirBlock(pos.down()) || world.isAirBlock(pos.up()) || world.isAirBlock(pos.west()) || world.isAirBlock(pos.east()))
            {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState newState, boolean bool)
    {
        world.getPendingBlockTicks().scheduleTick(pos, this, 2);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos fromPos)
    {
        Direction.Axis axis = state.get(AXIS);

        if(axis == Direction.Axis.X)
        {
            if(pos.west().equals(fromPos) || pos.east().equals(fromPos))
            {
                if(world.getBlockState(fromPos).getBlock() == this)
                {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }
        else if(axis == Direction.Axis.Y)
        {
            if(pos.down().equals(fromPos) || pos.up().equals(fromPos))
            {
                if(world.getBlockState(fromPos).getBlock() == this)
                {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }
        else if(axis == Direction.Axis.Z)
        {
            if(pos.north().equals(fromPos) || pos.south().equals(fromPos))
            {
                if(world.getBlockState(fromPos).getBlock() == this)
                {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }

        return super.updatePostPlacement(state, facing, facingState, world, pos, fromPos);
    }

    @Override
    public abstract void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity);

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        switch(rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch(state.get(AXIS))
                {
                    case X:
                        return state.with(AXIS, Direction.Axis.Z);
                    case Y:
                        return state.with(AXIS, Direction.Axis.Y);
                    case Z:
                        return state.with(AXIS, Direction.Axis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        switch(state.get(AXIS))
        {
            case X:
                return X_SHAPE;
            case Y:
            default:
                return Y_SHAPE;
            case Z:
                return Z_SHAPE;
        }
    }

    public static int getMetaForAxis(Direction.Axis axis)
    {
        return axis == Direction.Axis.X ? 0 : axis == Direction.Axis.Y ? 1 : 2;
    }

    public abstract boolean isPortalIgniter(World world, BlockPos pos);

    public abstract boolean isPortalPart(World world, BlockPos pos);

    private void addNeighborBlocks(BlockPos pos, Direction.Axis axis, Queue<BlockPos> neighbors)
    {
        for(Direction direction : Direction.values())
        {
            if(axis == Direction.Axis.X && (direction == Direction.EAST || direction == Direction.WEST))
            {
                continue;
            }
            else if(axis == Direction.Axis.Y && (direction == Direction.DOWN || direction == Direction.UP))
            {
                continue;
            }
            else if(axis == Direction.Axis.Z && (direction == Direction.NORTH || direction == Direction.SOUTH))
            {
                continue;
            }

            neighbors.add(pos.offset(direction));
        }
    }

    public boolean trySpawnPortal(World world, BlockPos pos)
    {
        for(Direction.Axis axis : Direction.Axis.values())
        {
            for(Direction direction : Direction.values())
            {
                Queue<BlockPos> portalBlocks = this.getPortalBlocks(world, pos.offset(direction), axis);

                if(portalBlocks.size() > 0)
                {
                    for(BlockPos newPos : portalBlocks)
                    {
                        world.setBlockState(newPos, this.getDefaultState().with(AXIS, axis));
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private Queue<BlockPos> getPortalBlocks(World world, BlockPos pos, Direction.Axis axis)
    {
        Queue<BlockPos> portalBlocks = new ArrayDeque<>();
        Queue<BlockPos> toProcess = new ArrayDeque<>();
        int chances = 0;

        toProcess.add(pos);

        while(!toProcess.isEmpty())
        {
            BlockPos newPos = toProcess.remove();

            if(!portalBlocks.contains(newPos))
            {
                if(world.isAirBlock(newPos) || this.isPortalIgniter(world, newPos))
                {
                    int neighborBlocks = this.getNeighborBlocks(world, newPos, portalBlocks, axis);

                    if(neighborBlocks < 2)
                    {
                        if(chances < 40)
                        {
                            chances++;
                            neighborBlocks += 2;
                        }
                        else
                        {
                            return new ArrayDeque<>();
                        }
                    }
                    if(neighborBlocks >= 2)
                    {
                        portalBlocks.add(newPos);
                        this.addNeighborBlocks(newPos, axis, toProcess);
                    }
                    else if(!this.isPortalPart(world, newPos))
                    {
                        return new ArrayDeque<>();
                    }
                }
                else if(!this.isPortalPart(world, newPos))
                {
                    return new ArrayDeque<>();
                }
            }
        }

        return portalBlocks;
    }

    private int getNeighborBlocks(World world, BlockPos pos, Queue<BlockPos> portalBlocks, Direction.Axis axis)
    {
        int sides = 0;
        Queue<BlockPos> neighbors = new ArrayDeque<>();

        this.addNeighborBlocks(pos, axis, neighbors);

        for(BlockPos newPos : neighbors)
        {
            if(portalBlocks.contains(newPos) || this.isPortalPart(world, newPos))
            {
                sides++;
            }
        }

        return sides;
    }
}
