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

import com.mojang.datafixers.Dynamic;
import logictechcorp.libraryex.world.generation.feature.BiomeDataFeatureWrapper;
import logictechcorp.libraryex.world.generation.feature.LibraryExFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.*;

public class BiomeData
{
    public static final BiomeData EMPTY = new BiomeData(Biomes.PLAINS, 10, true, false);

    protected final Biome biome;
    protected int generationWeight;
    protected boolean useDefaultFeatures;
    protected boolean isSubBiome;
    protected final Map<BlockType, BlockState> blocks;
    protected final Map<EntityClassification, List<Biome.SpawnListEntry>> spawns;
    protected final Map<GenerationStage.Decoration, List<ConfiguredFeature<?>>> defaultFeatures;
    protected final Map<GenerationStage.Decoration, List<ConfiguredFeature<?>>> customFeatures;
    protected final List<BiomeData> subBiomes;

    public BiomeData(Biome biome, int generationWeight, boolean useDefaultFeatures, boolean isSubBiome)
    {
        this.biome = biome;
        this.generationWeight = generationWeight;
        this.useDefaultFeatures = useDefaultFeatures;
        this.isSubBiome = isSubBiome;
        this.blocks = new EnumMap<>(BlockType.class);
        this.spawns = new EnumMap<>(EntityClassification.class);
        this.defaultFeatures = new EnumMap<>(GenerationStage.Decoration.class);
        this.customFeatures = new EnumMap<>(GenerationStage.Decoration.class);
        this.subBiomes = new ArrayList<>();
    }

    public void configureBiome()
    {
        for(GenerationStage.Decoration stage : GenerationStage.Decoration.values())
        {
            this.defaultFeatures.computeIfAbsent(stage, k -> new ArrayList<>());
            List<ConfiguredFeature<?>> features = this.biome.getFeatures(stage);

            for(ConfiguredFeature feature : features)
            {
                this.defaultFeatures.get(stage).add(feature);
            }

            if(!this.useDefaultFeatures)
            {
                features.clear();
            }
        }

        for(GenerationStage.Decoration stage : GenerationStage.Decoration.values())
        {
            List<ConfiguredFeature<?>> features = this.customFeatures.get(stage);

            if(features != null)
            {
                this.biome.addFeature(stage, Biome.createDecoratedFeature(LibraryExFeatures.BIOME_DATA_FEATURE_WRAPPER.get(), new BiomeDataFeatureWrapper.Config(features), Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
            }
        }
    }

    public void resetBiome()
    {
        for(GenerationStage.Decoration stage : GenerationStage.Decoration.values())
        {
            Iterator<ConfiguredFeature<?>> features = this.biome.getFeatures(stage).iterator();

            while(features.hasNext())
            {
                ConfiguredFeature<?> feature = features.next();
                IFeatureConfig featureConfig = feature.config;

                if(featureConfig instanceof DecoratedFeatureConfig)
                {
                    DecoratedFeatureConfig decoratedFeatureConfig = (DecoratedFeatureConfig) featureConfig;

                    if(decoratedFeatureConfig.feature.feature instanceof BiomeDataFeatureWrapper)
                    {
                        features.remove();
                    }
                }
            }
        }

        if(!this.useDefaultFeatures)
        {
            for(GenerationStage.Decoration stage : GenerationStage.Decoration.values())
            {
                this.defaultFeatures.computeIfAbsent(stage, k -> new ArrayList<>());
                List<ConfiguredFeature<?>> features = this.defaultFeatures.get(stage);

                for(ConfiguredFeature feature : features)
                {
                    this.biome.addFeature(stage, feature);
                }
            }
        }
    }

    public void addBiomeBlock(BlockType blockType, BlockState blockState)
    {
        this.blocks.put(blockType, blockState);
    }

    public void addEntitySpawn(Biome.SpawnListEntry spawnListEntry)
    {
        if(spawnListEntry.itemWeight > 0)
        {
            this.spawns.computeIfAbsent(spawnListEntry.entityType.getClassification(), k -> new ArrayList<>()).add(spawnListEntry);
        }

        for(EntityClassification classification : EntityClassification.values())
        {
            this.biome.getSpawns(classification).removeIf(entry -> entry.entityType == spawnListEntry.entityType);
        }
    }

    public void addFeature(GenerationStage.Decoration stage, ConfiguredFeature<?> feature)
    {
        this.customFeatures.computeIfAbsent(stage, k -> new ArrayList<>()).add(feature);
    }

    public void addSubBiome(BiomeData subBiomeData)
    {
        this.subBiomes.add(subBiomeData);
    }

    public Biome getBiome()
    {
        return this.biome;
    }

    public int getGenerationWeight()
    {
        return this.generationWeight;
    }

    public boolean useDefaultFeatures()
    {
        return this.useDefaultFeatures;
    }

    public boolean isSubBiome()
    {
        return this.isSubBiome;
    }

    public BlockState getBiomeBlock(BlockType blockType)
    {
        return this.blocks.computeIfAbsent(blockType, k -> Blocks.AIR.getDefaultState());
    }

    public List<Biome.SpawnListEntry> getSpawns(EntityClassification classification)
    {
        return this.spawns.computeIfAbsent(classification, k -> new ArrayList<>());
    }

    public List<BiomeData> getSubBiomes()
    {
        return this.subBiomes;
    }

    public enum BlockType
    {
        SURFACE_BLOCK("surface"),
        SUBSURFACE_BLOCK("subsurface"),
        CAVE_CEILING_BLOCK("cave_ceiling"),
        CAVE_WALL_BLOCK("cave_wall"),
        CAVE_FLOOR_BLOCK("cave_floor"),
        LIQUID_BLOCK("liquid");

        private String identifier;

        BlockType(String identifier)
        {
            this.identifier = identifier;
        }

        public static <T> BlockType deserialize(Dynamic<T> dynamic)
        {
            for(BlockType type : BlockType.values())
            {
                if(dynamic.asString().orElse("").equals(type.toString()))
                {
                    return type;
                }
            }

            return SURFACE_BLOCK;
        }

        @Override
        public String toString()
        {
            return this.identifier;
        }
    }
}
