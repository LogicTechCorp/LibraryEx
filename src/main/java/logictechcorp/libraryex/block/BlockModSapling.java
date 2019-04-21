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

import logictechcorp.libraryex.block.builder.BlockProperties;
import logictechcorp.libraryex.utility.BlockHelper;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTrait;
import logictechcorp.libraryex.world.generation.trait.impl.BiomeTraitBasicTree;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public abstract class BlockModSapling extends BlockModBush implements IGrowable
{
    public static final PropertyBool GROW = PropertyBool.create("grow");
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    public BlockModSapling(ResourceLocation registryName, BlockProperties properties)
    {
        super(registryName, properties);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROW, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SAPLING_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if(!world.isRemote)
        {
            super.updateTick(world, pos, state, random);

            if(!world.isAreaLoaded(pos, 1))
            {
                return;
            }
            if(world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0)
            {
                this.grow(world, random, pos, state);
            }
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Plains;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        Block block = state.getBlock();
        return BlockHelper.isOreDict(block, "grass") || BlockHelper.isOreDict(block, "dirt");
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos pos, IBlockState state)
    {
        if(!state.getValue(GROW))
        {
            world.setBlockState(pos, state.cycleProperty(GROW), 4);
        }
        else
        {
            this.generateTree(world, random, pos, state);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(GROW, meta != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(GROW) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GROW);
    }

    protected void generateTree(World world, Random random, BlockPos pos, IBlockState state)
    {
        if(!TerrainGen.saplingGrowTree(world, random, pos))
        {
            return;
        }

        IBiomeTrait treeTrait = new BiomeTraitBasicTree(1, false, 1.0F, pos.getY(), pos.up(8).getY(), this, this.getLog(), this.getLeaf().withProperty(BlockModLeaf.DECAY, false), 4, 6);

        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);

        if(!treeTrait.generate(world, pos, random))
        {
            world.setBlockState(pos, state, 4);
        }
    }

    protected abstract IBlockState getLog();

    protected abstract IBlockState getLeaf();
}
