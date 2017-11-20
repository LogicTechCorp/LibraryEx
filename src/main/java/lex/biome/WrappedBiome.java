package lex.biome;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lex.config.Config;
import lex.config.ConfigFactory;
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
import java.util.List;
import java.util.Map;

import static lex.util.JsonUtils.isObject;
import static lex.util.JsonUtils.isString;

public class WrappedBiome
{
    private Biome biome;
    private IBlockState topBlock;
    private IBlockState fillerBlock;
    private IBlockState caveCeilingBlock;
    private IBlockState caveWallBlock;
    private IBlockState caveFloorBlock;
    private IBlockState oceanBlock;
    private int weight;
    private List<Biome.SpawnListEntry> spawnableMonsters = new ArrayList<>();
    private List<Biome.SpawnListEntry> spawnableCreatures = new ArrayList<>();
    private List<Biome.SpawnListEntry> spawnableWaterCreatures = new ArrayList<>();
    private List<Biome.SpawnListEntry> spawnableCaveCreatures = new ArrayList<>();

    WrappedBiome(Biome biomeIn, Config config)
    {
        biome = biomeIn;
        configure(config);
    }

    protected void configure(Config config)
    {
        topBlock = config.getBlock("topBlock", biome.topBlock);
        fillerBlock = config.getBlock("fillerBlock", biome.fillerBlock);
        caveCeilingBlock = config.getBlock("caveCeilingBlock", biome.fillerBlock);
        caveWallBlock = config.getBlock("caveWallBlock", biome.fillerBlock);
        caveFloorBlock = config.getBlock("caveFloorBlock", biome.fillerBlock);
        oceanBlock = config.getBlock("oceanBlock", biome.fillerBlock);
        weight = config.getInt("weight", weight);

        Config entitiesConfig = config.getInnerConfig("entities");

        if(entitiesConfig == null)
        {
            JsonObject entitiesRootObject = new JsonObject();

            for(EnumCreatureType creatureType : EnumCreatureType.values())
            {
                for(Biome.SpawnListEntry entry : biome.getSpawnableList(creatureType))
                {
                    JsonObject entityObject = new JsonObject();
                    entityObject.addProperty("creatureType", creatureType.toString().toLowerCase());
                    entityObject.addProperty("weight", entry.itemWeight);
                    entityObject.addProperty("minGroupCount", entry.minGroupCount);
                    entityObject.addProperty("maxGroupCount", entry.maxGroupCount);
                    entitiesRootObject.add(ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString(), entityObject);
                }
            }

            entitiesConfig = config.getInnerConfig("entities", entitiesRootObject);
        }


        for(Map.Entry<String, JsonElement> entry : entitiesConfig.getElementMap().entrySet())
        {
            if(isObject(entry.getValue()))
            {
                String entityName = entry.getKey();
                Config entityConfig = entitiesConfig.getInnerConfig(entityName);

                if(entityConfig.has("creatureType") && isString(entityConfig.get("creatureType")))
                {
                    String creatureTypeIdentifier = entityConfig.get("creatureType").getAsJsonPrimitive().getAsString();

                    if(!Strings.isBlank(entityName) && !Strings.isBlank(creatureTypeIdentifier))
                    {
                        Class<? extends Entity> entityCls = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityName)).getEntityClass();

                        if(entityCls != null && EntityLiving.class.isAssignableFrom(entityCls))
                        {
                            EnumCreatureType creatureType = entityConfig.getEnum("creatureType", EnumCreatureType.class, EnumCreatureType.CREATURE);

                            int weight = entityConfig.getInt("weight", 10);
                            int minGroupCount = entityConfig.getInt("minGroupCount", 1);
                            int maxGroupCount = entityConfig.getInt("maxGroupCount", 4);

                            getSpawnableList(creatureType).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) entityCls, weight, minGroupCount, maxGroupCount));
                        }
                    }
                }
            }
        }

        ConfigFactory.saveConfig(config);
    }

    public Biome getBiome()
    {
        return biome;
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

    public List<Biome.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType)
    {
        switch(creatureType)
        {
            case MONSTER:
                return spawnableMonsters;
            case CREATURE:
                return spawnableCreatures;
            case WATER_CREATURE:
                return spawnableWaterCreatures;
            case AMBIENT:
                return spawnableCaveCreatures;
            default:
                return new ArrayList<>();
        }
    }
}
