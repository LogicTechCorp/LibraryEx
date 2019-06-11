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
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockModLog extends BlockMod
{
    public static final PropertyEnum<EnumAxis> AXIS = PropertyEnum.create("axis", EnumAxis.class);

    public BlockModLog(ResourceLocation registryName, BlockProperties properties)
    {
        super(registryName, properties);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumAxis.Y));
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isWood(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getStateFromMeta(meta).withProperty(AXIS, EnumAxis.fromAxis(facing.getAxis()));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if(world.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5)))
        {
            for(BlockPos blockPos : BlockPos.getAllInBox(pos.add(-4, -4, -4), pos.add(4, 4, 4)))
            {
                IBlockState testState = world.getBlockState(blockPos);

                if(testState.getBlock().isLeaves(testState, world, blockPos))
                {
                    testState.getBlock().beginLeavesDecay(testState, world, blockPos);
                }
            }
        }
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AXIS).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AXIS, EnumAxis.fromMeta(0));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch(rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch(state.getValue(AXIS))
                {
                    case X:
                        return state.withProperty(AXIS, EnumAxis.Z);
                    case Z:
                        return state.withProperty(AXIS, EnumAxis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }

    public enum EnumAxis implements IStringSerializable
    {
        X,
        Y,
        Z;

        @Override
        public String getName()
        {
            return this.toString().toLowerCase();
        }

        public static EnumAxis fromMeta(int meta)
        {
            if(meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }

        public static EnumAxis fromAxis(EnumFacing.Axis axis)
        {
            switch(axis)
            {
                case X:
                    return X;
                case Y:
                    return Y;
                case Z:
                    return Z;
                default:
                    return Y;
            }
        }
    }
}
