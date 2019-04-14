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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for biome data.
 */
public class BiomeData implements IBiomeData
{
    protected Biome biome;
    protected int weight;
    protected boolean enabled;
    protected boolean generateDefaultFeatures;
    protected Map<String, IBlockState> blocks;
    protected Map<EnumCreatureType, List<Biome.SpawnListEntry>> entities;
    protected Map<GenerationStage, List<FeatureMod>> features;

    public BiomeData(ResourceLocation biomeRegistryName, int weight, boolean enabled, boolean generateDefaultFeatures)
    {
        this.biome = ForgeRegistries.BIOMES.getValue(biomeRegistryName);
        this.weight = weight;
        this.enabled = enabled;
        this.generateDefaultFeatures = generateDefaultFeatures;
        this.blocks = new HashMap<>();
        this.entities = new HashMap<>();
        this.features = new HashMap<>();
    }

    public BiomeData(ResourceLocation biomeRegistryName)
    {
        this(biomeRegistryName, 10, true, true);
    }

    @Override
    public Biome getBiome()
    {
        return this.biome;
    }

    @Override
    public int getWeight()
    {
        return this.weight;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public boolean generateDefaultFeatures()
    {
        return this.generateDefaultFeatures;
    }

    @Override
    public IBlockState getBiomeBlock(IBiomeData.BlockType type, IBlockState fallback)
    {
        IBlockState value = this.blocks.get(type.getIdentifier());

        if(value == null)
        {
            this.blocks.put(type.getIdentifier(), fallback);
            return fallback;
        }

        return value;
    }

    @Override
    public Map<String, IBlockState> getBlocks()
    {
        return this.blocks;
    }

    @Override
    public List<Biome.SpawnListEntry> getEntities(EnumCreatureType creatureType)
    {
        return this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>());
    }

    @Override
    public List<FeatureMod> getFeatures(GenerationStage generationStage)
    {
        return this.features.computeIfAbsent(generationStage, k -> new ArrayList<>());
    }
}
