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
import logictechcorp.libraryex.api.LibraryExAPI;
import logictechcorp.libraryex.api.world.biome.IBiomeBlock;
import logictechcorp.libraryex.api.world.biome.data.IBiomeData;
import logictechcorp.libraryex.api.world.biome.data.IBiomeDataAPI;
import logictechcorp.libraryex.api.world.generation.IGeneratorStage;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTrait;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTraitBuilder;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.utility.EntityHelper;
import logictechcorp.libraryex.world.generation.GenerationStage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

/**
 * The base class for biome data.
 */
public class BiomeData implements IBiomeData
{
    protected Biome biome;
    protected int biomeGenerationWeight;
    protected boolean isSubBiomeData;
    protected boolean generateBiome;
    protected boolean generateDefaultBiomeFeatures;
    protected Map<String, BlockState> blocks;
    protected Map<EntityClassification, List<Biome.SpawnListEntry>> entities;
    protected Map<String, List<IBiomeTrait>> biomeTraits;
    protected List<IBiomeData> subBiomeData;

    public BiomeData(Biome biome, int biomeGenerationWeight, boolean isSubBiomeData, boolean generateBiome, boolean generateDefaultBiomeFeatures)
    {
        if(biome != null)
        {
            this.biome = biome;
        }
        else
        {
            this.biome = Biomes.PLAINS;
            generateBiome = false;
        }

        this.biomeGenerationWeight = biomeGenerationWeight;
        this.isSubBiomeData = isSubBiomeData;
        this.generateBiome = generateBiome;
        this.generateDefaultBiomeFeatures = generateDefaultBiomeFeatures;
        this.blocks = new HashMap<>();
        this.entities = new HashMap<>();
        this.biomeTraits = new HashMap<>();
        this.subBiomeData = new ArrayList<>();
    }

    public BiomeData(ResourceLocation biomeRegistryName, int biomeGenerationWeight, boolean isSubBiomeData, boolean generateBiome, boolean generateDefaultBiomeFeatures)
    {
        this(ForgeRegistries.BIOMES.getValue(biomeRegistryName), biomeGenerationWeight, isSubBiomeData, generateBiome, generateDefaultBiomeFeatures);
    }

    public BiomeData(ResourceLocation biomeRegistryName)
    {
        this(biomeRegistryName, 10, false, true, true);
    }

    @Override
    public void readFromConfig(IBiomeDataAPI biomeDataAPI, Config config)
    {
        this.biomeGenerationWeight = config.getOrElse("biomeGenerationWeight", this.biomeGenerationWeight);
        this.isSubBiomeData = config.getOrElse("isSubBiome", false);
        this.generateBiome = config.getOrElse("generateBiome", true);
        this.generateDefaultBiomeFeatures = config.getOrElse("generateDefaultBiomeFeatures", true);

        if(!(config.get("blocks") instanceof Config))
        {
            config.set("blocks", JsonFormat.newConfig(LinkedHashMap::new));
        }

        Config blocks = config.get("blocks");
        this.blocks.clear();

        for(Config.Entry entry : blocks.entrySet())
        {
            BlockState state = ConfigHelper.getBlockState(config, "blocks." + entry.getKey());

            if(state != null)
            {
                this.blocks.put(entry.getKey(), state);
            }
        }

        if(!(config.get("entities") instanceof List))
        {
            config.set("entities", new ArrayList<Config>());
        }

        List<Config> entities = new ArrayList<>();
        Iterator entityConfigIter = ((List) config.get("entities")).iterator();
        this.entities.clear();

        for(EntityClassification type : EntityClassification.values())
        {
            entryLoop:
            for(Biome.SpawnListEntry entry : this.biome.getSpawns(type))
            {
                ResourceLocation registryKey = ForgeRegistries.ENTITIES.getKey(entry.entityType);
                boolean containsEntry = false;

                while(entityConfigIter.hasNext())
                {
                    Config entityConfig = (Config) entityConfigIter.next();

                    if(registryKey != null && entityConfig.get("entity") instanceof String && ((String) entityConfig.get("entity")).equalsIgnoreCase(registryKey.toString()))
                    {
                        containsEntry = true;
                    }

                    entities.add(entityConfig);
                    entityConfigIter.remove();

                    if(containsEntry)
                    {
                        continue entryLoop;
                    }
                }

                Config entityConfig = JsonFormat.newConfig(LinkedHashMap::new);
                entityConfig.add("entity", ForgeRegistries.ENTITIES.getKey(entry.entityType));
                entityConfig.add("spawnWeight", entry.itemWeight);
                entityConfig.add("minimumGroupCount", entry.minGroupCount);
                entityConfig.add("maximumGroupCount", entry.maxGroupCount);
                entityConfig.add("spawn", true);
                entities.add(entityConfig);
            }
        }

        config.set("entities", entities);

        for(Config entityConfig : entities)
        {
            EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityConfig.get("entity")));

