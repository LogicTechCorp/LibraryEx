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
import logictechcorp.libraryex.LibraryEx;
import logictechcorp.libraryex.utilities.DynamicHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
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

public class BiomeDataManager extends JsonReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceLocation, BiomeData> biomeData;
    private final Map<ResourceLocation, List<String>> subBiomeData;
    private final Map<ResourceLocation, BiomeManager.BiomeEntry> biomeEntries;

    public BiomeDataManager()
    {
        this("biomes");
    }

    public BiomeDataManager(String folder)
    {
        super(GSON, folder);
        this.biomeData = new HashMap<>();
        this.subBiomeData = new HashMap<>();
        this.biomeEntries = new HashMap<>();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> locations, IResourceManager resourceManager, IProfiler profiler)
    {
        locations.forEach(((location, object) ->
        {
            try
            {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(location.getPath().replace("/", ":")));

                if(biome != null)
                {
                    ResourceLocation biomeName = biome.getRegistryName();
                    IResource resource = resourceManager.getResource(this.getPreparedPath(location));
                    InputStream inputStream = resource.getInputStream();
                    Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, JSONUtils.fromJson(GSON, reader, JsonObject.class));

                    if(dynamic.getValue() == null)
                    {
                        LibraryEx.LOGGER.error("Couldn't load {} biome config from {} data pack.", resource.getLocation(), resource.getPackName());
                    }
                    else
                    {
                        int generationWeight = dynamic.get("generation_weight").asInt(10);
                        boolean useDefaultFeatures = dynamic.get("use_default_features").asBoolean(true);
                        boolean isSubBiome = dynamic.get("is_sub_biome").asBoolean(false);
                        Map<BiomeData.BlockType, BlockState> blocks = dynamic.get("blocks").asMap(BiomeData.BlockType::deserialize, BlockState::deserialize);
                        List<Biome.SpawnListEntry> entities = dynamic.get("entities").asList(DynamicHelper::deserializeSpawnListEntry);
                        Map<Dynamic<?>, ConfiguredFeature<?>> features = dynamic.get("features").asStream().collect(Collectors.toMap(Function.identity(), DynamicHelper::deserializeConfiguredFeature));
                        List<String> subBiomes = dynamic.get("sub_biomes").asList(subBiomeDynamic -> subBiomeDynamic.asString(""));

                        BiomeData biomeData = this.createBiomeData(biome, generationWeight, useDefaultFeatures, isSubBiome);
                        blocks.forEach(biomeData::addBiomeBlock);
                        entities.forEach(biomeData::addEntitySpawn);
                        features.forEach((featureDynamic, feature) -> biomeData.addFeature(DynamicHelper.deserializeGenerationStage(featureDynamic), feature));
                        this.subBiomeData.put(biomeName, subBiomes);
                        this.biomeData.put(biomeName, biomeData);

                        if(!biomeData.isSubBiome())
                        {
                            this.biomeEntries.put(biomeName, new BiomeManager.BiomeEntry(biome, generationWeight));
                        }
                    }

                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(resource);
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

                this.biomeData.forEach((resourceLocation, biomeData) -> biomeData.configureBiome());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }));
    }

    public BiomeData createBiomeData(Biome biome, int generationWeight, boolean useDefaultFeatures, boolean isSubBiome)
    {
        return new BiomeData(biome, generationWeight, useDefaultFeatures, isSubBiome);
    }

    public void cleanup()
    {
        this.biomeData.forEach((resourceLocation, biomeData) -> biomeData.resetBiome());
        this.biomeData.clear();
        this.subBiomeData.clear();
        this.biomeEntries.clear();
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
