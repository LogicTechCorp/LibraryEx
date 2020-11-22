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
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Consumer;

public class BiomeTraitOre extends BiomeTrait
{
    protected IBlockState blockToSpawn;
    protected IBlockState blockToReplace;
    protected int veinSize;

    protected BiomeTraitOre(Builder builder)
    {
        super(builder);
        this.blockToSpawn = builder.blockToSpawn;
        this.blockToReplace = builder.blockToReplace;
        this.veinSize = builder.veinSize;
    }

    public static BiomeTraitOre create(Consumer<Builder> consumer)
    {
        Builder builder = new Builder();
        consumer.accept(builder);
        return builder.create();
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToReplace = ConfigHelper.getBlockState(config, "blockToReplace");
        this.veinSize = config.getOrElse("veinSize", 8);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        config.add("veinSize", this.veinSize);
        ConfigHelper.setBlockState(config, "blockToReplace", this.blockToReplace);
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.blockToSpawn == null || this.blockToReplace == null)
        {
            return false;
        }

        float f = random.nextFloat() * (float) Math.PI;
        double d0 = ((float) pos.getX() + MathHelper.sin(f) * (float) this.veinSize / 8.0F);
        double d1 = ((float) pos.getX() - MathHelper.sin(f) * (float) this.veinSize / 8.0F);
        double d2 = ((float) pos.getZ() + MathHelper.cos(f) * (float) this.veinSize / 8.0F);
        double d3 = ((float) pos.getZ() - MathHelper.cos(f) * (float) this.veinSize / 8.0F);
        double d4 = (pos.getY() + random.nextInt(3) - 2);
        double d5 = (pos.getY() + random.nextInt(3) - 2);

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

    public static class Builder extends BiomeTrait.Builder<BiomeTraitOre>
    {
        private IBlockState blockToSpawn;
        private IBlockState blockToReplace;
        private int veinSize;

        public Builder()
        {
            this.blockToSpawn = Blocks.COAL_ORE.getDefaultState();
            this.blockToReplace = Blocks.STONE.getDefaultState();
            this.veinSize = 7;
        }

        public Builder blockToSpawn(IBlockState blockToSpawn)
        {
            this.blockToSpawn = blockToSpawn;
            return this;
        }

        public Builder blockToReplace(IBlockState blockToReplace)
        {
            this.blockToReplace = blockToReplace;
            return this;
        }

        public Builder veinSize(int veinSize)
        {
            this.veinSize = veinSize;
            return this;
        }

        @Override
        public BiomeTraitOre create()
        {
            return new BiomeTraitOre(this);
        }
    }
}
