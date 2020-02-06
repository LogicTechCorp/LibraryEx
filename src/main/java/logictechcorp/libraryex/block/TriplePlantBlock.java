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
import net.minecraft.block.BushBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;
import java.util.Random;

public class TriplePlantBlock extends BushBlock
{
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public TriplePlantBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(PART, Part.TOP));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PART);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState directionState, IWorld world, BlockPos pos, BlockPos directionPos)
    {
        if(direction == Direction.UP)
        {
            if(directionState.getBlock() == this)
            {
                world.setBlockState(pos, state.with(PART, Part.fromMeta(directionState.get(PART).ordinal() + 1)), 3);
            }
            else
            {
                if(world.getBlockState(pos.down()).getBlock() == Blocks.SOUL_SAND)
                {
                    world.setBlockState(pos, state.with(PART, Part.TOP), 3);
                }
                else
                {
                    world.setBlockState(pos, state.with(PART, Part.fromMeta(state.get(PART).ordinal() - 1)), 3);
                }
            }
        }

        return super.updatePostPlacement(state, direction, directionState, world, pos, directionPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        if(state.getBlock() == this && state.get(PART) != Part.TOP)
        {
            BlockPos groundPos = pos.down();
            return this.isValidGround(world.getBlockState(groundPos), world, groundPos);
        }
        else
        {
            if(world.getBlockState(pos.down()).getBlock() != this)
            {
                return super.isValidPosition(state, world, pos);
            }

            return world.getBlockState(pos.down(3)).getBlock() != this;
        }
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader world, BlockPos pos)
    {
        Block block = state.getBlock();
        return block == this || block == Blocks.SOUL_SAND;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        return pos.getY() < context.getWorld().getDimension().getHeight() - 2 && context.getWorld().getBlockState(pos.up()).isReplaceable(context) && context.getWorld().getBlockState(pos.up(2)).isReplaceable(context) ? super.getStateForPlacement(context) : null;
    }

    @Override
    public Block.OffsetType getOffsetType()
    {
        return Block.OffsetType.XZ;
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos)
    {
        return PlantType.Nether;
    }

    public void placeAt(IWorld world, Random random, BlockPos pos)
    {
        if(world.getBlockState(pos.down()).getBlock() != this)
        {
            int height = random.nextInt(3) + 1;

            if(height == 1)
            {
                world.setBlockState(pos, this.getDefaultState().with(PART, Part.TOP), 3);
            }
            else if(height == 2)
            {
                world.setBlockState(pos.up(), this.getDefaultState().with(PART, Part.TOP), 3);
                world.setBlockState(pos, this.getDefaultState().with(PART, Part.MIDDLE), 3);
            }
            else
            {
                world.setBlockState(pos.up(2), this.getDefaultState().with(PART, Part.TOP), 3);
                world.setBlockState(pos.up(), this.getDefaultState().with(PART, Part.MIDDLE), 3);
                world.setBlockState(pos, this.getDefaultState().with(PART, Part.BOTTOM), 3);
            }
        }
    }

    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity)
    {
        return PathNodeType.DANGER_CACTUS;
    }

    public enum Part implements IStringSerializable
    {
        TOP,
        MIDDLE,
        BOTTOM;

        @Override
        public String getName()
        {
            return this.toString().toLowerCase();
        }

        public static Part fromMeta(int meta)
        {
            if(meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
    }
}

