package lex.world.biome;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.json.JsonFormat;
import lex.util.ConfigHelper;
import lex.world.gen.GenerationStage;
import lex.world.gen.feature.Feature;
import lex.world.gen.feature.FeatureRegistry;
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

import java.io.File;
import java.util.*;

public class BiomeConfigurations
{
    protected Biome biome;
    protected int weight;
    protected boolean enabled;
    protected boolean genDefaultFeatures;
    protected Map<String, IBlockState> blocks;
    protected Map<EnumCreatureType, List<Biome.SpawnListEntry>> entities;
    protected Map<GenerationStage, List<Feature>> features;

    public BiomeConfigurations(ResourceLocation biomeRegistryName, int weight, boolean enabled, boolean genDefaultFeatures, Map<String, IBlockState> blocks, Map<EnumCreatureType, List<Biome.SpawnListEntry>> entities, Map<GenerationStage, List<Feature>> features)
    {
        this.biome = ForgeRegistries.BIOMES.getValue(biomeRegistryName);
        this.weight = weight;
        this.enabled = enabled;
        this.genDefaultFeatures = genDefaultFeatures;
        this.blocks = blocks;
        this.entities = entities;
        this.features = features;
    }

    public BiomeConfigurations(FileConfig config)
    {
        this.blocks = new HashMap<>();
        this.entities = new HashMap<>();
        this.features = new HashMap<>();
        this.deserialize(config);
    }

