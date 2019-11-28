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
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class PoolFeature extends Feature<PoolFeature.Config>
{
    public PoolFeature(Function<Dynamic<?>, ? extends Config> configFactory)
    {
        super(configFactory);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, Config config)
    {
        while(pos.getY() > 5 && world.isAirBlock(pos))
        {
            pos = pos.down();
        }

        if(pos.down(4).getY() <= world.getSeaLevel())
        {
            return false;
        }
        else
        {
            pos = pos.down(4);
            ChunkPos chunkPos = new ChunkPos(pos);

            if(!world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(Feature.VILLAGE.getStructureName()).isEmpty())
            {
                return false;
            }
            else
            {
                boolean[] positions = new boolean[2048];
                int i = random.nextInt(4) + 4;

                for(int j = 0; j < i; j++)
                {
                    double d0 = random.nextDouble() * 6.0D + 3.0D;
                    double d1 = random.nextDouble() * 4.0D + 2.0D;
                    double d2 = random.nextDouble() * 6.0D + 3.0D;
                    double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                    double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                    double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                    for(int posX = 1; posX < 15; posX++)
                    {
                        for(int posZ = 1; posZ < 15; posZ++)
                        {
                            for(int posY = 1; posY < 7; posY++)
                            {
                                double d6 = ((double) posX - d3) / (d0 / 2.0D);
                                double d7 = ((double) posY - d4) / (d1 / 2.0D);
                                double d8 = ((double) posZ - d5) / (d2 / 2.0D);
                                double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                                if(d9 < 1.0D)
                                {
                                    positions[(posX * 16 + posZ) * 8 + posY] = true;
                                }
                            }
                        }
                    }
                }

                for(int posX = 0; posX < 16; posX++)
                {
                    for(int posZ = 0; posZ < 16; posZ++)
                    {
                        for(int posY = 0; posY < 8; posY++)
                        {
                            boolean flag = !positions[(posX * 16 + posZ) * 8 + posY] && (posX < 15 && positions[((posX + 1) * 16 + posZ) * 8 + posY] || posX > 0 && positions[((posX - 1) * 16 + posZ) * 8 + posY] || posZ < 15 && positions[(posX * 16 + posZ + 1) * 8 + posY] || posZ > 0 && positions[(posX * 16 + (posZ - 1)) * 8 + posY] || posY < 7 && positions[(posX * 16 + posZ) * 8 + posY + 1] || posY > 0 && positions[(posX * 16 + posZ) * 8 + (posY - 1)]);

                            if(flag)
                            {
                                Material material = world.getBlockState(pos.add(posX, posY, posZ)).getMaterial();

                                if(posY >= 4 && material.isLiquid())
                                {
                                    return false;
                                }
                                if(posY < 4 && !material.isSolid() && world.getBlockState(pos.add(posX, posY, posZ)) != config.getLiquidState())
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }

                for(int posX = 0; posX < 16; posX++)
                {
                    for(int posZ = 0; posZ < 16; posZ++)
                    {
                        for(int posY = 0; posY < 8; posY++)
                        {
                            if(positions[(posX * 16 + posZ) * 8 + posY])
                            {
                                world.setBlockState(pos.add(posX, posY, posZ), posY >= 4 ? Blocks.CAVE_AIR.getDefaultState() : config.getLiquidState(), 2);
                            }
                        }
                    }
                }

                for(int posX = 0; posX < 16; posX++)
                {
                    for(int posZ = 0; posZ < 16; posZ++)
                    {
                        for(int posY = 0; posY < 8; posY++)
                        {
                            boolean flag1 = !positions[(posX * 16 + posZ) * 8 + posY] && (posX < 15 && positions[((posX + 1) * 16 + posZ) * 8 + posY] || posX > 0 && positions[((posX - 1) * 16 + posZ) * 8 + posY] || posZ < 15 && positions[(posX * 16 + posZ + 1) * 8 + posY] || posZ > 0 && positions[(posX * 16 + (posZ - 1)) * 8 + posY] || posY < 7 && positions[(posX * 16 + posZ) * 8 + posY + 1] || posY > 0 && positions[(posX * 16 + posZ) * 8 + (posY - 1)]);

                            if(flag1 && (posY < 4 || random.nextInt(2) != 0) && world.getBlockState(pos.add(posX, posY, posZ)).getMaterial().isSolid())
                            {
                                world.setBlockState(pos.add(posX, posY, posZ), config.getSurroundingState(), 2);
                            }
                        }
                    }
                }

                return true;
            }
        }
    }

    public static class Config implements IFeatureConfig
    {
        private final BlockState liquidState;
        private final BlockState surroundingState;

        public Config(BlockState liquidState, BlockState surroundingState)
        {
            this.liquidState = liquidState;
            this.surroundingState = surroundingState;
        }

        @Override
        public <T> Dynamic<T> serialize(DynamicOps<T> ops)
        {
            Map<T, T> map = new HashMap<>();
            map.put(ops.createString("liquid_state"), BlockState.serialize(ops, this.liquidState).getValue());
            map.put(ops.createString("surrounding_state"), BlockState.serialize(ops, this.surroundingState).getValue());
            return new Dynamic<>(ops, ops.createMap(map));
        }

        public static <T> Config deserialize(Dynamic<T> dynamic)
        {
            BlockState liquidState = dynamic.get("liquid_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            BlockState surroundingState = dynamic.get("surrounding_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            return new Config(liquidState, surroundingState);
        }

        public BlockState getLiquidState()
        {
            return this.liquidState;
        }

        public BlockState getSurroundingState()
        {
            return this.surroundingState;
        }
    }
}
