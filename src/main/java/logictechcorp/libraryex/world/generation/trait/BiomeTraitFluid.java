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

package logictechcorp.libraryex.world.generation.trait;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.utility.ConfigHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BiomeTraitFluid extends BiomeTraitConfigurable
{
    private IBlockState blockToSpawn;
    private IBlockState blockToTarget;
    private boolean hidden;

    public BiomeTraitFluid(int generationAttempts, boolean randomizeGenerationAttempts, double generationProbability, int minimumGenerationHeight, int maximumGenerationHeight, IBlockState blockToSpawn, IBlockState blockToTarget, boolean hidden)
    {
        super(generationAttempts, randomizeGenerationAttempts, generationProbability, minimumGenerationHeight, maximumGenerationHeight);
        this.blockToSpawn = blockToSpawn;
        this.blockToTarget = blockToTarget;
        this.hidden = hidden;
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToTarget = ConfigHelper.getBlockState(config, "blockToTarget");
        this.hidden = config.getOrElse("hidden", true);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        config.add("hidden", this.hidden);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
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
                world.immediateBlockTick(pos, this.blockToSpawn, random);
            }

            return true;
        }
    }

    @Override
    public IBiomeTraitConfigurable create()
    {
        return null;
    }
}
