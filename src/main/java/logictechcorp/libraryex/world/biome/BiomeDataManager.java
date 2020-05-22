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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.realmsclient.util.JsonUtils;
import logictechcorp.libraryex.LibraryEx;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BiomeDataManager extends ReloadListener<Map<ResourceLocation, JsonObject>>
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String folderName;
    private final Map<ResourceLocation, BiomeData> biomeData;
    private final Map<ResourceLocation, List<String>> subBiomeData;
    private final Map<ResourceLocation, BiomeManager.BiomeEntry> biomeEntries;

    public BiomeDataManager()
    {
        this("biomes");
    }

    public BiomeDataManager(String folderName)
    {
        this.folderName = folderName;
        this.biomeData = new HashMap<>();
        this.subBiomeData = new HashMap<>();
        this.biomeEntries = new HashMap<>();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> locations, IResourceManager resourceManager, IProfiler profiler)
    {
        locations.forEach(((resourceLocation, object) ->
        {
            try
            {
                if(!resourceLocation.getPath().startsWith(this.folderName))
                {
                    resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), this.folderName + "/" + resourceLocation.getPath() + ".json");
                }

                IResource resource = resourceManager.getResource(resourceLocation);
                InputStream inputStream = resource.getInputStream();
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Dynamic<JsonElement> rootDynamic = new Dynamic<>(JsonOps.INSTANCE, JSONUtils.fromJson(GSON, reader, JsonObject.class));
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(resource);

                if(rootDynamic.getValue() == null)
                {
                    LibraryEx.LOGGER.error("Couldn't load {} biome config from {} data pack.", resource.getLocation(), resource.getPackName());
                }
                else
                {
                    Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(rootDynamic.get("biome").asString("")));

                    if(biome != null)
                    {
                        ResourceLocation biomeName = biome.getRegistryName();
                        int generationWeight = rootDynamic.get("generation_weight").asInt(10);

                        if(generationWeight > 0)
                        {
                            JsonObject rootObject = rootDynamic.getValue().getAsJsonObject();
                            boolean useDefaultEntities = JSONUtils.getBoolean(rootObject, "use_default_entities", true);
                            boolean useDefaultCarvers = JSONUtils.getBoolean(rootObject, "use_default_carvers", true);
                            boolean useDefaultFeatures = JSONUtils.getBoolean(rootObject, "use_default_features", true);
                            boolean useDefaultStructures = JSONUtils.getBoolean(rootObject, "use_default_structures", true);
                            boolean isSubBiome = rootDynamic.getValue().getAsJsonObject().get("is_sub_biome").getAsBoolean();
                            Map<BiomeData.BlockType, BlockState> blocks = rootDynamic.get("blocks").asMap(BiomeData.BlockType::deserialize, BlockState::deserialize);
                            List<Biome.SpawnListEntry> entities = rootDynamic.get("entities").asList(entityDynamic ->
                            {
                                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityDynamic.get("type").asString("minecraft:pig")));
                                int spawnWeight = entityDynamic.get("spawn_weight").asInt(10);
                                int minimumGroupCount = entityDynamic.get("minimum_group_count").asInt(1);
                                int maximumGroupCount = entityDynamic.get("maximum_group_count").asInt(4);
                                return new Biome.SpawnListEntry(entityType, spawnWeight, minimumGroupCount, maximumGroupCount);
                            });
                            Map<Dynamic<?>, ConfiguredCarver<?>> carvers = rootDynamic.get("carvers").asStream().collect(Collectors.toMap(Function.identity(), carversDynamic ->
                            {
                                WorldCarver<ICarverConfig> configuredCarver = (WorldCarver<ICarverConfig>) Registry.CARVER.getOrDefault(new ResourceLocation(carversDynamic.get("carver").orElseEmptyMap().get("name").asString("")));
                                ProbabilityConfig carverConfig = ProbabilityConfig.deserialize(carversDynamic.get("decorator").orElseEmptyMap().get("config").orElseEmptyMap());
                                return new ConfiguredCarver<>(configuredCarver, carverConfig);
                            }));
                            Map<Dynamic<?>, ConfiguredFeature<?, ?>> features = rootDynamic.get("features").asStream().collect(Collectors.toMap(Function.identity(), featuresDynamic ->
                            {
                                ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(featuresDynamic.get("feature").orElseEmptyMap());
                                ConfiguredPlacement<?> configuredPlacement = ConfiguredPlacement.deserialize(featuresDynamic.get("decorator").orElseEmptyMap());
                                return new ConfiguredFeature<>(Feature.DECORATED, new DecoratedFeatureConfig(configuredFeature, configuredPlacement));
                            }));
                            Map<Dynamic<?>, ConfiguredFeature<?, ?>> structures = rootDynamic.get("structures").asStream().collect(Collectors.toMap(Function.identity(), structuresDynamic ->
                            {
                                ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(structuresDynamic.get("structure").orElseEmptyMap());
                                ConfiguredPlacement<?> configuredPlacement = ConfiguredPlacement.deserialize(structuresDynamic.get("decorator").orElseEmptyMap());
                                return new ConfiguredFeature<>(Feature.DECORATED, new DecoratedFeatureConfig(configuredFeature, configuredPlacement));
                            }));
                            List<String> subBiomes = rootDynamic.get("sub_biomes").asList(subBiomeDynamic -> subBiomeDynamic.asString(""));

                            BiomeData biomeData = this.createBiomeData(biome, generationWeight, useDefaultEntities, useDefaultCarvers, useDefaultFeatures, useDefaultStructures, isSubBiome);
                            blocks.forEach(biomeData::addBiomeBlock);
                            entities.forEach(biomeData::addEntitySpawn);
                            carvers.forEach(((carverDynamic, configuredCarver) ->
                            {
                                GenerationStage.Carving stage = Stream.of(GenerationStage.Carving.values())
                                        .filter(value -> value.getName().equalsIgnoreCase(carverDynamic.get("stage").asString("").toUpperCase()))
                                        .findAny().orElse(GenerationStage.Carving.AIR);
                                biomeData.addCarver(stage, configuredCarver);
                            }));
                            features.forEach((featureDynamic, configuredFeature) ->
                            {
                                GenerationStage.Decoration stage = Stream.of(GenerationStage.Decoration.values())
                                        .filter(value -> value.getName().equalsIgnoreCase(featureDynamic.get("stage").asString("").toUpperCase()))
                                        .findAny().orElse(GenerationStage.Decoration.RAW_GENERATION);
                                biomeData.addFeature(stage, configuredFeature);
                            });
                            structures.forEach((featureDynamic, configuredFeature) ->
                            {
                                DecoratedFeatureConfig decoratedFeatureConfig = (DecoratedFeatureConfig) configuredFeature.config;
                                Feature<?> feature = decoratedFeatureConfig.feature.feature;
                                IFeatureConfig config = decoratedFeatureConfig.feature.config;

                                if(feature instanceof Structure<?>)
                                {
                                    Structure<?> structure = (Structure<?>) feature;
                                    biomeData.addStructure(structure, config);
                                    GenerationStage.Decoration stage = Stream.of(GenerationStage.Decoration.values())
                                            .filter(value -> value.getName().equalsIgnoreCase(featureDynamic.get("stage").asString("").toUpperCase()))
                                            .findAny().orElse(GenerationStage.Decoration.RAW_GENERATION);
                                    biomeData.addFeature(stage, configuredFeature);
                                }
                            });
                            this.biomeData.put(biomeName, biomeData);
                            this.subBiomeData.put(biomeName, subBiomes);

                            if(!biomeData.isSubBiome())
                            {
                                this.biomeEntries.put(biomeName, new BiomeManager.BiomeEntry(biome, generationWeight));
                            }
                        }
                    }
                }

                for(Map.Entry<ResourceLocation, List<String>> entry : this.subBiomeData.entrySet())
                {
                    BiomeData biomeData = this.biomeData.get(entry.getKey());

                    if(biomeData != null)
                    {
                        for(String subBiomeName : entry.getValue())
                        {
                            BiomeData subBiomeData = this.biomeData.get(new ResourceLocation(subBiomeName));

                            if(subBiomeData != null)
                            {
                                biomeData.addSubBiome(subBiomeData);
                            }
                        }
                    }
                }

                this.biomeData.forEach((location, biomeData) -> biomeData.configureBiome());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }));
    }

    @Override
    protected Map<ResourceLocation, JsonObject> prepare(IResourceManager resourceManager, IProfiler profiler)
    {
        Map<ResourceLocation, JsonObject> map = new HashMap<>();

        for(ResourceLocation resource : resourceManager.getAllResourceLocations(this.folderName, (fileName) -> fileName.endsWith(".json")))
        {
            String path = resource.getPath();
            ResourceLocation truncatedResource;

            if(!path.startsWith(this.folderName))
            {
                truncatedResource = resource;
            }
            else
            {
                truncatedResource = new ResourceLocation(resource.getNamespace(), path.substring(this.folderName.length() + 1, path.lastIndexOf(".")));
            }

            try(Reader reader = new BufferedReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream(), StandardCharsets.UTF_8)))
            {
                JsonObject jsonObject = JSONUtils.fromJson(GSON, reader, JsonObject.class);

                if(jsonObject != null)
                {
                    if(map.put(truncatedResource, jsonObject) != null)
                    {
                        LibraryEx.LOGGER.error("Duplicate data file: {}", truncatedResource);
                    }
                }
                else
                {
                    LibraryEx.LOGGER.error("Invalid data file: {}", truncatedResource);
                }
            }
            catch(IOException e)
            {
                LibraryEx.LOGGER.error("Unreadable data file: {}", truncatedResource);
            }
        }

        return map;
    }

    public BiomeData createBiomeData(Biome biome, int generationWeight, boolean useDefaultEntities, boolean useDefaultCarvers, boolean useDefaultFeatures, boolean useDefaultStructures, boolean isSubBiome)
    {
        return new BiomeData(biome, generationWeight, useDefaultEntities, useDefaultCarvers, useDefaultFeatures, useDefaultStructures, isSubBiome);
    }

    public BiomeData registerBiomeData(BiomeData biomeData)
    {
        Biome biome = biomeData.getBiome();
        ResourceLocation biomeName = biome.getRegistryName();

        this.biomeData.put(biomeName, biomeData);
        this.subBiomeData.put(biomeName, biomeData.getSubBiomes().stream().map(subBiomeData -> subBiomeData.getBiome().getRegistryName().toString()).collect(Collectors.toList()));

        if(!biomeData.isSubBiome())
        {
            this.biomeEntries.put(biomeName, new BiomeManager.BiomeEntry(biome, biomeData.getGenerationWeight()));
        }

        return biomeData;
    }

    public void unregisterBiomeData(BiomeData biomeData)
    {
        ResourceLocation biomeName = biomeData.getBiome().getRegistryName();
        this.biomeData.remove(biomeName);
        this.subBiomeData.remove(biomeName);
        this.biomeEntries.remove(biomeName);
    }

    public void cleanup()
    {
        this.biomeData.forEach((resourceLocation, biomeData) -> biomeData.resetBiome());
        this.biomeData.clear();
        this.subBiomeData.clear();
        this.biomeEntries.clear();
    }

    public BiomeData getBiomeData(Biome biome)
    {
        return this.biomeData.getOrDefault(biome.getRegistryName(), BiomeData.EMPTY);
    }

    public Map<ResourceLocation, BiomeData> getBiomeData()
    {
        return Collections.unmodifiableMap(this.biomeData);
    }

    public Map<ResourceLocation, BiomeManager.BiomeEntry> getBiomeEntries()
    {
        return Collections.unmodifiableMap(this.biomeEntries);
    }
}
