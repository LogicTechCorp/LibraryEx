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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class ScatterFeature extends Feature<ScatterFeature.Config>
{
    public ScatterFeature(Function<Dynamic<?>, ? extends Config> configFactory)
    {
        super(configFactory);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, Config config)
    {
        BlockState scatterState = config.getScatterState();
        Config.TargetArea targetArea = config.getTargetArea();

        for(int i = 0; i < 64; i++)
        {
            BlockPos randomPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if(world.isAirBlock(randomPos))
            {
                BlockPos offsetPos = targetArea.offsetPos(randomPos);

                if((targetArea == Config.TargetArea.ON_GROUND || targetArea == Config.TargetArea.IN_GROUND) && world.getBlockState(randomPos.down()) == config.getTargetState())
                {
                    if(scatterState.isValidPosition(world, offsetPos))
                    {
                        world.setBlockState(offsetPos, scatterState, 2);
                    }
                }
                else if((targetArea == Config.TargetArea.ON_ROOF || targetArea == Config.TargetArea.IN_ROOF) && world.getBlockState(randomPos.up()) == config.getTargetState())
                {
                    if(scatterState.isValidPosition(world, offsetPos))
                    {
                        world.setBlockState(offsetPos, scatterState, 2);
                    }
                }
            }
        }

        return true;
    }

    public static class Config implements IFeatureConfig
    {
        private final BlockState scatterState;
        private final BlockState targetState;
        private final TargetArea targetArea;

        public Config(BlockState scatterState, BlockState targetState, TargetArea targetArea)
        {
            this.scatterState = scatterState;
            this.targetState = targetState;
            this.targetArea = targetArea;
        }

        @Override
        public <T> Dynamic<T> serialize(DynamicOps<T> ops)
        {
            Map<T, T> map = new HashMap<>();
            map.put(ops.createString("scatter_state"), BlockState.serialize(ops, this.scatterState).getValue());
            map.put(ops.createString("target_state"), BlockState.serialize(ops, this.targetState).getValue());
            map.put(ops.createString("target_area"), ops.createString(this.targetArea.toString()));
            return new Dynamic<>(ops, ops.createMap(map));
        }

        public static <T> Config deserialize(Dynamic<T> dynamic)
        {
            BlockState scatterState = dynamic.get("scatter_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            BlockState targetState = dynamic.get("target_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            TargetArea targetArea = dynamic.get("target_area").map(TargetArea::deserialize).orElse(TargetArea.ON_GROUND);
            return new Config(scatterState, targetState, targetArea);
        }

        public BlockState getScatterState()
        {
            return this.scatterState;
        }

        public BlockState getTargetState()
        {
            return this.targetState;
        }

        public TargetArea getTargetArea()
        {
            return this.targetArea;
        }

        private enum TargetArea
        {
            ON_GROUND(null),
            IN_GROUND(Direction.DOWN),
            ON_ROOF(null),
            IN_ROOF(Direction.UP);

            Direction offset;

            TargetArea(Direction offset)
            {
                this.offset = offset;
            }

            public static <T> TargetArea deserialize(Dynamic<T> dynamic)
            {
                for(TargetArea area : TargetArea.values())
                {
                    if(dynamic.get("target_area").asString().orElse("").equals(area.toString()))
                    {
                        return area;
                    }
                }

                return TargetArea.ON_GROUND;
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

            public Direction getOffset()
            {
                return this.offset;
            }

            @Override
            public String toString()
            {
                return this.name().toLowerCase();
            }
        }
    }
}
