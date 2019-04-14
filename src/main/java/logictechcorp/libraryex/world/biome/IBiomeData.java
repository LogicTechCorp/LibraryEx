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

package logictechcorp.libraryex.world.biome;

import logictechcorp.libraryex.world.generation.GenerationStage;
import logictechcorp.libraryex.world.generation.feature.FeatureMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;

public interface IBiomeData
{
    Biome getBiome();

    int getWeight();

    boolean isEnabled();

    boolean generateDefaultFeatures();

    IBlockState getBiomeBlock(BlockType type, IBlockState fallback);

    Map<String, IBlockState> getBlocks();

    List<Biome.SpawnListEntry> getEntities(EnumCreatureType creatureType);

    List<FeatureMod> getFeatures(GenerationStage generationStage);

    enum BlockType
    {
        FLOOR_TOP_BLOCK("floorTopBlock"),
        FLOOR_FILLER_BLOCK("floorFillerBlock"),
        WALL_BLOCK("wallBlock"),
        CEILING_FILLER_BLOCK("ceilingFillerBlock"),
        CEILING_BOTTOM_BLOCK("ceilingBottomBlock"),
        OCEAN_BLOCK("oceanBlock");

        private String identifier;

        BlockType(String identifier)
        {
            this.identifier = identifier;
        }

        public String getIdentifier()
        {
            return this.identifier;
        }
    }
}