            if(entityType != null && config.getOrElse("spawn", true))
            {
                EntityClassification entityClass = entityType.getClassification();

                if(entityType.getClassification() != EntityClassification.MISC)
                {
                    this.entities.computeIfAbsent(entityClass, k -> new ArrayList<>()).add(new Biome.SpawnListEntry(entityType, entityConfig.getOrElse("spawnWeight", 10), entityConfig.getOrElse("minimumGroupCount", 1), entityConfig.getOrElse("maximumGroupCount", 4)));
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
            IBiomeTraitBuilder biomeTraitBuilder = LibraryExAPI.getInstance().getBiomeTraitRegistry().getBiomeTraitBuilder(new ResourceLocation(biomeTraitConfig.get("trait")));

            if(biomeTraitBuilder != null)
            {
                IBiomeTrait biomeTrait = biomeTraitBuilder.create();
                biomeTrait.readFromConfig(biomeTraitConfig);

                if(this.generateBiome)
                {
                    String generationStage = biomeTraitConfig.getOrElse("generationStage", GenerationStage.DECORATION.getIdentifier());

                    if(generationStage != null)
                    {
                        this.biomeTraits.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(biomeTrait);
                    }
                    else
                    {
                        this.biomeTraits.computeIfAbsent(GenerationStage.DECORATION.getIdentifier(), k -> new ArrayList<>()).add(biomeTrait);
                    }
                }
            }

            biomeTraits.add(biomeTraitConfig);
        }

        config.set("traits", biomeTraits);

        if(!this.isSubBiomeData)
        {
            if(!(config.get("subBiomes") instanceof List))
            {
                config.set("subBiomes", new ArrayList<String>());
            }

            List<String> subBiomeNames = config.get("subBiomes");
            this.subBiomeData.clear();

            for(String subBiomeName : subBiomeNames)
            {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(subBiomeName));

                if(biome != null)
                {
                    this.subBiomeData.add(biomeDataAPI.getBiomeDataRegistry().getBiomeData(biome));
                }
            }
        }
    }

    @Override
    public void writeToConfig(Config config)
    {
        config.add("biome", this.biome.getRegistryName().toString());
        config.add("biomeGenerationWeight", this.biomeGenerationWeight);
        config.add("isSubBiome", this.isSubBiomeData);
        config.add("generateBiome", this.generateBiome);
        config.add("generateDefaultBiomeFeatures", this.generateDefaultBiomeFeatures);
        Config blockConfigs = JsonFormat.newConfig(LinkedHashMap::new);

        for(Map.Entry<String, BlockState> entry : this.blocks.entrySet())
        {
            ConfigHelper.setBlockState(blockConfigs, entry.getKey(), entry.getValue());
        }

        config.add("blocks", blockConfigs);
        List<Config> entityConfigs = new ArrayList<>();

        for(EntityClassification type : EntityClassification.values())
        {
            for(Biome.SpawnListEntry entry : this.getBiomeEntities(type))
            {
                ResourceLocation entityRegistryName = EntityHelper.getEntityLocation(entry.entityType);

                if(entityRegistryName != null)
                {
                    Config entityConfig = JsonFormat.newConfig(LinkedHashMap::new);
                    entityConfig.add("entity", entityRegistryName.toString());
                    entityConfig.add("entitySpawnWeight", entry.itemWeight);
                    entityConfig.add("minimumGroupCount", entry.minGroupCount);
                    entityConfig.add("maximumGroupCount", entry.maxGroupCount);
                    entityConfig.add("spawn", true);
                    entityConfigs.add(entityConfig);
                }
            }
        }

        config.add("entities", entityConfigs);
        List<Config> biomeTraitConfigs = new ArrayList<>();

        for(Map.Entry<String, List<IBiomeTrait>> entry : this.biomeTraits.entrySet())
        {
            String generatorStage = entry.getKey();

            for(IBiomeTrait biomeTrait : entry.getValue())
            {
                Config biomeTraitConfig = JsonFormat.newConfig(LinkedHashMap::new);
                biomeTrait.writeToConfig(biomeTraitConfig);
                biomeTraitConfig.add("generationStage", generatorStage);
                biomeTraitConfigs.add(biomeTraitConfig);
            }
        }

        config.add("traits", biomeTraitConfigs);

        if(!this.isSubBiomeData)
        {
            List<String> subBiomeNames = new ArrayList<>();

            for(IBiomeData biomeData : this.subBiomeData)
            {
                if(biomeData.getBiome().getRegistryName() != null)
                {
                    subBiomeNames.add(biomeData.getBiome().getRegistryName().toString());
                }
            }

            config.add("subBiomes", subBiomeNames);
        }
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
    public boolean isSubBiomeData()
    {
        return this.isSubBiomeData;
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
    public BlockState getBiomeBlock(IBiomeBlock biomeBlock, BlockState fallback)
    {
        BlockState value = this.blocks.get(biomeBlock.getIdentifier());

        if(value == null)
        {
            this.blocks.put(biomeBlock.getIdentifier(), fallback);
            return fallback;
        }

        return value;
    }

    @Override
    public Map<String, BlockState> getBiomeBlocks()
    {
        return this.blocks;
    }

    @Override
    public List<Biome.SpawnListEntry> getBiomeEntities(EntityClassification creatureType)
    {
        return this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>());
    }

    @Override
    public List<IBiomeTrait> getBiomeTraits(IGeneratorStage generationStage)
    {
        return this.biomeTraits.computeIfAbsent(generationStage.getIdentifier(), k -> new ArrayList<>());
    }

    @Override
    public List<IBiomeData> getSubBiomeData()
    {
        return this.subBiomeData;
    }

    @Override
    public String getRelativeSaveFile()
    {
        return "biomes/" + this.biome.getRegistryName().toString().replace(":", "/") + ".json";
    }
}
