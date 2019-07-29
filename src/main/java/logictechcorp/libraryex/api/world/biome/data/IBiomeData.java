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
import logictechcorp.libraryex.api.world.biome.BiomeBlockType;
import logictechcorp.libraryex.api.world.generation.GenerationStage;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTrait;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;

public interface IBiomeData
{
    /**
     * Called to add a biome block to the biome data.
     *
     * @param blockType The biome block type for the block that is to be added.
     * @param state     The block that is to be added.
     */
    void addBiomeBlock(BiomeBlockType blockType, IBlockState state);

    /**
     * Called to add an entity spawn to the biome data.
     *
     * @param creatureType   The type of entity to be added.
     * @param spawnListEntry The entity spawn that is to be added.
     */
    void addEntitySpawn(EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry);

    /**
     * Called to add a biome trait to the biome data.
     *
     * @param generationStage The generation stage for the biome trait that is to be added.
     * @param biomeTrait      The biome trait that is to be added.
     */
    void addBiomeTrait(GenerationStage generationStage, IBiomeTrait biomeTrait);

    /**
     * Called to add a sub biome to the biome data.
     *
     * @param biomeData The biome wrapper for the sub biome that is to be added.
     */
    void addSubBiome(IBiomeData biomeData);

    /**
     * Called to write the current state of the biome data to its default config.
     * <p>
     * This should be called after the default values have been changed by a modder.
     * <p>
     * This should not be called after the biome data has been configured from an
     * external config because it may contain player edits.
     */
    void writeToDefaultConfig();

    /**
     * Called when the server is starting.
     * <p>
     * This is used to configure this biome data from a config.
     *
     * @param biomeDataRegistry The biome data registry that this biome data is registered to.
     * @param config       The config that belongs to the biome data.
     */
    void readFromConfig(IBiomeDataRegistry biomeDataRegistry, Config config);

    /**
     * Called when the server is stopping.
     * <p>
     * This is used to save this biome data to a config.
     *
     * @param config The config that belongs to the biome data.
     */
    void writeToConfig(Config config);

    /**
     * Called after {@link #writeToConfig}.
     * <p>
     * This is called to read the biome data from its default config.
     *
     * @param biomeDataRegistry The biome data registry that the biome data is registered to.
     */
    void readFromDefaultConfig(IBiomeDataRegistry biomeDataRegistry);

    /**
     * Called to check if the associated biome's default features should generate.
     *
     * @return Whether the associated biome's default features should generate.
     */
    boolean useDefaultBiomeDecorations();

    /**
     * Returns true if this data represents a sub biome.
     *
     * @return True if this data represents a sub biome.
     */
    boolean isSubBiome();

    /**
     * Called to check if the associated biome is enabled.
     *
     * @return Whether the associated biome is enabled.
     */
    boolean isEnabled();

    /**
     * Called to check if the biome data was created by a player.
     *
     * @return Whether the biome data was created by a player.
     */
    boolean isPlayerCreated();

    /**
     * Called to get the biome associated with this data.
     *
     * @return The biome associated with this data.
     */
    Biome getBiome();

    /**
     * Called to get the generation weight of the associated biome.
     *
     * @return The generation weight of the associated biome.
     */
    int getGenerationWeight();

    /**
     * Called to get a block that makes up the associated biome.
     *
     * @param blockType The type of block to get.
     * @return A block that makes up the associated biome.
     */
    IBlockState getBiomeBlock(BiomeBlockType blockType);

    /**
     * Called to get a map containing the biome blocks and their identifiers.
     *
     * @return A map containing the biome blocks and their types.
     */
    Map<BiomeBlockType, IBlockState> getBiomeBlocks();

    /**
     * Called to get a list of biome traits that generate in the associated biome.
     *
     * @param generationStage The stage to get the list for.
     * @return A list of biome traits that generate in the associated biome.
     */
    List<IBiomeTrait> getBiomeTraits(GenerationStage generationStage);

    /**
     * Called to get a list of entities that spawn in the associated biome.
     *
     * @param creatureType The type of entity to get the list for.
     * @return A list of entities that spawn in the associated biome.
     */
    List<Biome.SpawnListEntry> getEntitySpawns(EnumCreatureType creatureType);

    /**
     * Called to get a list of sub biomes that can generate in the associated biome.
     *
     * @return A list of sub biomes that can generate in the associated biome.
     */
    List<IBiomeData> getSubBiomes();

    /**
     * Called to get this biome data's relative config path.
     *
     * @return This biome data's relative config path.
     */
    String getRelativeConfigPath();
}
