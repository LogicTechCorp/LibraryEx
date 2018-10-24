package lex.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BiomeRegistry<T extends IBiomeWrapper> implements IBiomeRegistry<T>
{
    protected final List<BiomeManager.BiomeEntry> biomeEntries = new ArrayList<>();
    protected final Map<Integer, T> moddedBiomes = new HashMap<>();
    protected final Map<Integer, T> playerBiomes = new HashMap<>();

    @Override
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

    @Override
    public void removeBiome(Biome biome)
    {
        this.moddedBiomes.remove(Biome.getIdForBiome(biome));
    }

    @Override
    public List<BiomeManager.BiomeEntry> getBiomeEntries()
    {
        return this.biomeEntries;
    }

    @Override
    public T getBiomeWrapper(Biome biome)
    {
        int biomeId = Biome.getIdForBiome(biome);

        if(this.moddedBiomes.containsKey(biomeId))
        {
            return this.moddedBiomes.get(biomeId);
        }

        return this.playerBiomes.get(biomeId);
    }

    @Override
    public T getModdedBiomeWrapper(Biome biome)
    {
        return this.moddedBiomes.get(Biome.getIdForBiome(biome));
    }

    @Override
    public T getPlayerBiomeWrapper(Biome biome)
    {
        return this.playerBiomes.get(Biome.getIdForBiome(biome));
    }
}
