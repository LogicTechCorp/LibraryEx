package lex.biome;

import com.google.gson.JsonElement;
import lex.config.JsonConfig;
import lex.util.JsonUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;

public class WrappedBiome
{
    private Biome biome;
    private BiomeType biomeType;
    private IBlockState topBlock;
    private IBlockState fillerBlock;
    private IBlockState caveCeilingBlock;
    private IBlockState caveWallBlock;
    private IBlockState caveFloorBlock;
    private IBlockState oceanBlock;
    private int weight;

    public WrappedBiome(Biome biomeIn, BiomeType biomeTypeIn, JsonConfig config)
    {
        biome = biomeIn;
        biomeType = biomeTypeIn;
        configure(config);
    }

    private void configure(JsonConfig config)
    {
        topBlock = config.getBlock("topBlock", biome.topBlock);
        fillerBlock = config.getBlock("fillerBlock", biome.fillerBlock);
        caveCeilingBlock = config.getBlock("caveCeilingBlock", biomeType.getAssociatedLandBlock());
        caveWallBlock = config.getBlock("caveWallBlock", biomeType.getAssociatedLandBlock());
        caveFloorBlock = config.getBlock("caveFloorBlock", biomeType.getAssociatedLandBlock());
        oceanBlock = config.getBlock("oceanBlock", biomeType.getAssociatedLiquidBlock());
        weight = config.getInt("weight", weight);

        JsonConfig entitiesConfig = config.getSubConfig("entities");

        for(Map.Entry<String, JsonElement> entry : entitiesConfig.getAll().entrySet())
        {
            if(entry.getValue().isJsonObject())
            {
                String entityName = entry.getKey();
                JsonConfig entityConfig = entitiesConfig.getSubConfig(entityName);

                if(entityConfig.has("creatureType") && JsonUtils.isString(entityConfig.get("creatureType")))
                {
                    String creatureTypeIdentifier = entityConfig.get("creatureType").getAsJsonPrimitive().getAsString();

                    if(!Strings.isBlank(entityName) && !Strings.isBlank(creatureTypeIdentifier))
                    {
                        Class<? extends Entity> entityCls = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityName)).getEntityClass();

                        if(entityCls != null && EntityLiving.class.isAssignableFrom(entityCls))
                        {
                            EnumCreatureType creatureType = entityConfig.getEnum("creatureType", EnumCreatureType.class, EnumCreatureType.CREATURE);

                            int weight = entitiesConfig.getInt("weight", 10);
                            int minGroupCount = entitiesConfig.getInt("minGroupCount", 1);
                            int maxGroupCount = entitiesConfig.getInt("maxGroupCount", 4);

                            biome.getSpawnableList(creatureType).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) entityCls, weight, minGroupCount, maxGroupCount));
                        }
                        else
                        {
                            entitiesConfig.getLogger().warn("The {} config's entity class is null or not of the type EntityLiving", entitiesConfig.getName());
                        }
                    }
                    else
                    {
                        entitiesConfig.getLogger().warn("The {} config's entity name or creatureType is blank", entitiesConfig.getName());
                    }
                }
                else
                {
                    entitiesConfig.getLogger().warn("The {} config's entity creatureType is missing", entitiesConfig.getName());
                }
            }
        }

        config.save();
    }

    public Biome getBiome()
    {
        return biome;
    }

    public BiomeType getBiomeType()
    {
        return biomeType;
    }

    public IBlockState getTopBlock()
    {
        return topBlock;
    }

    public IBlockState getFillerBlock()
    {
        return fillerBlock;
    }

    public IBlockState getCaveCeilingBlock()
    {
        return caveCeilingBlock;
    }

    public IBlockState getCaveWallBlock()
    {
        return caveWallBlock;
    }

    public IBlockState getCaveFloorBlock()
    {
        return caveFloorBlock;
    }

    public IBlockState getOceanBlock()
    {
        return oceanBlock;
    }

    public int getWeight()
    {
        return weight;
    }
}