    private void deserialize(FileConfig config)
    {
        if(config != null)
        {
            this.biome = ForgeRegistries.BIOMES.getValue(ConfigHelper.getOrSetResourceLocation(config, "biome", null));

            if(this.biome != null)
            {
                this.weight = ConfigHelper.getOrSet(config, "weight", 10);

                if(!this.biome.getRegistryName().getNamespace().equalsIgnoreCase("biomesoplenty"))
                {
                    ConfigHelper.getOrSetBlockState(config, "blocks.topBlock", this.biome.topBlock);
                    ConfigHelper.getOrSetBlockState(config, "blocks.fillerBlock", this.biome.fillerBlock);
                    Config blocks = ConfigHelper.getOrSet(config, "blocks", null);

                    for(Config.Entry entry : blocks.entrySet())
                    {
                        IBlockState state = ConfigHelper.getOrSetBlockState(config, "blocks." + entry.getKey(), null);

                        if(state != null)
                        {
                            this.blocks.put(entry.getKey(), state);
                        }
                    }
                }

                List<Config> entityConfigs = new ArrayList<>();

                for(EnumCreatureType type : EnumCreatureType.values())
                {
                    entryLoop:
                    for(Biome.SpawnListEntry entry : this.biome.getSpawnableList(type))
                    {
                        ResourceLocation registryName = ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass));
                        boolean containsEntry = false;

                        Iterator<Config> configIter = ConfigHelper.getOrSet(config, "entities", new ArrayList<Config>()).iterator();

                        while(configIter.hasNext())
                        {
                            Config entityConfig = configIter.next();

                            if(registryName != null && entityConfig.contains("entity") && entityConfig.get("entity") instanceof String && ((String) entityConfig.get("entity")).equalsIgnoreCase(registryName.toString()))
                            {
                                containsEntry = true;
                            }

                            entityConfigs.add(entityConfig);
                            configIter.remove();

                            if(containsEntry)
                            {
                                continue entryLoop;
                            }
                        }

                        Config entityConfig = JsonFormat.newConcurrentConfig();
                        entityConfig.add("entity", ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString());
                        entityConfig.add("creatureType", type.toString().toLowerCase());
                        entityConfig.add("weight", entry.itemWeight);
                        entityConfig.add("minGroupCount", entry.minGroupCount);
                        entityConfig.add("maxGroupCount", entry.maxGroupCount);
                        entityConfig.add("spawn", true);
                        entityConfigs.add(entityConfig);
                    }
                }

                config.remove("entities");
                ConfigHelper.getOrSet(config, "entities", entityConfigs);

                for(Config entityConfig : entityConfigs)
                {
                    EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(ConfigHelper.getOrSetResourceLocation(entityConfig, "entity", null));

                    if(entityEntry != null && ConfigHelper.getOrSet(entityConfig, "spawn", true))
                    {
                        Class<? extends Entity> cls = entityEntry.getEntityClass();
                        EnumCreatureType creatureType = ConfigHelper.getOrSetEnum(entityConfig, "creatureType", EnumCreatureType.class, null);

                        if(creatureType != null && EntityLiving.class.isAssignableFrom(cls))
                        {
                            this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) cls, ConfigHelper.getOrSet(entityConfig, "weight", 10), ConfigHelper.getOrSet(entityConfig, "minGroupCount", 1), ConfigHelper.getOrSet(entityConfig, "maxGroupCount", 4)));
                        }
                    }
                }
            }

            List<Config> featureConfigs = new ArrayList<>();

            for(Config featureConfig : ConfigHelper.getOrSet(config, "features", new ArrayList<Config>()))
            {
                Feature feature = FeatureRegistry.createFeature(ConfigHelper.getOrSetResourceLocation(featureConfig, "feature", null), featureConfig);

                if(feature != null && ConfigHelper.getOrSet(featureConfig, "generate", true))
                {
                    GenerationStage generationStage = ConfigHelper.getOrSetEnum(featureConfig, "genStage", GenerationStage.class, GenerationStage.POST_DECORATE);
                    this.features.computeIfAbsent(generationStage, k -> new ArrayList<>()).add(feature);
                }

                featureConfigs.add(featureConfig);
            }

            config.remove("features");
            ConfigHelper.getOrSet(config, "features", featureConfigs);
            this.enabled = ConfigHelper.getOrSet(config, "enabled", true);
            this.genDefaultFeatures = ConfigHelper.getOrSet(config, "genDefaultFeatures", true);
        }
    }

    public FileConfig serialize(File configFile)
    {
        if(!configFile.exists() && configFile.getParentFile().mkdirs() || !configFile.exists())
        {
            FileConfig config = FileConfig.of(configFile);

            config.add("biome", this.biome.getRegistryName().toString());
            config.add("weight", this.weight);
            config.add("enabled", this.enabled);
            config.add("genDefaultFeatures", this.genDefaultFeatures);
            Config blockConfigs = JsonFormat.newConcurrentConfig();

            for(Map.Entry<String, IBlockState> entry : this.getBlocks().entrySet())
            {
                ConfigHelper.getOrSetBlockState(blockConfigs, entry.getKey(), entry.getValue());
            }

            config.add("blocks", blockConfigs);
            List<Config> entityConfigs = new ArrayList<>();

            for(EnumCreatureType type : EnumCreatureType.values())
            {
                for(Biome.SpawnListEntry entry : this.getEntities(type))
                {
                    ResourceLocation entityRegistryName = EntityList.getKey(entry.entityClass);

                    if(entityRegistryName != null)
                    {
                        Config entityConfig = JsonFormat.newConcurrentConfig();
                        entityConfig.add("entity", entityRegistryName.toString());
                        entityConfig.add("weight", entry.itemWeight);
                        entityConfig.add("creatureType", type.toString().toLowerCase());
                        entityConfig.add("minGroupCount", entry.minGroupCount);
                        entityConfig.add("maxGroupCount", entry.maxGroupCount);
                        entityConfig.add("spawn", true);
                        entityConfigs.add(entityConfig);
                    }
                }
            }

            config.add("entities", entityConfigs);
            List<Config> featureConfigs = new ArrayList<>();

            for(GenerationStage stage : GenerationStage.values())
            {
                for(Feature feature : this.getFeatures(stage))
                {
                    Config featureConfig = feature.serialize();
                    featureConfig.add("genStage", stage.toString().toLowerCase());
                    featureConfigs.add(featureConfig);
                }
            }

            config.add("features", featureConfigs);
            return config;
        }
        else
        {
            FileConfig config = FileConfig.of(configFile);
            config.load();
            this.deserialize(config);
            return config;
        }
    }

    public Biome getBiome()
    {
        return this.biome;
    }

    public int getWeight()
    {
        return this.weight;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public boolean shouldGenDefaultFeatures()
    {
        return this.genDefaultFeatures;
    }

    public IBlockState getBlock(String key, IBlockState fallback)
    {
        IBlockState value = this.blocks.get(key);

        if(value == null)
        {
            this.blocks.put(key, fallback);
            return fallback;
        }

        return value;
    }

    public Map<String, IBlockState> getBlocks()
    {
        return this.blocks;
    }

    public List<Biome.SpawnListEntry> getEntities(EnumCreatureType creatureType)
    {
        return this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>());
    }

    public List<Feature> getFeatures(GenerationStage generationStage)
    {
        return this.features.computeIfAbsent(generationStage, k -> new ArrayList<>());
    }
}
