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

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class OreFeature extends Feature<OreFeature.Config>
{
    public OreFeature(Function<Dynamic<?>, ? extends Config> configFactory)
    {
        super(configFactory);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, OreFeature.Config config)
    {
        float angle = random.nextFloat() * (float) Math.PI;
        float multiplier = (float) config.getSize() / 8.0F;
        int offset = MathHelper.ceil(((float) config.getSize() / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d0 = ((float) pos.getX() + MathHelper.sin(angle) * multiplier);
        double d1 = ((float) pos.getX() - MathHelper.sin(angle) * multiplier);
        double d2 = ((float) pos.getZ() + MathHelper.cos(angle) * multiplier);
        double d3 = ((float) pos.getZ() - MathHelper.cos(angle) * multiplier);
        double d4 = (pos.getY() + random.nextInt(3) - 2);
        double d5 = (pos.getY() + random.nextInt(3) - 2);
        int maxX = pos.getX() - MathHelper.ceil(multiplier) - offset;
        int maxY = pos.getY() - 2 - offset;
        int maxZ = pos.getZ() - MathHelper.ceil(multiplier) - offset;
        int j1 = 2 * (MathHelper.ceil(multiplier) + offset);
        int k1 = 2 * (2 + offset);

        for(int posX = maxX; posX <= maxX + j1; posX++)
        {
            for(int posZ = maxZ; posZ <= maxZ + j1; posZ++)
            {
                if(maxY <= world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, posX, posZ))
                {
                    return this.spawnOre(world, random, config, d0, d1, d2, d3, d4, d5, maxX, maxY, maxZ, j1, k1);
                }
            }
        }

        return false;
    }

    private boolean spawnOre(IWorld world, Random random, OreFeature.Config config, double p_207803_4_, double p_207803_6_, double p_207803_8_, double p_207803_10_, double p_207803_12_, double p_207803_14_, int maxX, int maxY, int maxZ, int width, int height)
    {
        int placements = 0;
        BitSet bitset = new BitSet(width * height * width);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        double[] adouble = new double[config.getSize() * 4];

        for(int size = 0; size < config.getSize(); size++)
        {
            float f = (float) size / (float) config.getSize();
            double d0 = MathHelper.lerp(f, p_207803_4_, p_207803_6_);
            double d2 = MathHelper.lerp(f, p_207803_12_, p_207803_14_);
            double d4 = MathHelper.lerp(f, p_207803_8_, p_207803_10_);
            double d6 = random.nextDouble() * (double) config.getSize() / 16.0D;
            double d7 = ((double) (MathHelper.sin((float) Math.PI * f) + 1.0F) * d6 + 1.0D) / 2.0D;
            adouble[size * 4 + 0] = d0;
            adouble[size * 4 + 1] = d2;
            adouble[size * 4 + 2] = d4;
            adouble[size * 4 + 3] = d7;
        }

        for(int size = 0; size < config.getSize() - 1; size++)
        {
            if(!(adouble[size * 4 + 3] <= 0.0D))
            {
                for(int j3 = size + 1; j3 < config.getSize(); j3++)
                {
                    if(!(adouble[j3 * 4 + 3] <= 0.0D))
                    {
                        double d12 = adouble[size * 4 + 0] - adouble[j3 * 4 + 0];
                        double d13 = adouble[size * 4 + 1] - adouble[j3 * 4 + 1];
                        double d14 = adouble[size * 4 + 2] - adouble[j3 * 4 + 2];
                        double d15 = adouble[size * 4 + 3] - adouble[j3 * 4 + 3];
                        if(d15 * d15 > d12 * d12 + d13 * d13 + d14 * d14)
                        {
                            if(d15 > 0.0D)
                            {
                                adouble[j3 * 4 + 3] = -1.0D;
                            }
                            else
                            {
                                adouble[size * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for(int size = 0; size < config.getSize(); size++)
        {
            double d11 = adouble[size * 4 + 3];

            if(!(d11 < 0.0D))
            {
                double d1 = adouble[size * 4 + 0];
                double d3 = adouble[size * 4 + 1];
                double d5 = adouble[size * 4 + 2];
                int startX = Math.max(MathHelper.floor(d1 - d11), maxX);
                int startY = Math.max(MathHelper.floor(d3 - d11), maxY);
                int startZ = Math.max(MathHelper.floor(d5 - d11), maxZ);
                int sizeX = Math.max(MathHelper.floor(d1 + d11), startX);
                int sizeY = Math.max(MathHelper.floor(d3 + d11), startY);
                int sizeZ = Math.max(MathHelper.floor(d5 + d11), startZ);

                for(int posX = startX; posX <= sizeX; posX++)
                {
                    double d8 = ((double) posX + 0.5D - d1) / d11;

                    if(d8 * d8 < 1.0D)
                    {
                        for(int posY = startY; posY <= sizeY; posY++)
                        {
                            double d9 = ((double) posY + 0.5D - d3) / d11;

                            if(d8 * d8 + d9 * d9 < 1.0D)
                            {
                                for(int posZ = startZ; posZ <= sizeZ; posZ++)
                                {
                                    double d10 = ((double) posZ + 0.5D - d5) / d11;

                                    if(d8 * d8 + d9 * d9 + d10 * d10 < 1.0D)
                                    {
                                        int k2 = posX - maxX + (posY - maxY) * width + (posZ - maxZ) * width * height;

                                        if(!bitset.get(k2))
                                        {
                                            bitset.set(k2);
                                            mutablePos.setPos(posX, posY, posZ);

                                            if(config.getTargetState() == world.getBlockState(mutablePos))
                                            {
                                                world.setBlockState(mutablePos, config.getOreState(), 2);
                                                placements++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return placements > 0;
    }

    public static class Config implements IFeatureConfig
    {
        private final BlockState oreState;
        private final BlockState targetState;
        private final int size;

        public Config(BlockState oreState, BlockState targetState, int size)
        {
            this.oreState = oreState;
            this.targetState = targetState;
            this.size = size;
        }

        @Override
        public <T> Dynamic<T> serialize(DynamicOps<T> ops)
        {
            Map<T, T> map = new HashMap<>();
            map.put(ops.createString("ore_state"), BlockState.serialize(ops, this.oreState).getValue());
            map.put(ops.createString("target_state"), BlockState.serialize(ops, this.targetState).getValue());
            map.put(ops.createString("size"), ops.createInt(this.size));
            return new Dynamic<>(ops, ops.createMap(map));
        }

        public static <T> Config deserialize(Dynamic<T> dynamic)
        {
            BlockState oreState = dynamic.get("ore_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            BlockState targetState = dynamic.get("target_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            int size = dynamic.get("size").asInt(0);
            return new Config(oreState, targetState, size);
        }

        public BlockState getOreState()
        {
            return this.oreState;
        }

        public BlockState getTargetState()
        {
            return this.targetState;
        }

        public int getSize()
        {
            return this.size;
        }
    }
}
