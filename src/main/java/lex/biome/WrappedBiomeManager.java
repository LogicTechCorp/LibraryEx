package lex.biome;

import com.google.common.collect.ImmutableMap;
import lex.config.JsonConfig;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WrappedBiomeManager
{
    private static final Map<Biome, WrappedBiome> WRAPPED_BIOMES = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger("LibEx|Main");

    public static void createWrappedBiome(Biome biome, JsonConfig config)
    {
        if(!WRAPPED_BIOMES.containsKey(biome))
        {
            WRAPPED_BIOMES.put(biome, new WrappedBiome(biome, config));
        }
        else
        {
            LOGGER.warn("The {} biome already has a wrapped version", biome.getRegistryName().toString());
        }
    }

    public static WrappedBiome getWrappedBiome(Biome biome)
    {
        if(WRAPPED_BIOMES.containsKey(biome))
        {
            return WRAPPED_BIOMES.get(biome);
        }
        else
        {
            LOGGER.warn("The {} biome does not have a wrapped version", biome.getRegistryName().toString());
        }

        return null;
    }

    public static Map<Biome, WrappedBiome> getWrappedBiomes()
    {
        return ImmutableMap.copyOf(WRAPPED_BIOMES);
    }
}
