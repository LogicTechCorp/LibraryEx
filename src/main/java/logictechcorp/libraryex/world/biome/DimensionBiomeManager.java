package logictechcorp.libraryex.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DimensionBiomeManager
{
    private final List<BiomeInfo> biomeInfo = new ArrayList<>();
    private final Map<BiomeInfo, BiomeManager.BiomeEntry> biomeEntries = new ConcurrentHashMap<>();

    public abstract void readBiomeInfoFromConfigs(MinecraftServer server);

    public abstract void writeBiomeInfoToConfigs(MinecraftServer server);

    public void addBiome(BiomeInfo info)
    {
        if(info == null)
        {
            return;
        }

        if(!this.biomeInfo.contains(info))
        {
            this.biomeInfo.add(info);
            this.biomeEntries.put(info, new BiomeManager.BiomeEntry(info.getBiome(), info.getWeight()));
        }
    }

    public void removeBiome(BiomeInfo info)
    {
        this.biomeEntries.remove(info);
    }

    public List<BiomeInfo> getAllBiomeInfo()
    {
        return ImmutableList.copyOf(this.biomeInfo);
    }

    public List<BiomeManager.BiomeEntry> getBiomeEntries()
    {
        return ImmutableList.copyOf(this.biomeEntries.values());
    }

    public BiomeInfo getBiomeInfo(Biome biome)
    {
        for(BiomeInfo info : this.biomeInfo)
        {
            if(biome == info.getBiome())
            {
                return info;
            }
        }

        return null;
    }

    public abstract File getBiomeInfoSaveFile(MinecraftServer server, BiomeInfo wrapper);
}
