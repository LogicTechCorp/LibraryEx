/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.init;

import lex.world.biome.BiomeWrapperBuilder;
import lex.world.gen.feature.FeatureBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class LibExRegistries
{
    public static final IForgeRegistry<FeatureBuilder> FEATURE_BUILDERS = GameRegistry.findRegistry(FeatureBuilder.class);
    public static final IForgeRegistry<BiomeWrapperBuilder> BIOME_WRAPPER_BUILDERS = GameRegistry.findRegistry(BiomeWrapperBuilder.class);
}
