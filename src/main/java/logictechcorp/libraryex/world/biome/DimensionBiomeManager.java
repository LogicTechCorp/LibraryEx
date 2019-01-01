package logictechcorp.libraryex.world.biome;

import com.google.common.collect.ImmutableList;
import logictechcorp.libraryex.util.WorldHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class DimensionBiomeManager
{
    private final List<BiomeInfo> biomeInfo = new ArrayList<>();
    private final List<BiomeManager.BiomeEntry> biomeEntries = new ArrayList<>();

    public abstract void readBiomeInfoFromConfigs(MinecraftServer server);

    public abstract void writeBiomeInfoToConfigs(MinecraftServer server);

    public void addBiome(BiomeInfo wrapper)
    {
        if(wrapper == null)
        {
            return;
        }

        BiomeManager.BiomeEntry entry = new BiomeManager.BiomeEntry(wrapper.getBiome(), wrapper.getWeight());

        if(this.biomeInfo.stream().noneMatch(k -> wrapper.getBiome() == k.getBiome()))
        {
            this.biomeInfo.add(wrapper);
        }

        if(this.biomeEntries.stream().noneMatch(k -> entry.biome == k.biome))
        {
            this.biomeEntries.add(entry);
        }
    }

    public void removeBiome(BiomeInfo wrapper)
    {
        this.biomeEntries.removeIf(entry -> entry.biome == wrapper.getBiome());
    }

    public List<BiomeInfo> getAllBiomeInfo()
    {
        return ImmutableList.copyOf(this.biomeInfo);
    }

    public List<BiomeManager.BiomeEntry> getBiomeEntries()
    {
        return ImmutableList.copyOf(this.biomeEntries);
    }

    public BiomeInfo getAllBiomeInfo(Biome biome)
    {
        return this.biomeInfo.stream().filter(wrapper -> biome == wrapper.getBiome()).findFirst().orElse(null);
    }

    public File getBiomeInfoSaveFile(MinecraftServer server, BiomeInfo wrapper)
    {
        return new File(WorldHelper.getSaveFile(server.getEntityWorld()), "/config/NetherEx/Biomes/" + wrapper.getFileName());
    }
}
