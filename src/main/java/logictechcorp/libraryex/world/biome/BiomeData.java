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
import logictechcorp.libraryex.world.generation.trait.IBiomeTrait;
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
    protected int biomeGenerationWeight;
    protected boolean generateBiome;
    protected boolean generateDefaultBiomeFeatures;
    protected Map<String, IBlockState> blocks;
    protected Map<EnumCreatureType, List<Biome.SpawnListEntry>> entities;
    protected Map<GenerationStage, List<IBiomeTrait>> features;

    public BiomeData(Biome biome, int biomeGenerationWeight, boolean generateBiome, boolean generateDefaultBiomeFeatures)
    {
        this.biome = biome;
        this.biomeGenerationWeight = biomeGenerationWeight;
        this.generateBiome = generateBiome;
        this.generateDefaultBiomeFeatures = generateDefaultBiomeFeatures;
        this.blocks = new HashMap<>();
        this.entities = new HashMap<>();
        this.features = new HashMap<>();
    }

    public BiomeData(ResourceLocation biomeRegistryName, int biomeGenerationWeight, boolean generateBiome, boolean generateDefaultBiomeFeatures)
    {
        this(ForgeRegistries.BIOMES.getValue(biomeRegistryName), biomeGenerationWeight, generateBiome, generateDefaultBiomeFeatures);
    }

    public BiomeData(ResourceLocation biomeRegistryName)
    {
        this(biomeRegistryName, 10, true, true);
    }

    public BiomeData(Biome biome)
    {
        this(biome.getRegistryName());
    }

    @Override
    public Biome getBiome()
    {
        return this.biome;
    }

    @Override
    public int getBiomeGenerationWeight()
    {
        return this.biomeGenerationWeight;
    }

    @Override
    public boolean generateBiome()
    {
        return this.generateBiome;
    }

    @Override
    public boolean generateDefaultBiomeFeatures()
    {
        return this.generateDefaultBiomeFeatures;
    }

    @Override
    public IBlockState getBiomeBlock(IBiomeBlock type, IBlockState fallback)
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
    public Map<String, IBlockState> getBiomeBlocks()
    {
        return this.blocks;
    }

    @Override
    public List<Biome.SpawnListEntry> getBiomeEntities(EnumCreatureType creatureType)
    {
        return this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>());
    }

    @Override
    public List<IBiomeTrait> getBiomeTraits(GenerationStage generationStage)
    {
        return this.features.computeIfAbsent(generationStage, k -> new ArrayList<>());
    }
}
