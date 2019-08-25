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
import logictechcorp.libraryex.block.TriplePlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

public class TriplePlantFeature extends Feature<TriplePlantFeature.Config>
{
    public TriplePlantFeature(Function<Dynamic<?>, ? extends Config> configFactory)
    {
        super(configFactory);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, Config config)
    {
        boolean flag = false;

        for(int i = 0; i < 64; i++)
        {
            BlockPos randomPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if(world.isAirBlock(randomPos) && randomPos.getY() < world.getWorld().getDimension().getHeight() - 2 && config.state.isValidPosition(world, randomPos))
            {
                ((TriplePlantBlock) config.state.getBlock()).placeAt(world, random, randomPos);
                flag = true;
            }
        }

        return flag;
    }

    public static class Config implements IFeatureConfig
    {
        public final BlockState state;

        public Config(BlockState state)
        {
            this.state = state;
        }

        @Override
        public <T> Dynamic<T> serialize(DynamicOps<T> ops)
        {
            return new Dynamic<>(ops, ops.createMap(Collections.singletonMap(ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
        }

        public static <T> Config deserialize(Dynamic<T> dynamic)
        {
            BlockState blockstate = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
            return new Config(blockstate);
        }
    }
}
