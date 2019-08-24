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
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class BiomeData
{
    public static final BiomeData EMPTY = new BiomeData(Biomes.PLAINS, 10, true, false)
    {
        @Override
        public void configureBiome()
        {
        }

        @Override
        public void resetBiome()
        {
        }
    };

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

    public abstract void configureBiome();

    public abstract void resetBiome();

    public void addBiomeBlock(BlockType blockType, BlockState blockState)
    {
        this.blocks.put(blockType, blockState);
    }

    public void addEntitySpawn(Biome.SpawnListEntry spawnListEntry)
    {
        this.spawns.computeIfAbsent(spawnListEntry.entityType.getClassification(), k -> new ArrayList<>()).add(spawnListEntry);
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

    public List<BiomeData> getSubBiomes()
    {
        return this.subBiomes;
    }

    public BlockState getBiomeBlock(BlockType blockType)
    {
        return this.blocks.get(blockType);
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
