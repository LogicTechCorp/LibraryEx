/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lex.config.IConfig;
import lex.world.gen.GenerationStage;
import lex.world.gen.feature.FeatureRegistry;
import lex.world.gen.feature.IFeature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static lex.util.ConfigHelper.isString;

public class BiomeWrapper implements IBiomeWrapper
{
    private Biome biome;
    private int weight;
    private Map<String, IBlockState> blocks = new HashMap<>();
    private Map<EnumCreatureType, List<Biome.SpawnListEntry>> spawnListEntries = new HashMap<>();
    private Map<GenerationStage, List<IFeature>> generationStageFeatures = new HashMap<>();
    private IConfig config;

    public BiomeWrapper(IConfig configIn)
    {
        biome = ForgeRegistries.BIOMES.getValue(configIn.getResource("biome"));
        config = configIn;
        parse();
    }

    protected void parse()
    {
        weight = config.getInt("weight", 10);
        IConfig blockConfig = config.getInnerConfig("blocks", new JsonObject());
        List<IConfig> entityConfigs = config.getInnerConfigs("entities", new ArrayList<>());
        List<IConfig> featureConfigs = config.getInnerConfigs("features", new ArrayList<>());
        blockConfig.getBlock("topBlock", biome.topBlock);
        blockConfig.getBlock("fillerBlock", biome.fillerBlock);

        for(Map.Entry<String, JsonElement> entry : blockConfig.getElements().entrySet())
        {
            if(blockConfig.getBlock(entry.getKey()) != null)
            {
                blocks.put(entry.getKey(), blockConfig.getBlock(entry.getKey()));
            }
        }

        List<JsonObject> entityObjects = new ArrayList<>();

        for(EnumCreatureType creatureType : EnumCreatureType.values())
        {
            entryLoop:
            for(Biome.SpawnListEntry entry : biome.getSpawnableList(creatureType))
            {
                ResourceLocation entityName = ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass));
                boolean containsEntry = false;

                ListIterator<IConfig> configIter = entityConfigs.listIterator();

                while(configIter.hasNext())
                {
                    IConfig entityConfig = configIter.next();

                    if(entityName != null && entityConfig.getString("entity").equals(entityName.toString()))
                    {
                        containsEntry = true;
                    }

                    entityObjects.add(entityConfig.compose().getAsJsonObject());
                    configIter.remove();

                    if(containsEntry)
                    {
                        continue entryLoop;
                    }
                }

                JsonObject entityObject = new JsonObject();
                entityObject.addProperty("entity", ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString());
                entityObject.addProperty("creatureType", creatureType.toString().toLowerCase());
                entityObject.addProperty("weight", entry.itemWeight);
                entityObject.addProperty("minGroupCount", entry.minGroupCount);
                entityObject.addProperty("maxGroupCount", entry.maxGroupCount);
                entityObject.addProperty("spawn", true);
                entityObjects.add(entityObject);
            }
        }

        config.remove("entities");
        entityConfigs = config.getInnerConfigs("entities", entityObjects);

        for(IConfig entityConfig : entityConfigs)
        {
            if(isString(entityConfig.get("entity")) && isString(entityConfig.get("creatureType")) && entityConfig.getBoolean("spawn", true))
            {
                String entity = entityConfig.getString("entity");

                if(!Strings.isBlank(entity) && !Strings.isBlank(entityConfig.get("creatureType").getAsJsonPrimitive().getAsString()))
                {
                    Class<? extends Entity> entityCls = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entity)).getEntityClass();

                    if(entityCls != null && EntityLiving.class.isAssignableFrom(entityCls))
                    {
                        EnumCreatureType creatureType = entityConfig.getEnum("creatureType", EnumCreatureType.class);

                        if(creatureType != null)
                        {
                            int weight = entityConfig.getInt("weight", 10);
                            int minGroupCount = entityConfig.getInt("minGroupCount", 1);
                            int maxGroupCount = entityConfig.getInt("maxGroupCount", 4);
                            spawnListEntries.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) entityCls, weight, minGroupCount, maxGroupCount));
                        }
                    }
                }
            }
        }

        List<JsonObject> featureObjects = new ArrayList<>();

        for(IConfig featureConfig : featureConfigs)
        {
            if(isString(featureConfig.get("feature")) && isString(featureConfig.get("generationStage")))
            {
                IFeature feature = FeatureRegistry.createFeature(featureConfig.getResource("feature"), featureConfig);
                GenerationStage generationStage = featureConfig.getEnum("generationStage", GenerationStage.class, GenerationStage.POST_DECORATE);

                if(feature != null && featureConfig.getBoolean("generate", true))
                {
                    generationStageFeatures.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(feature);
                }
            }

            featureObjects.add(featureConfig.compose().getAsJsonObject());
        }

        config.remove("features");
        config.getInnerConfigs("features", featureObjects);
    }

    @Override
    public Biome getBiome()
    {
        return biome;
    }

    @Override
    public int getWeight()
    {
        return weight;
    }

    @Override
    public IBlockState getBlock(String key, IBlockState fallbackValue)
    {
        IBlockState value = getBlock(key);

        if(value == null)
        {
            config.getInnerConfig("blocks").getBlock(key, fallbackValue);
            blocks.put(key, fallbackValue);
            return fallbackValue;
        }

        return value;
    }

    @Override
    public IBlockState getBlock(String key)
    {
        return blocks.get(key);
    }

    @Override
    public List<IBlockState> getBlocks()
    {
        return ImmutableList.copyOf(blocks.values());
    }

    @Override
    public List<Biome.SpawnListEntry> getSpawnListEntries(EnumCreatureType creatureType)
    {
        return ImmutableList.copyOf(spawnListEntries.computeIfAbsent(creatureType, k -> new ArrayList<>()));
    }

    @Override
    public List<IFeature> getFeatures(GenerationStage generationStage)
    {
        return ImmutableList.copyOf(generationStageFeatures.computeIfAbsent(generationStage, k -> new ArrayList<>()));
    }

    @Override
    public IConfig getConfig()
    {
        return config;
    }
}
