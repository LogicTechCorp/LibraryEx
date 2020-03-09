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
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.*;

public class BiomeData
{
    public static final BiomeData EMPTY = new BiomeData(Biomes.PLAINS, 10, true, true, false);

    protected final Biome biome;
    protected int generationWeight;
    protected boolean useDefaultFeatures;
    protected boolean useDefaultCarvers;
    protected boolean isSubBiome;
    protected final Map<BlockType, BlockState> blocks;
    protected final Map<EntityClassification, List<Biome.SpawnListEntry>> spawns;
    protected final Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> carvers;
    protected final Map<GenerationStage.Decoration, List<ConfiguredFeature<?>>> features;
    protected final List<BiomeData> subBiomes;

    public BiomeData(Biome biome, int generationWeight, boolean useDefaultCarvers, boolean useDefaultFeatures, boolean isSubBiome)
    {
        this.biome = biome;
        this.generationWeight = generationWeight;
        this.useDefaultCarvers = useDefaultCarvers;
        this.useDefaultFeatures = useDefaultFeatures;
        this.isSubBiome = isSubBiome;
        this.blocks = new EnumMap<>(BlockType.class);
        this.spawns = new EnumMap<>(EntityClassification.class);
        this.carvers = new EnumMap<>(GenerationStage.Carving.class);
        this.features = new EnumMap<>(GenerationStage.Decoration.class);
        this.subBiomes = new ArrayList<>();
    }

    public void configureBiome()
    {

    }

    public void resetBiome()
    {

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

    public void addCarver(GenerationStage.Carving stage, ConfiguredCarver<?> carver)
    {
        this.carvers.computeIfAbsent(stage, k -> new ArrayList<>()).add(carver);
    }

    public void addFeature(GenerationStage.Decoration stage, ConfiguredFeature<?> feature)
    {
        this.features.computeIfAbsent(stage, k -> new ArrayList<>()).add(feature);
    }

    public void addSubBiome(BiomeData subBiomeData)
    {
        this.subBiomes.add(subBiomeData);
    }

    public void carve(GenerationStage.Carving stage, IChunk chunk, long seed, int seaLevel)
    {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        SharedSeedRandom random = new SharedSeedRandom();

        for(int posX = chunkX - 8; posX <= chunkX + 8; posX++)
        {
            for(int posZ = chunkZ - 8; posZ <= chunkZ + 8; posZ++)
            {
                List<ConfiguredCarver<?>> carvers = new ArrayList<>();

                if(this.useDefaultCarvers)
                {
                    carvers.addAll(this.biome.getCarvers(stage));
                }

                carvers.addAll(this.getCarvers(stage));
                ListIterator<ConfiguredCarver<?>> carverIter = carvers.listIterator();

                while(carverIter.hasNext())
                {
                    int index = carverIter.nextIndex();
                    ConfiguredCarver<?> carver = carverIter.next();
                    random.setLargeFeatureSeed(seed + (long) index, posX, posZ);

                    if(carver.shouldCarve(random, posX, posZ))
                    {
                        carver.carve(chunk, random, seaLevel, posX, posZ, chunkX, chunkZ, chunk.getCarvingMask(stage));
                    }
                }
            }
        }
    }

    public void decorate(GenerationStage.Decoration stage, ChunkGenerator<? extends GenerationSettings> chunkGenerator, IWorld world, long seed, SharedSeedRandom random, BlockPos pos)
    {
        int featureCount = 0;

        List<ConfiguredFeature<?>> features = new ArrayList<>();

        if(this.useDefaultFeatures)
        {
            features.addAll(this.biome.getFeatures(stage));
        }

        features.addAll(this.getFeatures(stage));

        for(ConfiguredFeature<?> feature : features)
        {
            random.setFeatureSeed(seed, featureCount, stage.ordinal());

            try
            {
                feature.place(world, chunkGenerator, random, pos);
            }
            catch(Exception exception)
            {
                CrashReport crashReport = CrashReport.makeCrashReport(exception, "Feature placement");
                crashReport.makeCategory("Feature").addDetail("Id", feature.feature.getRegistryName()).addDetail("Description", feature.feature::toString);
                throw new ReportedException(crashReport);
            }

            featureCount++;
        }
    }

    public Biome getBiome()
    {
        return this.biome;
    }

    public int getGenerationWeight()
    {
        return this.generationWeight;
    }

    public boolean useDefaultCarvers()
    {
        return this.useDefaultCarvers;
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

    public List<ConfiguredCarver<?>> getCarvers(GenerationStage.Carving stage)
    {
        return this.carvers.computeIfAbsent(stage, k -> new ArrayList<>());
    }

    public List<ConfiguredFeature<?>> getFeatures(GenerationStage.Decoration stage)
    {
        return this.features.computeIfAbsent(stage, k -> new ArrayList<>());
    }

    public List<BiomeData> getSubBiomes()
    {
        return this.subBiomes;
    }

    public enum BlockType
    {
        SURFACE_BLOCK("surface"),
        SUBSURFACE_BLOCK("subsurface"),
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
