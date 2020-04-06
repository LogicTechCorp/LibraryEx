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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FeatureWrapper extends Feature<FeatureWrapper.Config>
{
    public FeatureWrapper(Function<Dynamic<?>, ? extends Config> configFactory)
    {
        super(configFactory);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, Config config)
    {
        for(ConfiguredFeature<?, ?> feature : config.getFeatures())
        {
            feature.place(world, generator, random, pos);
        }

        return true;
    }

    public static class Config implements IFeatureConfig
    {
        private final List<ConfiguredFeature<?, ?>> features;

        public Config(List<ConfiguredFeature<?, ?>> features)
        {
            this.features = features;
        }

        @Override
        public <T> Dynamic<T> serialize(DynamicOps<T> ops)
        {
            return new Dynamic<>(ops);
        }

        public static <T> Config deserialize(Dynamic<T> dynamic)
        {
            return new Config(new ArrayList<>());
        }

        public List<ConfiguredFeature<?, ?>> getFeatures()
        {
            return this.features;
        }
    }
}
