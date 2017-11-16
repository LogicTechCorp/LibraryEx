package lex.biome;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lex.config.JsonConfig;
import lex.util.JsonUtils;
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

    public WrappedBiome(Biome biomeIn, JsonConfig config)
    {
        biome = biomeIn;
        configure(config);
    }

    private void configure(JsonConfig config)
    {
        topBlock = config.getBlock("topBlock", biome.topBlock);
        fillerBlock = config.getBlock("fillerBlock", biome.fillerBlock);
        caveCeilingBlock = config.getBlock("caveCeilingBlock", biome.fillerBlock);
        caveWallBlock = config.getBlock("caveWallBlock", biome.fillerBlock);
        caveFloorBlock = config.getBlock("caveFloorBlock", biome.fillerBlock);
        oceanBlock = config.getBlock("oceanBlock", biome.fillerBlock);
        weight = config.getInt("weight", weight);

        JsonConfig entitiesConfig = config.getSubConfig("entities");

        if(entitiesConfig.getAll().size() == 0)
        {
            for(EnumCreatureType creatureType : EnumCreatureType.values())
            {
                for(Biome.SpawnListEntry entry : biome.getSpawnableList(creatureType))
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("creatureType", creatureType.toString().toLowerCase());
                    object.addProperty("weight", entry.itemWeight);
                    object.addProperty("minGroupCount", entry.minGroupCount);
                    object.addProperty("maxGroupCount", entry.maxGroupCount);
                    entitiesConfig.add(ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString(), object);
                }
            }
        }

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

                            int weight = entityConfig.getInt("weight", 10);
                            int minGroupCount = entityConfig.getInt("minGroupCount", 1);
                            int maxGroupCount = entityConfig.getInt("maxGroupCount", 4);

                            getSpawnableList(creatureType).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) entityCls, weight, minGroupCount, maxGroupCount));
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
