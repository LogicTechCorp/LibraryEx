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

package lex.world.biome;

import lex.config.IConfig;
import lex.world.gen.GenerationStage;
import lex.world.gen.feature.IFeature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;

import java.util.List;

public interface IBiomeWrapper
{
    Biome getBiome();

    int getWeight();

    IBlockState getBlock(String key, IBlockState fallbackValue);

    IBlockState getBlock(String key);

    List<IBlockState> getBlocks();

    List<Biome.SpawnListEntry> getSpawnListEntries(EnumCreatureType creatureType);

    List<IFeature> getFeatures(GenerationStage generationStage);

    IConfig getConfig();
}
