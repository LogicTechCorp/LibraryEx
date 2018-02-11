/*
 * LibEx
 * Copyright (c) 2017-2018 by MineEx
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

package lex.api.world.biome;

import lex.api.world.gen.feature.IFeature;
import lex.world.gen.GenerationStage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;

import java.util.List;

public interface IBiomeWrapper
{
    Biome getBiome();

    IBlockState getBlock(String key, IBlockState fallbackValue);

    IBlockState getBlock(String key);

    List<IBlockState> getBlocks();

    List<IFeature> getFeatures(GenerationStage generationStage);

    List<Biome.SpawnListEntry> getSpawnableMobs(EnumCreatureType creatureType);

    boolean isEnabled();
}
