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

package logictechcorp.libraryex.world.biome.data;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.world.generation.GenerationStage;
import logictechcorp.libraryex.world.generation.trait.BiomeTrait;
import logictechcorp.libraryex.world.generation.trait.BiomeTraitRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

/**
 * The base class for biome data.
 */
public class BiomeData
{
    public static final BiomeData EMPTY = new BiomeData(Biomes.PLAINS, 10, true, false);

    protected final Biome biome;
    protected int generationWeight;
    protected boolean useDefaultDecorations;
    protected boolean isSubBiome;
    protected final Map<BlockType, IBlockState> biomeBlocks;
    protected final Map<GenerationStage, List<BiomeTrait>> biomeTraits;
    protected final Map<EnumCreatureType, List<Biome.SpawnListEntry>> entitySpawns;
    protected final List<BiomeData> subBiomes;

    public BiomeData(Biome biome, int generationWeight, boolean useDefaultDecorations, boolean isSubBiome)
    {
        if(biome != null)
        {
            this.biome = biome;
        }
        else
        {
            this.biome = Biomes.PLAINS;
        }

        this.generationWeight = generationWeight;
        this.useDefaultDecorations = useDefaultDecorations;
        this.isSubBiome = isSubBiome;
        this.biomeBlocks = new EnumMap<>(BlockType.class);
        this.biomeTraits = new EnumMap<>(GenerationStage.class);
        this.entitySpawns = new EnumMap<>(EnumCreatureType.class);
        this.subBiomes = new ArrayList<>();
    }

    public BiomeData(ResourceLocation biomeRegistryName, int generationWeight, boolean useDefaultDecorations, boolean isSubBiome)
    {
        this(ForgeRegistries.BIOMES.getValue(biomeRegistryName), generationWeight, useDefaultDecorations, isSubBiome);
    }

    public void addBiomeBlock(BlockType blockType, IBlockState state)
    {
        this.biomeBlocks.put(blockType, state);
    }

    public void addEntitySpawn(EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry)
    {
        if(spawnListEntry.itemWeight > 0)
        {
            this.entitySpawns.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(spawnListEntry);
        }

        for(EnumCreatureType type : EnumCreatureType.values())
        {
            this.biome.getSpawnableList(type).removeIf(entry -> entry.entityClass == spawnListEntry.entityClass);
        }
    }

    public void addBiomeTrait(GenerationStage generationStage, BiomeTrait biomeTrait)
    {
        this.biomeTraits.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(biomeTrait);
    }

    public void addSubBiome(BiomeData biomeData)
    {
        this.subBiomes.add(biomeData);
    }

    public void readFromConfig(BiomeDataManager biomeDataManager, Config config)
    {
        this.generationWeight = config.getOrElse("generationWeight", this.generationWeight);

        if(this.generationWeight < 0)
        {
            this.generationWeight = 0;
        }

        this.useDefaultDecorations = config.getOrElse("useDefaultDecorations", this.useDefaultDecorations);
        this.isSubBiome = config.getOrElse("isSubBiome", this.isSubBiome);

        if(!(config.get("blocks") instanceof Config))
        {
            config.set("blocks", JsonFormat.newConfig(LinkedHashMap::new));
        }

        Config blocks = config.get("blocks");
        this.biomeBlocks.clear();

        for(Config.Entry entry : blocks.entrySet())
        {
            IBlockState state = ConfigHelper.getBlockState(config, "blocks." + entry.getKey());

            if(state != null)
            {
                this.biomeBlocks.put(BlockType.getFromIdentifier(entry.getKey()), state);
            }
        }

        if(!(config.get("entities") instanceof List))
        {
            config.set("entities", new ArrayList<Config>());
        }

        List<Config> entities = config.get("entities");
        this.entitySpawns.clear();

        for(Config entityConfig : entities)
        {
            EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityConfig.get("entity")));

