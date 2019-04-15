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

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.config.ModJsonConfigFormat;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.world.generation.GenerationStage;
import logictechcorp.libraryex.world.generation.trait.BiomeTraitRegistry;
import logictechcorp.libraryex.world.generation.trait.IBiomeTrait;
import logictechcorp.libraryex.world.generation.trait.IBiomeTraitConfigurable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The base class for biome data that can be configured from json.
 */
public class BiomeDataConfigurable extends BiomeData implements IBiomeDataConfigurable
{
    public BiomeDataConfigurable(Biome biome, int biomeGenerationWeight, boolean generateBiome, boolean generateDefaultBiomeFeatures)
    {
        super(biome, biomeGenerationWeight, generateBiome, generateDefaultBiomeFeatures);
    }

    public BiomeDataConfigurable(ResourceLocation biomeRegistryName, int biomeGenerationWeight, boolean generateBiome, boolean generateDefaultBiomeFeatures)
    {
        super(ForgeRegistries.BIOMES.getValue(biomeRegistryName), biomeGenerationWeight, generateBiome, generateDefaultBiomeFeatures);
    }

    public BiomeDataConfigurable(ResourceLocation biomeRegistryName)
    {
        super(biomeRegistryName);
    }

    public BiomeDataConfigurable(Biome biome)
    {
        super(biome);
    }

    @Override
    public void readFromConfig(Config config)
    {
        this.biomeGenerationWeight = config.getOrElse("biomeGenerationWeight", this.biomeGenerationWeight);

        if(config.get("blocks") instanceof Config)
        {
            Config blocks = config.get("blocks");
            this.blocks.clear();

            for(Config.Entry entry : blocks.entrySet())
            {
                IBlockState state = ConfigHelper.getBlockState(config, "blocks." + entry.getKey());

                if(state != null)
                {
                    this.blocks.put(entry.getKey(), state);
                }
            }
        }

        if(config.get("entities") instanceof List)
        {
            List<Config> entities = new ArrayList<>();
            Iterator entityConfigIter = ((List) config.get("entities")).iterator();
            this.entities.clear();

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

                    Config entityConfig = ModJsonConfigFormat.newConfig();
                    entityConfig.add("entity", ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString());
                    entityConfig.add("biomeGenerationWeight", entry.itemWeight);
                    entityConfig.add("minGroupCount", entry.minGroupCount);
                    entityConfig.add("maxGroupCount", entry.maxGroupCount);
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
                        this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) cls, config.getOrElse("biomeGenerationWeight", 10), config.getOrElse("minGroupCount", 1), config.getOrElse("maxGroupCount", 4)));
                    }
                }
            }
        }

        if(config.get("traits") instanceof List)
        {
            List<Config> features = new ArrayList<>();
            List<Config> featureConfigs = config.get("traits");
            this.features.clear();

            for(Config featureConfig : featureConfigs)
            {
                IBiomeTraitConfigurable feature = BiomeTraitRegistry.createFeature(new ResourceLocation(featureConfig.get("trait")), featureConfig);

                if(feature != null && config.getOrElse("generate", true))
                {
                    GenerationStage generationStage = featureConfig.getEnumOrElse("generationStage", GenerationStage.DECORATE);

                    if(generationStage != null)
                    {
                        this.features.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(feature);
                    }
                    else
                    {
                        this.features.computeIfAbsent(GenerationStage.POST_DECORATE, k -> new ArrayList<>()).add(feature);
                    }
                }

                features.add(featureConfig);
            }

            config.set("traits", features);
        }

        this.generateBiome = config.getOrElse("generateBiome", true);
        this.generateDefaultBiomeFeatures = config.getOrElse("generateDefaultBiomeFeatures", true);
    }

    @Override
    public void writeToConfig(Config config)
    {
        config.add("biome", this.biome.getRegistryName().toString());
        config.add("biomeGenerationWeight", this.biomeGenerationWeight);
        config.add("generateBiome", this.generateBiome);
        config.add("generateDefaultBiomeFeatures", this.generateDefaultBiomeFeatures);
        Config blockConfigs = ModJsonConfigFormat.newConfig();

        for(Map.Entry<String, IBlockState> entry : this.getBiomeBlocks().entrySet())
        {
            ConfigHelper.setBlockState(blockConfigs, entry.getKey(), entry.getValue());
        }

        config.add("blocks", blockConfigs);
        List<Config> entityConfigs = new ArrayList<>();

        for(EnumCreatureType type : EnumCreatureType.values())
        {
            for(Biome.SpawnListEntry entry : this.getBiomeEntities(type))
            {
                ResourceLocation entityRegistryName = EntityList.getKey(entry.entityClass);

                if(entityRegistryName != null)
                {
                    Config entityConfig = ModJsonConfigFormat.newConfig();
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

        for(GenerationStage stage : GenerationStage.values())
        {
            for(IBiomeTrait biomeTrait : this.getBiomeTraits(stage))
            {
                if(!(biomeTrait instanceof IBiomeTraitConfigurable))
                {
                    continue;
                }

                Config biomeTraitConfig = ModJsonConfigFormat.newConfig();
                ((IBiomeTraitConfigurable) biomeTrait).writeToConfig(biomeTraitConfig);
                biomeTraitConfig.add("generationStage", stage.toString().toLowerCase());
                biomeTraitConfigs.add(biomeTraitConfig);
            }
        }

        config.add("traits", biomeTraitConfigs);
    }

    @Override
    public String getRelativeSaveFile()
    {
        return "config/netherex/biomes/" + this.biome.getRegistryName().toString().replace(":", "/") + ".json";
    }
}
