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

package logictechcorp.libraryex.world.generation.feature;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.utility.ConfigHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class ConfigurableFeatureOre extends ConfigurableFeature
{
    private IBlockState blockToSpawn;
    private IBlockState blockToReplace;
    private int veinSize;

    public ConfigurableFeatureOre(Config config)
    {
        super(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToReplace = ConfigHelper.getBlockState(config, "blockToReplace");
        this.veinSize = config.getOrElse("veinSize", 8);
    }

    public ConfigurableFeatureOre(int generationAttempts, double generationProbability, boolean randomizeGenerationAttempts, int minGenerationHeight, int maxGenerationHeight, IBlockState blockToSpawn, IBlockState blockToReplace, int veinSize)
    {
        super(generationAttempts, generationProbability, randomizeGenerationAttempts, minGenerationHeight, maxGenerationHeight);
        this.blockToSpawn = blockToSpawn;
        this.blockToReplace = blockToReplace;
        this.veinSize = veinSize;
    }

    @Override
    public Config serialize()
    {
        Config config = super.serialize();
        config.add("veinSize", this.veinSize);
        ConfigHelper.setBlockState(config, "blockToReplace", this.blockToReplace);
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
        return config;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos pos)
    {
        if(this.blockToSpawn == null || this.blockToReplace == null)
        {
            return false;
        }

        float f = random.nextFloat() * (float) Math.PI;
        double d0 = (double) ((float) pos.getX() + MathHelper.sin(f) * (float) this.veinSize / 8.0F);
        double d1 = (double) ((float) pos.getX() - MathHelper.sin(f) * (float) this.veinSize / 8.0F);
        double d2 = (double) ((float) pos.getZ() + MathHelper.cos(f) * (float) this.veinSize / 8.0F);
        double d3 = (double) ((float) pos.getZ() - MathHelper.cos(f) * (float) this.veinSize / 8.0F);
        double d4 = (double) (pos.getY() + random.nextInt(3) - 2);
        double d5 = (double) (pos.getY() + random.nextInt(3) - 2);

        for(int i = 0; i < this.veinSize; ++i)
        {
            float f1 = (float) i / (float) this.veinSize;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = random.nextDouble() * (double) this.veinSize / 16.0D;
            double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for(int l1 = j; l1 <= i1; ++l1)
            {
                double d12 = ((double) l1 + 0.5D - d6) / (d10 / 2.0D);

                if(d12 * d12 < 1.0D)
                {
                    for(int i2 = k; i2 <= j1; ++i2)
                    {
                        double d13 = ((double) i2 + 0.5D - d7) / (d11 / 2.0D);

                        if(d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for(int j2 = l; j2 <= k1; ++j2)
                            {
                                double d14 = ((double) j2 + 0.5D - d8) / (d10 / 2.0D);

                                if(d12 * d12 + d13 * d13 + d14 * d14 < 1.0D)
                                {
                                    BlockPos newPos = new BlockPos(l1, i2, j2);
                                    IBlockState state = world.getBlockState(newPos);

                                    if(state.getBlock().isReplaceableOreGen(state, world, newPos, BlockMatcher.forBlock(this.blockToReplace.getBlock())) && state == this.blockToReplace)
                                    {
                                        world.setBlockState(newPos, this.blockToSpawn, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
