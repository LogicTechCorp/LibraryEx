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

import logictechcorp.libraryex.LibraryEx;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LibraryExFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, LibraryEx.MOD_ID);

    public static final RegistryObject<Feature<BiomeDataFeatureWrapper.Config>> BIOME_DATA_FEATURE_WRAPPER = FEATURES.register("biome_data_feature_wrapper", () -> new BiomeDataFeatureWrapper(BiomeDataFeatureWrapper.Config::deserialize));
    public static final RegistryObject<Feature<TriplePlantFeature.Config>> TRIPLE_PLANT = FEATURES.register("triple_plant", () -> new TriplePlantFeature(TriplePlantFeature.Config::deserialize));
    public static final RegistryObject<Feature<PoolFeature.Config>> POOL = FEATURES.register("pool", () -> new PoolFeature(PoolFeature.Config::deserialize));
    public static final RegistryObject<Feature<OreFeature.Config>> ORE = FEATURES.register("ore", () -> new OreFeature(OreFeature.Config::deserialize));
    public static final RegistryObject<Feature<ScatterFeature.Config>> SCATTER = FEATURES.register("scatter", () -> new ScatterFeature(ScatterFeature.Config::deserialize));
}
