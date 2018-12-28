package logictechcorp.libraryex.world.biome;

import logictechcorp.libraryex.util.WorldHelper;
import logictechcorp.libraryex.world.biome.wrapper.IBiomeWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DimensionBiomeManager<T extends IBiomeWrapper>
{
    protected final List<BiomeManager.BiomeEntry> biomeEntries = new ArrayList<>();
    protected final Map<Integer, T> moddedBiomes = new HashMap<>();
    protected final Map<Integer, T> playerBiomes = new HashMap<>();

    public void addBiome(T wrapper)
    {
        if(wrapper == null)
        {
            return;
        }

        int biomeId = Biome.getIdForBiome(wrapper.getBiome());

        if(!this.moddedBiomes.containsKey(biomeId))
        {
            this.moddedBiomes.put(biomeId, wrapper);
        }
    }

    public void removeBiome(Biome biome)
    {
        this.moddedBiomes.remove(Biome.getIdForBiome(biome));
    }

    public List<BiomeManager.BiomeEntry> getBiomeEntries()
    {
        return this.biomeEntries;
    }

    public T getBiomeWrapper(Biome biome)
    {
        int biomeId = Biome.getIdForBiome(biome);

        if(this.moddedBiomes.containsKey(biomeId))
        {
            return this.moddedBiomes.get(biomeId);
        }

        return this.playerBiomes.get(biomeId);
    }

    public T getModdedBiomeWrapper(Biome biome)
    {
        return this.moddedBiomes.get(Biome.getIdForBiome(biome));
    }

    public T getPlayerBiomeWrapper(Biome biome)
    {
        return this.playerBiomes.get(Biome.getIdForBiome(biome));
    }

    public File getBiomeWrapperSaveFile(MinecraftServer server, IBiomeWrapper wrapper)
    {
        return new File(WorldHelper.getSaveFile(server.getEntityWorld()), "/config/NetherEx/Biomes/" + wrapper.getFileName());
    }
}
