package lex.biome;

import lex.config.Config;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class WrappedBiomeManager
{
    private static final Map<Biome, WrappedBiome> WRAPPED_BIOME_MAP = new HashMap<>();

    public static void createWrappedBiome(Biome biome, Config config)
    {
        if(!WRAPPED_BIOME_MAP.containsKey(biome))
        {
            WRAPPED_BIOME_MAP.put(biome, new WrappedBiome(biome, config));
        }
    }

    public static WrappedBiome getWrappedBiome(Biome biome)
    {
        if(WRAPPED_BIOME_MAP.containsKey(biome))
        {
            return WRAPPED_BIOME_MAP.get(biome);
        }

        return null;
    }
}
