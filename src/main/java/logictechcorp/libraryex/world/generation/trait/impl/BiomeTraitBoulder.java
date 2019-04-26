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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BiomeTraitBoulder extends BiomeTraitConfigurable
{
    private IBlockState blockToSpawn;
    private IBlockState blockToTarget;
    private int radius;

    public BiomeTraitBoulder(int generationAttempts, boolean randomizeGenerationAttempts, double generationProbability, int minimumGenerationHeight, int maximumGenerationHeight, IBlockState blockToSpawn, IBlockState blockToTarget, int radius)
    {
        super(generationAttempts, randomizeGenerationAttempts, generationProbability, minimumGenerationHeight, maximumGenerationHeight);
        this.blockToSpawn = blockToSpawn;
        this.blockToTarget = blockToTarget;
        this.radius = radius;
    }

    public BiomeTraitBoulder(Builder builder)
    {
        super(builder);
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToTarget = ConfigHelper.getBlockState(config, "blockToTarget");
        this.radius = config.getOrElse("radius", 4);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
        config.add("radius", this.radius);
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.blockToSpawn == null || this.blockToTarget == null)
        {
            return false;
        }

        while(true)
        {
            airCheckLabel:
            {
                if(pos.getY() > 3)
                {
                    if(world.isAirBlock(pos.down()))
                    {
                        break airCheckLabel;
                    }

                    IBlockState state = world.getBlockState(pos.down());

                    if(this.blockToTarget != state)
                    {
                        break airCheckLabel;
                    }
                }

                if(pos.getY() <= 3)
                {
                    return false;
                }

                for(int i = 0; this.radius >= 0 && i < 3; i++)
                {
                    int posX = this.radius + random.nextInt(2);
                    int posY = this.radius + random.nextInt(2);
                    int posZ = this.radius + random.nextInt(2);
                    float distance = (float) (posX + posY + posZ) * 0.333F + 0.5F;

                    for(BlockPos posLocal : BlockPos.getAllInBox(pos.add(-posX, -posY, -posZ), pos.add(posX, posY, posZ)))
                    {
                        if(posLocal.distanceSq(pos) <= (double) (distance * distance))
                        {
                            world.setBlockState(posLocal, this.blockToSpawn, 4);
                        }
                    }

                    pos = pos.add(-(this.radius + 1) + random.nextInt(2 + this.radius * 2), 0 - random.nextInt(2), -(this.radius + 1) + random.nextInt(2 + this.radius * 2));
                }

                return true;
            }

            pos = pos.down();
        }
    }

    public static class Builder extends BiomeTrait.Builder
    {
        private IBlockState blockToSpawn;
        private IBlockState blockToTarget;
        private int radius;

        public Builder()
        {
            this.blockToSpawn = Blocks.MOSSY_COBBLESTONE.getDefaultState();
            this.blockToTarget = Blocks.GRASS.getDefaultState();
            this.radius = 4;
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

        public Builder radius(int radius)
        {
            this.radius = radius;
            return this;
        }

        @Override
        public BiomeTrait create()
        {
            return new BiomeTraitBoulder(this);
        }
    }
}
