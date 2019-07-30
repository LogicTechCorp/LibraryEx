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
import logictechcorp.libraryex.api.world.biome.BiomeBlockType;
import logictechcorp.libraryex.api.world.biome.data.IBiomeData;
import logictechcorp.libraryex.api.world.biome.data.IBiomeDataRegistry;
import logictechcorp.libraryex.api.world.generation.GenerationStage;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTrait;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTraitBuilder;
import logictechcorp.libraryex.utility.ConfigHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

/**
 * The base class for biome data.
 */
public class BiomeData implements IBiomeData
{
    protected final Biome biome;
    protected int generationWeight;
    protected boolean useDefaultDecorations;
    protected boolean isSubBiome;
    protected boolean isEnabled;
    protected final Map<BiomeBlockType, IBlockState> biomeBlocks;
    protected final Map<GenerationStage, List<IBiomeTrait>> biomeTraits;
    protected final Map<EnumCreatureType, List<Biome.SpawnListEntry>> entitySpawns;
    protected final List<IBiomeData> subBiomes;

    public BiomeData(Biome biome, int generationWeight, boolean useDefaultDecorations, boolean isSubBiome, boolean isEnabled)
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
        this.isEnabled = isEnabled;
        this.isSubBiome = isSubBiome;
        this.biomeBlocks = new EnumMap<>(BiomeBlockType.class);
        this.biomeTraits = new EnumMap<>(GenerationStage.class);
        this.entitySpawns = new EnumMap<>(EnumCreatureType.class);
        this.subBiomes = new ArrayList<>();
    }

    public BiomeData(ResourceLocation biomeRegistryName, int generationWeight, boolean useDefaultDecorations, boolean isSubBiome, boolean isEnabled)
    {
        this(ForgeRegistries.BIOMES.getValue(biomeRegistryName), generationWeight, useDefaultDecorations, isSubBiome, isEnabled);
    }

    @Override
    public void addBiomeBlock(BiomeBlockType blockType, IBlockState state)
    {
        this.biomeBlocks.put(blockType, state);
    }

    @Override
    public void addEntitySpawn(EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry)
    {
        this.entitySpawns.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(spawnListEntry);
    }

    @Override
    public void addBiomeTrait(GenerationStage generationStage, IBiomeTrait biomeTrait)
    {
        this.biomeTraits.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(biomeTrait);
    }

    @Override
    public void addSubBiome(IBiomeData biomeData)
    {
        this.subBiomes.add(biomeData);
    }

    @Override
    public void readFromConfig(IBiomeDataRegistry biomeDataRegistry, Config config)
    {
        this.generationWeight = config.getOrElse("generationWeight", this.generationWeight);

        if(this.generationWeight <= 0)
        {
            this.generationWeight = 10;
        }

        this.useDefaultDecorations = config.getOrElse("useDefaultDecorations", true);
        this.isSubBiome = config.getOrElse("isSubBiome", false);
        this.isEnabled = config.getOrElse("isEnabled", true);

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
                this.biomeBlocks.put(BiomeBlockType.getFromIdentifier(entry.getKey()), state);
            }
        }

        if(!(config.get("entities") instanceof List))
        {
            config.set("entities", new ArrayList<Config>());
        }

        List<Config> entities = new ArrayList<>();
        Iterator entityConfigIter = ((List) config.get("entities")).iterator();
        this.entitySpawns.clear();

        for(EnumCreatureType type : EnumCreatureType.values())
        {
            entryLoop:
            for(Biome.SpawnListEntry entry : this.biome.getSpawnableList(type))
            {
                ResourceLocation registryKey = ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass));
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
                entityConfig.add("entity", ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString());
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
            EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityConfig.get("entity")));

            if(entityEntry != null && config.getOrElse("spawn", true))
            {
                Class<? extends Entity> cls = entityEntry.getEntityClass();
                EnumCreatureType creatureType = null;

                for(EnumCreatureType type : EnumCreatureType.values())
                {
                    if(type.getCreatureClass().isAssignableFrom(cls))
                    {
                        creatureType = type;
                        break;
                    }
                }

                if(creatureType != null && EntityLiving.class.isAssignableFrom(cls))
                {
                    this.entitySpawns.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) cls, entityConfig.getOrElse("spawnWeight", 10), entityConfig.getOrElse("minimumGroupCount", 1), entityConfig.getOrElse("maximumGroupCount", 4)));
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
                this.biomeTraits.computeIfAbsent(GenerationStage.getFromIdentifier(biomeTraitConfig.get("generationStage")), k -> new ArrayList<>()).add(biomeTrait);
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

                if(biome != null && biomeDataRegistry.hasBiomeData(biome))
                {
                    this.subBiomes.add(biomeDataRegistry.getBiomeData(biome));
                }
            }
        }
    }

    @Override
    public void writeToConfig(Config config)
    {
        config.add("biome", this.biome.getRegistryName().toString());
        config.add("generationWeight", this.generationWeight);
        config.add("useDefaultDecorations", this.useDefaultDecorations);
        config.add("isSubBiome", this.isSubBiome);
        config.add("isEnabled", this.isEnabled);
        Config blockConfigs = JsonFormat.newConfig(LinkedHashMap::new);

        for(Map.Entry<BiomeBlockType, IBlockState> entry : this.biomeBlocks.entrySet())
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
                    entityConfig.add("spawn", true);
                    entityConfigs.add(entityConfig);
                }
            }
        }

        config.set("entities", entityConfigs);
        List<Config> biomeTraitConfigs = new ArrayList<>();

        for(Map.Entry<GenerationStage, List<IBiomeTrait>> entry : this.biomeTraits.entrySet())
        {
            for(IBiomeTrait biomeTrait : entry.getValue())
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

            for(IBiomeData biomeData : this.subBiomes)
            {
                if(biomeData.getBiome().getRegistryName() != null)
                {
                    subBiomeNames.add(biomeData.getBiome().getRegistryName().toString());
                }
            }

            config.set("subBiomes", subBiomeNames);
        }
    }

    @Override
    public Biome getBiome()
    {
        return this.biome;
    }

    @Override
    public int getGenerationWeight()
    {
        return this.generationWeight;
    }

    @Override
    public boolean useDefaultBiomeDecorations()
    {
        return this.useDefaultDecorations;
    }

    @Override
    public boolean isSubBiome()
    {
        return this.isSubBiome;
    }

    @Override
    public boolean isEnabled()
    {
        return this.isEnabled;
    }

    @Override
    public IBlockState getBiomeBlock(BiomeBlockType biomeBlock)
    {
        return this.biomeBlocks.get(biomeBlock);
    }

    @Override
    public Map<BiomeBlockType, IBlockState> getBiomeBlocks()
    {
        return this.biomeBlocks;
    }

    @Override
    public List<IBiomeTrait> getBiomeTraits(GenerationStage generationStage)
    {
        return this.biomeTraits.computeIfAbsent(generationStage, k -> new ArrayList<>());
    }

    @Override
    public List<Biome.SpawnListEntry> getEntitySpawns(EnumCreatureType creatureType)
    {
        return this.entitySpawns.computeIfAbsent(creatureType, k -> new ArrayList<>());
    }

    @Override
    public List<IBiomeData> getSubBiomes()
    {
        return this.subBiomes;
    }
}
