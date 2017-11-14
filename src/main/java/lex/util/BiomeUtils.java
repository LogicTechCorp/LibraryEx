package lex.util;

import lex.biome.BiomeEx;
import lex.config.IConfigEx;
import net.minecraft.world.biome.Biome;

public class BiomeUtils
{
    public static Biome.BiomeProperties configureBiome(String name, BiomeEx.Properties defaultProperties, IConfigEx config)
    {
        if(config.isValid())
        {
            return new Biome.BiomeProperties("Null");
        }

        float baseHeight = config.getFloat("baseHeight", defaultProperties.getBaseHeight());
        float heightVariation = config.getFloat("heightVariation", defaultProperties.getHeightVariation());
        float temperature = config.getFloat("temperature", defaultProperties.getTemperature());
        float rainfall = config.getFloat("rainfall", defaultProperties.getRainfall());
        int waterColor = config.getInt("waterColor", defaultProperties.getWaterColor());
        boolean enableSnow = config.getBoolean("enableSnow", defaultProperties.isSnowEnabled());
        boolean enableRain = config.getBoolean("enableRain", defaultProperties.isRainEnabled());

        return new BiomeEx.Properties(name, enableSnow, enableRain).setBaseHeight(baseHeight).setHeightVariation(heightVariation).setTemperature(temperature).setRainfall(rainfall).setWaterColor(waterColor);
    }
}
