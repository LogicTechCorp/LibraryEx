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

package logictechcorp.libraryex.world.biome.data;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.utility.FileHelper;
import logictechcorp.libraryex.utility.WorldHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeDataManager
{
    private final String modId;
    private final Logger logger;
    private final Map<ResourceLocation, BiomeData> defaultBiomeData;
    private final Map<ResourceLocation, BiomeData> worldSpecificBiomeData;
    private final Map<ResourceLocation, BiomeManager.BiomeEntry> worldSpecificBiomeEntries;

    public BiomeDataManager(String modId, String modName)
    {
        this.modId = modId;
        this.logger = LogManager.getLogger(modName);
        this.defaultBiomeData = new HashMap<>();
        this.worldSpecificBiomeData = new HashMap<>();
        this.worldSpecificBiomeEntries = new ConcurrentHashMap<>();
    }

    public void setup()
    {
        this.worldSpecificBiomeData.forEach(this.defaultBiomeData::put);
    }

    public void registerBiomeData(BiomeData biomeData)
    {
        if(biomeData != null && biomeData.getBiome() != null && biomeData != BiomeData.EMPTY)
        {
            Biome biome = biomeData.getBiome();
            ResourceLocation biomeRegistryName = biome.getRegistryName();

            if(!this.worldSpecificBiomeData.containsKey(biomeRegistryName))
            {
                this.worldSpecificBiomeData.put(biomeRegistryName, biomeData);
            }
            if(!biomeData.isSubBiome())
            {
                if(biomeData.isEnabled())
                {
                    this.worldSpecificBiomeEntries.put(biomeRegistryName, new BiomeManager.BiomeEntry(biome, biomeData.getGenerationWeight()));
                }
                else
                {
                    this.worldSpecificBiomeEntries.remove(biomeRegistryName);
                }
            }
        }
    }

    public void unregisterBiomeData(Biome biome)
    {
        if(biome != null)
        {
            ResourceLocation biomeRegistryName = biome.getRegistryName();
            this.worldSpecificBiomeData.remove(biomeRegistryName);
            this.worldSpecificBiomeEntries.remove(biomeRegistryName);
        }
    }

    public void cleanup(WorldEvent.Unload event)
    {
        this.worldSpecificBiomeData.clear();
        this.worldSpecificBiomeEntries.clear();
    }

    public void readBiomeDataConfigs(WorldEvent.Load event)
    {
        Path path = Paths.get(WorldHelper.getSaveDirectory(event.getWorld()), "config", this.modId, "nether_biomes");

        if(Files.isReadable(path))
        {
            this.logger.info("Reading Nether biome data configs.");

            try
            {
                Files.createDirectories(path);
                Iterator<Path> pathIter = Files.walk(path).iterator();

                while(pathIter.hasNext())
                {
                    File configFile = pathIter.next().toFile();

                    if(FileHelper.getFileExtension(configFile).equals("json"))
                    {
                        String fileText = FileUtils.readFileToString(configFile, Charset.defaultCharset()).trim();

                        if(fileText.isEmpty() || !fileText.startsWith("{") || !fileText.endsWith("}"))
                        {
                            String filePath = configFile.getPath();
                            String fileBackupPath = filePath + "_backup";
                            Files.move(configFile.toPath(), Paths.get(fileBackupPath));
                            this.logger.warn("The biome config at {} was invalid and was backed up as {}.", filePath, fileBackupPath);
                            continue;
                        }

                        FileConfig config = FileConfig.builder(configFile, JsonFormat.fancyInstance()).preserveInsertionOrder().build();
                        config.load();

                        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(config.get("biome")));

                        if(biome != null)
                        {
                            BiomeData biomeData;

                            if(this.hasBiomeData(biome))
                            {
                                biomeData = this.getBiomeData(biome);
                            }
                            else
                            {
                                biomeData = new BiomeData(biome.getRegistryName(), 10, true, false);
                            }

                            biomeData.readFromConfig(this, config);
                            this.registerBiomeData(biomeData);
                        }

                        config.save();
                        config.close();
                    }
                    else if(!configFile.isDirectory() && !FileHelper.getFileExtension(configFile).equals("json_backup"))
                    {
                        this.logger.warn("Skipping file located at, {}, as it is not a json file.", configFile.getPath());
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            this.logger.warn("Unable to read Biome configs. The default configs will be used.");
        }
    }

    public void createBiomeDataConfigs(WorldEvent.Load event)
    {
        this.logger.info("Creating Nether biome data configs.");

        try
        {
            for(BiomeData biomeData : this.getDefaultBiomeData().values())
            {
                File configFile = new File(WorldHelper.getSaveDirectory(event.getWorld()), "config/" + this.modId + "/nether_biomes/" + biomeData.getBiome().getRegistryName().toString().replace(":", "/") + ".json");

                if(!configFile.exists())
                {
                    Files.createDirectories(configFile.getParentFile().toPath());
                    FileConfig config = FileConfig.builder(configFile, JsonFormat.fancyInstance()).preserveInsertionOrder().build();
                    biomeData.writeToConfig(config);
                    config.save();
                    config.close();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean hasBiomeData(Biome biome)
    {
        return this.worldSpecificBiomeData.containsKey(biome.getRegistryName());
    }

    public BiomeData getBiomeData(Biome biome)
    {
        return this.worldSpecificBiomeData.getOrDefault(biome.getRegistryName(), BiomeData.EMPTY);
    }

    public Map<ResourceLocation, BiomeData> getDefaultBiomeData()
    {
        return Collections.unmodifiableMap(this.defaultBiomeData);
    }

    public Map<ResourceLocation, BiomeData> getWorldSpecificBiomeData()
    {
        return Collections.unmodifiableMap(this.worldSpecificBiomeData);
    }

    public Map<ResourceLocation, BiomeManager.BiomeEntry> getWorldSpecificBiomeEntries()
    {
        return Collections.unmodifiableMap(this.worldSpecificBiomeEntries);
    }
}
