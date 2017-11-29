/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.world.gen.feature;

import lex.config.IConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FeatureFluid extends AbstractFeature
{
    protected IBlockState blockToSpawn;
    protected IBlockState blockToTarget;
    protected boolean hidden;

    FeatureFluid(Builder builder)
    {
        super(builder);
        blockToSpawn = builder.blockToSpawn;
        blockToTarget = builder.blockToTarget;
        hidden = builder.hidden;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        if(world.getBlockState(pos.up()) != blockToTarget)
        {
            return false;
        }
        else if(!world.isAirBlock(pos) && world.getBlockState(pos) != blockToTarget)
        {
            return false;
        }
        else
        {
            int i = 0;

            if(world.getBlockState(pos.west()) == blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.east()) == blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.north()) == blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.south()) == blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.down()) == blockToTarget)
            {
                i++;
            }

            int j = 0;

            if(world.isAirBlock(pos.west()))
            {
                j++;
            }

            if(world.isAirBlock(pos.east()))
            {
                j++;
            }

            if(world.isAirBlock(pos.north()))
            {
                j++;
            }

            if(world.isAirBlock(pos.south()))
            {
                j++;
            }

            if(world.isAirBlock(pos.down()))
            {
                j++;
            }

            if(!hidden && i == 4 && j == 1 || i == 5)
            {
                world.setBlockState(pos, blockToSpawn, 2);
                world.immediateBlockTick(pos, blockToSpawn, rand);
            }

            return true;
        }
    }

    public static class Builder extends AbstractBuilder<Builder, FeatureFluid>
    {
        protected IBlockState blockToSpawn;
        protected IBlockState blockToTarget;
        protected boolean hidden;

        @Override
        public Builder configure(IConfig config)
        {
            super.configure(config);
            blockToSpawn = config.getBlock("blockToSpawn", Blocks.STONE.getDefaultState());
            blockToTarget = config.getBlock("blockToTarget", Blocks.AIR.getDefaultState());
            hidden = config.getBoolean("hidden", false);
            return this;
        }

        @Override
        public FeatureFluid create()
        {
            return new FeatureFluid(this);
        }
    }
}
