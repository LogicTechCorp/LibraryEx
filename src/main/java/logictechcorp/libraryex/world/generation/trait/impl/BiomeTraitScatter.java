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

package logictechcorp.libraryex.world.generation.trait.impl;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.utility.ConfigHelper;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BiomeTraitScatter extends BiomeTraitConfigurable
{
    private IBlockState blockToSpawn;
    private IBlockState blockToTarget;
    private Placement placement;

    public BiomeTraitScatter(int generationAttempts, boolean randomizeGenerationAttempts, double generationProbability, int minimumGenerationHeight, int maximumGenerationHeight, IBlockState blockToSpawn, IBlockState blockToTarget, Placement placement)
    {
        super(generationAttempts, randomizeGenerationAttempts, generationProbability, minimumGenerationHeight, maximumGenerationHeight);
        this.blockToSpawn = blockToSpawn;
        this.blockToTarget = blockToTarget;
        this.placement = placement;
    }

    private BiomeTraitScatter(Builder builder)
    {
        super(builder);
        this.blockToSpawn = builder.blockToSpawn;
        this.blockToTarget = builder.blockToTarget;
        this.placement = builder.placement;
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToTarget = ConfigHelper.getBlockState(config, "blockToTarget");
        this.placement = config.getEnumOrElse("placement", Placement.ON_GROUND);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        config.add("placement", this.placement == null ? null : this.placement.toString().toLowerCase());
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.blockToSpawn == null || this.blockToTarget == null || this.placement == null)
        {
            return false;
        }

        for(int i = 0; i < 64; i++)
        {
            BlockPos newPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if(world.isAirBlock(newPos) && world.getBlockState(newPos.down()) == this.blockToTarget)
            {
                if(this.blockToSpawn instanceof BlockBush)
                {
                    if(((BlockBush) this.blockToSpawn).canBlockStay(world, this.placement.offsetPos(pos), this.blockToSpawn))
                    {
                        world.setBlockState(this.placement.offsetPos(newPos), this.blockToSpawn, 2);
                    }
                }
                else
                {
                    world.setBlockState(this.placement.offsetPos(newPos), this.blockToSpawn, 2);
                }
            }
        }

        return true;
    }

    public enum Placement
    {
        ON_GROUND(null),
        IN_GROUND(EnumFacing.DOWN);

        EnumFacing offset;

        Placement(EnumFacing offsetIn)
        {
            this.offset = offsetIn;
        }

        public BlockPos offsetPos(BlockPos pos)
        {
            if(this.offset != null)
            {
                return pos.offset(this.offset);
            }
            else
            {
                return pos;
            }
        }
    }

    public static class Builder extends BiomeTrait.Builder
    {
        private IBlockState blockToSpawn;
        private IBlockState blockToTarget;
        private Placement placement;

        public Builder()
        {
            this.blockToSpawn = Blocks.TALLGRASS.getDefaultState();
            this.blockToTarget = Blocks.GRASS.getDefaultState();
            this.placement = Placement.ON_GROUND;
        }

        public Builder blockToSpawn(IBlockState blockToSpawn)
        {
            this.blockToSpawn = blockToSpawn;
            return this;
        }

        public Builder blockToTarget(IBlockState blockToTarget)
        {
            this.blockToTarget = blockToTarget;
            return this;
        }

        public Builder placement(Placement placement)
        {
            this.placement = placement;
            return this;
        }

        @Override
        public BiomeTrait create()
        {
            return new BiomeTraitScatter(this);
        }
    }
}
