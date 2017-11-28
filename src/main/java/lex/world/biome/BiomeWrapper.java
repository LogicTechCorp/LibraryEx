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
import lex.world.gen.feature.IFeature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lex.util.JsonUtils.isString;

public class BiomeWrapper
{
    private Biome biome;
    private IBlockState surfaceBlock;
    private IBlockState subsurfaceBlock;
    private IBlockState wallBlock;
    private IBlockState ceilingBlock;
    private IBlockState superCeilingBlock;
    private IBlockState fluidBlock;
    private int weight;
    private Map<DimensionType, Map<GenerationStage, List<IFeature>>> DIMENSION_FEATURE_MAP = new HashMap<>();

    BiomeWrapper(Biome biomeIn, IConfig config)
    {
        biome = biomeIn;
        configure(config);
    }

    protected void configure(IConfig config)
    {
        surfaceBlock = config.getBlock("surfaceBlock", biome.topBlock);
        subsurfaceBlock = config.getBlock("subsurfaceBlock", biome.fillerBlock);
        wallBlock = config.getBlock("wallBlock", biome.fillerBlock);
        ceilingBlock = config.getBlock("ceilingBlock", biome.fillerBlock);
        superCeilingBlock = config.getBlock("superCeilingBlock", biome.fillerBlock);
        fluidBlock = config.getBlock("fluidBlock", Blocks.WATER.getDefaultState());
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
            if(isString(featureConfig.get("generator")) && isString(featureConfig.get("generationStage")) && isString(featureConfig.get("dimension")))
            {
                //TODO: Obtain feature from a registry of some sort
                IFeature feature = null;
                GenerationStage generationStage = featureConfig.getEnum("generationStage", GenerationStage.class);
                DimensionType dimensionType = featureConfig.getEnum("dimension", DimensionType.class);

                if(feature != null && generationStage != null && dimensionType != null)
                {
                    feature.configure(featureConfig);
                    DIMENSION_FEATURE_MAP.computeIfAbsent(dimensionType, k -> new HashMap<>()).computeIfAbsent(generationStage, k -> new ArrayList<>()).add(feature);
                }
            }
        }
    }

    public Biome getBiome()
    {
        return biome;
    }

    public IBlockState getSurfaceBlock()
    {
        return surfaceBlock;
    }

    public IBlockState getSubsurfaceBlock()
    {
        return subsurfaceBlock;
    }

    public IBlockState getCeilingBlock()
    {
        return ceilingBlock;
    }

    public IBlockState getWallBlock()
    {
        return wallBlock;
    }

    public IBlockState getSuperCeilingBlock()
    {
        return superCeilingBlock;
    }

    public IBlockState getFluidBlock()
    {
        return fluidBlock;
    }

    public int getWeight()
    {
        return weight;
    }

    public List<IFeature> getFeatureList(DimensionType dimensionType, GenerationStage generationStage)
    {
        return ImmutableList.copyOf(DIMENSION_FEATURE_MAP.computeIfAbsent(dimensionType, k -> new HashMap<>()).computeIfAbsent(generationStage, k -> new ArrayList<>()));
    }
}
