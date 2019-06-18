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

package logictechcorp.libraryex.api.world.biome.data;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.api.world.biome.IBiomeBlock;
import logictechcorp.libraryex.api.world.generation.IGeneratorStage;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTrait;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;

public interface IBiomeData
{
    /**
     * Called when the server is starting to configure this biome data.
     *
     * @param config The config that belongs to the biome data.
     */
    void readFromConfig(IBiomeDataAPI biomeDataAPI, Config config);

    /**
     * Called when the server is stopping to save this biome data.
     *
     * @param config The config that belongs to the biome data.
     */
    void writeToConfig(Config config);

    /**
     * Returns true if this data represents a sub biome.
     *
     * @return True if this data represents a sub biome.
     */
    boolean isSubBiomeData();

    /**
     * Called to check if the associated biome should generate.
     *
     * @return Whether the associated biome should generate.
     */
    boolean generateBiome();

    /**
     * Called to check if the associated biome's default biomeTraits should generate.
     *
     * @return Whether the associated biome's default biomeTraits should generate.
     */
    boolean generateDefaultBiomeFeatures();

    /**
     * Called to get the biome associated with this data.
     *
     * @return The biome associated with this data.
     */
    Biome getBiome();

    /**
     * Called to get the generation biomeGenerationWeight of the associated biome.
     *
     * @return The generation biomeGenerationWeight of the associated biome.
     */
    int getBiomeGenerationWeight();

    /**
     * Called to get a block that makes up the associated biome.
     *
     * @param type     The type of block to get.
     * @param fallback The block to fallback to if the biome doesn't have a block for the type.
     * @return A block that makes up the associated biome.
     */
    BlockState getBiomeBlock(IBiomeBlock type, BlockState fallback);

    /**
     * Called to get a map containing the biome blocks and their identifiers.
     *
     * @return A map containing the biome blocks and their identifiers.
     */
    Map<String, BlockState> getBiomeBlocks();

    /**
     * Called to get a list of entities that spawn in the associated biome.
     *
     * @param creatureType The type of entity to get the list for.
     * @return A list of entities that spawn in the associated biome.
     */
    List<Biome.SpawnListEntry> getBiomeEntities(EntityClassification creatureType);

    /**
     * Called to get a list of biome traits that generate in the associated biome.
     *
     * @param generationStage The stage to get the list for.
     * @return A list of biome traits that generate in the associated biome.
     */
    List<IBiomeTrait> getBiomeTraits(IGeneratorStage generationStage);

    /**
     * Called to get a list of sub biomes that can generate in the associated biome.
     *
     * @return A list of sub biomes that can generate in the associated biome.
     */
    List<IBiomeData> getSubBiomeData();

    /**
     * Called to get the biome data config's relative save file.
     *
     * @return The biome data config's relative save file.
     */
    String getRelativeSaveFile();
}
