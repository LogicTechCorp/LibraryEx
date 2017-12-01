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
import com.google.gson.JsonObject;
import lex.config.IConfig;
import lex.world.gen.GenerationStage;
import lex.world.gen.feature.FeatureManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lex.util.ConfigHelper.isString;

public abstract class AbstractBiomeWrapper implements IBiomeWrapper
{
    Biome biome;
    int weight;
    Map<String, IBlockState> blocks = new HashMap<>();
    Map<GenerationStage, List<IFeature>> generationStageFeatureMap = new HashMap<>();

    AbstractBiomeWrapper(AbstractBuilder builder)
    {
        biome = builder.biome;
        weight = builder.weight;
        blocks = builder.blocks;
        generationStageFeatureMap = builder.generationStageFeatureMap;
    }

    @Override
    public Biome getBiome()
    {
        return biome;
    }

    @Override
    public IBlockState getBlock(String key, IBlockState fallbackValue)
    {
        IBlockState value = getBlock(key);

        if(value == null)
        {
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
    public int getWeight()
    {
        return weight;
    }

    @Override
    public List<IFeature> getFeatureList(GenerationStage generationStage)
    {
        return ImmutableList.copyOf(generationStageFeatureMap.computeIfAbsent(generationStage, k -> new ArrayList<>()));
    }

    public abstract static class AbstractBuilder<B extends AbstractBuilder<B, F>, F extends IBiomeWrapper> implements IBiomeWrapperBuilder<B, F>
    {
        Biome biome;
        int weight;
        Map<String, IBlockState> blocks = new HashMap<>();
        Map<GenerationStage, List<IFeature>> generationStageFeatureMap = new HashMap<>();

        @Override
        public B configure(IConfig config)
        {
            weight = config.getInt("weight", 10);

            List<IConfig> entityConfigs = config.getInnerConfigs("entities");

            if(entityConfigs == null)
            {
                List<JsonObject> entityObjects = new ArrayList<>();

                for(EnumCreatureType creatureType : EnumCreatureType.values())
                {
                    for(Biome.SpawnListEntry entry : biome.getSpawnableList(creatureType))
                    {
                        JsonObject entityObject = new JsonObject();
                        entityObject.addProperty("name", ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString());
                        entityObject.addProperty("creatureType", creatureType.toString().toLowerCase());
                        entityObject.addProperty("weight", entry.itemWeight);
                        entityObject.addProperty("minGroupCount", entry.minGroupCount);
                        entityObject.addProperty("maxGroupCount", entry.maxGroupCount);
                        entityObjects.add(entityObject);
                    }
                }

                entityConfigs = config.getInnerConfigs("entities", entityObjects);
            }

            for(IConfig entityConfig : entityConfigs)
            {
                if(isString(entityConfig.get("name")) && isString(entityConfig.get("creatureType")))
                {
                    String entityName = entityConfig.getString("name");
                    String creatureTypeIdentifier = entityConfig.get("creatureType").getAsJsonPrimitive().getAsString();

                    if(!Strings.isBlank(entityName) && !Strings.isBlank(creatureTypeIdentifier))
                    {
                        Class<? extends Entity> entityCls = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityName)).getEntityClass();

                        if(entityCls != null && EntityLiving.class.isAssignableFrom(entityCls))
                        {
                            EnumCreatureType creatureType = entityConfig.getEnum("creatureType", EnumCreatureType.class);

                            if(creatureType != null)
                            {
                                int weight = entityConfig.getInt("weight", 10);
                                int minGroupCount = entityConfig.getInt("minGroupCount", 1);
                                int maxGroupCount = entityConfig.getInt("maxGroupCount", 4);
                                biome.getSpawnableList(creatureType).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) entityCls, weight, minGroupCount, maxGroupCount));
                            }
                        }
                    }
                }
            }

            List<IConfig> featureConfigs = config.getInnerConfigs("features");

            if(featureConfigs == null)
            {
                featureConfigs = config.getInnerConfigs("features", new ArrayList<>());
            }

            for(IConfig featureConfig : featureConfigs)
            {
                if(isString(featureConfig.get("generator")) && isString(featureConfig.get("generationStage")))
                {
                    IFeature feature = FeatureManager.createFeature(featureConfig.getString("generator"), featureConfig);
                    GenerationStage generationStage = featureConfig.getEnum("generationStage", GenerationStage.class);

                    if(feature != null && generationStage != null)
                    {
                        generationStageFeatureMap.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(feature);
                    }
                }
            }

            return (B) this;
        }
    }
}