            if(entityEntry != null)
            {
                Class<? extends Entity> cls = entityEntry.getEntityClass();

                for(EnumCreatureType creatureType : EnumCreatureType.values())
                {
                    if(EntityLiving.class.isAssignableFrom(cls) && creatureType.getCreatureClass().isAssignableFrom(cls))
                    {
                        int spawnWeight = entityConfig.getOrElse("spawnWeight", 10);
                        int minimumGroupCount = entityConfig.getOrElse("minimumGroupCount", 1);
                        int maximumGroupCount = entityConfig.getOrElse("maximumGroupCount", 4);

                        if(spawnWeight < 0)
                        {
                            spawnWeight = 0;
                        }

                        this.addEntitySpawn(creatureType, new Biome.SpawnListEntry((Class<? extends EntityLiving>) cls, spawnWeight, minimumGroupCount, maximumGroupCount));
                        break;
                    }
                }
            }
        }

        if(!(config.get("traits") instanceof List))
        {
            config.set("traits", new ArrayList<Config>());
        }

        List<Config> biomeTraits = new ArrayList<>();
        List<Config> biomeTraitConfigs = config.get("traits");
        this.biomeTraits.clear();

        for(Config biomeTraitConfig : biomeTraitConfigs)
        {
            BiomeTrait.Builder<?> biomeTraitBuilder = BiomeTraitRegistry.INSTANCE.getBiomeTraitBuilder(new ResourceLocation(biomeTraitConfig.get("trait")));

            if(biomeTraitBuilder != null)
            {
                BiomeTrait biomeTrait = biomeTraitBuilder.create();
                biomeTrait.readFromConfig(biomeTraitConfig);
                this.addBiomeTrait(GenerationStage.getFromIdentifier(biomeTraitConfig.get("generationStage")), biomeTrait);
            }

            biomeTraits.add(biomeTraitConfig);
        }

        config.set("traits", biomeTraits);

        if(!this.isSubBiome)
        {
            if(!(config.get("subBiomes") instanceof List))
            {
                config.set("subBiomes", new ArrayList<String>());
            }

            List<String> subBiomeNames = config.get("subBiomes");
            this.subBiomes.clear();

            for(String subBiomeName : subBiomeNames)
            {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(subBiomeName));

                if(biome != null && biomeDataManager.hasBiomeData(biome))
                {
                    this.subBiomes.add(biomeDataManager.getBiomeData(biome));
                }
            }
        }
    }

    public void writeToConfig(Config config)
    {
        config.add("biome", this.biome.getRegistryName().toString());
        config.add("generationWeight", this.generationWeight);
        config.add("useDefaultDecorations", this.useDefaultDecorations);
        config.add("isSubBiome", this.isSubBiome);
        Config blockConfigs = JsonFormat.newConfig(LinkedHashMap::new);

        for(Map.Entry<BlockType, IBlockState> entry : this.biomeBlocks.entrySet())
        {
            ConfigHelper.setBlockState(blockConfigs, entry.getKey().toString(), entry.getValue());
        }

        config.set("blocks", blockConfigs);
        List<Config> entityConfigs = new ArrayList<>();

        for(EnumCreatureType type : EnumCreatureType.values())
        {
            if(!this.entitySpawns.containsKey(type))
            {
                this.entitySpawns.put(type, new ArrayList<>(this.biome.getSpawnableList(type)));
            }

            for(Biome.SpawnListEntry entry : this.entitySpawns.get(type))
            {
                ResourceLocation entityRegistryName = EntityList.getKey(entry.entityClass);

                if(entityRegistryName != null)
                {
                    Config entityConfig = JsonFormat.newConfig(LinkedHashMap::new);
                    entityConfig.add("entity", entityRegistryName.toString());
                    entityConfig.add("spawnWeight", entry.itemWeight);
                    entityConfig.add("minimumGroupCount", entry.minGroupCount);
                    entityConfig.add("maximumGroupCount", entry.maxGroupCount);
                    entityConfigs.add(entityConfig);
                }
            }
        }

        config.set("entities", entityConfigs);
        List<Config> biomeTraitConfigs = new ArrayList<>();

        for(Map.Entry<GenerationStage, List<BiomeTrait>> entry : this.biomeTraits.entrySet())
        {
            for(BiomeTrait biomeTrait : entry.getValue())
            {
                Config biomeTraitConfig = JsonFormat.newConfig(LinkedHashMap::new);
                biomeTrait.writeToConfig(biomeTraitConfig);
                biomeTraitConfig.add("generationStage", entry.getKey().toString());
                biomeTraitConfigs.add(biomeTraitConfig);
            }
        }

        config.set("traits", biomeTraitConfigs);

        if(!this.isSubBiome)
        {
            List<String> subBiomeNames = new ArrayList<>();

            for(BiomeData biomeData : this.subBiomes)
            {
                if(biomeData.getBiome().getRegistryName() != null)
                {
                    subBiomeNames.add(biomeData.getBiome().getRegistryName().toString());
                }
            }

            config.set("subBiomes", subBiomeNames);
        }
    }

    public void generateTerrain(World world, Random random, ChunkPrimer primer, int posX, int posZ, double noise)
    {
        this.biome.genTerrainBlocks(world, random, primer, posX, posZ, noise);
    }

    public Biome getBiome()
    {
        return this.biome;
    }

    public int getGenerationWeight()
    {
        return this.generationWeight;
    }

    public boolean useDefaultBiomeDecorations()
    {
        return this.useDefaultDecorations;
    }

    public boolean isSubBiome()
    {
        return this.isSubBiome;
    }

    public boolean isEnabled()
    {
        return this.generationWeight > 0;
    }

    public IBlockState getBiomeBlock(BlockType blockType)
    {
        IBlockState state = this.biomeBlocks.get(blockType);

        if(state == null)
        {
            switch(blockType)
            {
                case SURFACE_BLOCK:
                    state = this.biome.topBlock;
                    break;
                case SUBSURFACE_BLOCK:
                    state = this.biome.fillerBlock;
                    break;
                case LIQUID_BLOCK:
                    state = Blocks.LAVA.getDefaultState();
                    break;
            }

            this.addBiomeBlock(blockType, state);
        }

        return state;
    }

    public Map<BlockType, IBlockState> getBiomeBlocks()
    {
        return this.biomeBlocks;
    }

    public List<BiomeTrait> getBiomeTraits(GenerationStage generationStage)
    {
        return this.biomeTraits.computeIfAbsent(generationStage, k -> new ArrayList<>());
    }

    public List<Biome.SpawnListEntry> getEntitySpawns(EnumCreatureType creatureType)
    {
        return this.entitySpawns.computeIfAbsent(creatureType, k -> new ArrayList<>());
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

        private final String identifier;

        BlockType(String identifier)
        {
            this.identifier = identifier;
        }

        public static BlockType getFromIdentifier(String identifier)
        {
            for(BlockType type : BlockType.values())
            {
                if(type.toString().equals(identifier))
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
