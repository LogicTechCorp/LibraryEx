/*
 * LibEx
 * Copyright (c) 2017-2018 by MineEx
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

import com.electronwill.nightconfig.core.Config;
import lex.util.ConfigHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FeatureFluid extends Feature
{
    private IBlockState blockToSpawn;
    private IBlockState blockToTarget;
    private boolean hidden;

    public FeatureFluid(Config config)
    {
        super(config);
        this.blockToSpawn = ConfigHelper.getOrSetBlockState(config, "blockToSpawn", null);
        this.blockToTarget = ConfigHelper.getOrSetBlockState(config, "blockToTarget", null);
        this.hidden = ConfigHelper.getOrSet(config, "hidden", true);
    }

    public FeatureFluid(int genAttempts, double genProbability, boolean randomizeGenAttempts, int minGenHeight, int maxGenHeight, IBlockState blockToSpawn, IBlockState blockToTarget, boolean hidden)
    {
        super(genAttempts, genProbability, randomizeGenAttempts, minGenHeight, maxGenHeight);
        this.blockToSpawn = blockToSpawn;
        this.blockToTarget = blockToTarget;
        this.hidden = hidden;
    }

    @Override
    public Config serialize()
    {
        Config config = super.serialize();
        config.add("hidden", this.hidden);
        ConfigHelper.getOrSetBlockState(config, "blockToTarget", this.blockToTarget);
        ConfigHelper.getOrSetBlockState(config, "blockToSpawn", this.blockToSpawn);
        return config;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        if(this.blockToSpawn == null || this.blockToTarget == null)
        {
            return false;
        }

        if(world.getBlockState(pos.up()) != this.blockToTarget)
        {
            return false;
        }
        else if(!world.isAirBlock(pos) && world.getBlockState(pos) != this.blockToTarget)
        {
            return false;
        }
        else
        {
            int i = 0;

            if(world.getBlockState(pos.west()) == this.blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.east()) == this.blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.north()) == this.blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.south()) == this.blockToTarget)
            {
                i++;
            }

            if(world.getBlockState(pos.down()) == this.blockToTarget)
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

            if(!this.hidden && i == 4 && j == 1 || i == 5)
            {
                world.setBlockState(pos, this.blockToSpawn, 2);
                world.immediateBlockTick(pos, this.blockToSpawn, rand);
            }

            return true;
        }
    }
}
