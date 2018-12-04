package logictechcorp.libraryex.world.biome.wrapper;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.config.IConfigData;
import logictechcorp.libraryex.util.ConfigHelper;
import logictechcorp.libraryex.world.gen.GenerationStage;
import logictechcorp.libraryex.world.gen.feature.Feature;
import logictechcorp.libraryex.world.gen.feature.FeatureRegistry;
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

import java.io.File;
import java.util.*;

public abstract class BiomeWrapper implements IBiomeWrapper, IConfigData
{
    protected Biome biome;
    protected int weight;
    protected boolean enabled;
    protected boolean genDefaultFeatures;
    protected Map<String, IBlockState> blocks;
    protected Map<EnumCreatureType, List<Biome.SpawnListEntry>> entities;
    protected Map<GenerationStage, List<Feature>> features;

    public BiomeWrapper(ResourceLocation biomeRegistryName, int weight, boolean enabled, boolean genDefaultFeatures)
    {
        this.biome = ForgeRegistries.BIOMES.getValue(biomeRegistryName);
        this.weight = weight;
        this.enabled = enabled;
        this.genDefaultFeatures = genDefaultFeatures;
        this.blocks = new HashMap<>();
        this.entities = new HashMap<>();
        this.features = new HashMap<>();
    }

    public BiomeWrapper()
    {
        this.biome = Biomes.PLAINS;
        this.weight = 10;
        this.enabled = true;
        this.genDefaultFeatures = true;
        this.blocks = new HashMap<>();
        this.entities = new HashMap<>();
        this.features = new HashMap<>();
    }

    @Override
    public void deserialize(FileConfig config)
    {
        if(config != null)
        {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("biome"));

            if(biome != null)
            {
                this.biome = biome;
                this.weight = config.getOrElse("weight", this.weight);

                if(config.get("blocks") instanceof Config)
                {
                    ConfigHelper.rename(config, "blocks.topBlock", "blocks.floorTopBlock");
                    ConfigHelper.rename(config, "blocks.fillerBlock", "blocks.floorFillerBlock");
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
                    List<Config> entityConfigs = config.get("entities");
                    Iterator<Config> entityConfigIter = entityConfigs.iterator();
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
                                Config entityConfig = entityConfigIter.next();

                                if(registryKey != null && entityConfig.contains("entity") && entityConfig.get("entity") instanceof String && ((String) entityConfig.get("entity")).equalsIgnoreCase(registryKey.toString()))
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

                            Config entityConfig = JsonFormat.newConcurrentConfig();
                            entityConfig.add("entity", ForgeRegistries.ENTITIES.getKey(EntityRegistry.getEntry(entry.entityClass)).toString());
                            entityConfig.add("creatureType", type.toString().toLowerCase());
                            entityConfig.add("weight", entry.itemWeight);
                            entityConfig.add("minGroupCount", entry.minGroupCount);
                            entityConfig.add("maxGroupCount", entry.maxGroupCount);
                            entityConfig.add("spawn", true);
                            entities.add(entityConfig);
                        }
                    }

                    config.set("entities", entities);

                    for(Config entityConfig : entities)
                    {
                        EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("entity"));

                        if(entityEntry != null && config.getOrElse("spawn", true))
                        {
                            Class<? extends Entity> cls = entityEntry.getEntityClass();
                            EnumCreatureType creatureType = ConfigHelper.getEnum(entityConfig, "creatureType", EnumCreatureType.class);

                            if(creatureType != null && EntityLiving.class.isAssignableFrom(cls))
                            {
                                this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>()).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) cls, config.getOrElse("weight", 10), config.getOrElse("minGroupCount", 1), config.getOrElse("maxGroupCount", 4)));
                            }
                        }
                    }
                }

                if(config.get("features") instanceof List)
                {
                    List<Config> features = new ArrayList<>();
                    List<Config> featureConfigs = config.get("features");
                    this.features.clear();

                    for(Config featureConfig : featureConfigs)
                    {
                        Feature feature = FeatureRegistry.createFeature(new ResourceLocation("feature"), featureConfig);

                        if(feature != null && config.getOrElse("generate", true))
                        {
                            GenerationStage generationStage = ConfigHelper.getEnum(featureConfig, "genStage", GenerationStage.class);

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

                    config.set("features", features);
                }

                this.enabled = config.getOrElse("enabled", true);
                this.genDefaultFeatures = config.getOrElse("genDefaultFeatures", true);
            }
        }
    }

    @Override
    public FileConfig serialize()
    {
        File configFile = this.getSaveFile();

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
                ConfigHelper.setBlockState(blockConfigs, entry.getKey(), entry.getValue());
            }

            config.add("blocks", blockConfigs);
            List<Config> entityConfigs = new ArrayList<>();

            for(EnumCreatureType type : EnumCreatureType.values())
            {
                for(Biome.SpawnListEntry entry : this.getEntitySpawnEntries(type))
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

    @Override
    public Biome getBiome()
    {
        return this.biome;
    }

    @Override
    public int getWeight()
    {
        return this.weight;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public boolean genDefaultFeatures()
    {
        return this.genDefaultFeatures;
    }

    @Override
    public IBlockState getBiomeBlock(BiomeBlockType type, IBlockState fallback)
    {
        IBlockState value = this.blocks.get(type.getIdentifier());

        if(value == null)
        {
            this.blocks.put(type.getIdentifier(), fallback);
            return fallback;
        }

        return value;
    }

    public Map<String, IBlockState> getBlocks()
    {
        return this.blocks;
    }

    @Override
    public List<Biome.SpawnListEntry> getEntitySpawnEntries(EnumCreatureType creatureType)
    {
        return this.entities.computeIfAbsent(creatureType, k -> new ArrayList<>());
    }

    @Override
    public List<Feature> getFeatures(GenerationStage generationStage)
    {
        return this.features.computeIfAbsent(generationStage, k -> new ArrayList<>());
    }
}
