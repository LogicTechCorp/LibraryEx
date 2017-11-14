package lex.biome;

import lex.config.IConfigEx;
import lex.util.BiomeUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeEx extends Biome implements IBiomeEx
{
    public IBlockState caveCeilingBlock = Blocks.STONE.getDefaultState();
    public IBlockState caveWallBlock = Blocks.STONE.getDefaultState();
    public IBlockState caveFloorBlock = Blocks.STONE.getDefaultState();
    public IBlockState oceanBlock = Blocks.WATER.getDefaultState();
    public int weight = 10;

    public BiomeEx(String name, Properties defaultProperties, IConfigEx configIn)
    {
        super(BiomeUtils.configureBiome(name, defaultProperties, configIn));
    }

    @Override
    public void configure(IConfigEx config)
    {
        topBlock = config.getBlock("topBlock", topBlock);
        fillerBlock = config.getBlock("fillerBlock", fillerBlock);
        caveCeilingBlock = config.getBlock("caveCeilingBlock", caveCeilingBlock);
        caveWallBlock = config.getBlock("caveWallBlock", caveWallBlock);
        caveFloorBlock = config.getBlock("caveFloorBlock", caveFloorBlock);
        oceanBlock = config.getBlock("fillerBlock", oceanBlock);
        weight = config.getInt("weight", weight);
    }

    @Override
    public IBlockState getTopBlock()
    {
        return topBlock;
    }

    @Override
    public IBlockState getFillerBlock()
    {
        return fillerBlock;
    }

    @Override
    public IBlockState getCaveCeilingBlock()
    {
        return caveCeilingBlock;
    }

    @Override
    public IBlockState getCaveWallBlock()
    {
        return caveWallBlock;
    }

    @Override
    public IBlockState getCaveFloorBlock()
    {
        return caveFloorBlock;
    }

    @Override
    public IBlockState getOceanBlock()
    {
        return oceanBlock;
    }

    @Override
    public int getWeight()
    {
        return weight;
    }

    public static class Properties extends BiomeProperties
    {
        private float baseHeight = 0.1F;
        private float heightVariation = 0.2F;
        private float temperature = 0.5F;
        private float rainfall = 0.5F;
        private int waterColor = 16777215;
        private boolean enableSnow = false;
        private boolean enableRain = true;

        public Properties(String name, boolean enableSnowIn, boolean enableRainIn)
        {
            super(name);
            enableSnow = enableSnowIn;
            enableRain = enableRainIn;
        }

        public float getBaseHeight()
        {
            return baseHeight;
        }

        public float getHeightVariation()
        {
            return heightVariation;
        }

        public float getTemperature()
        {
            return temperature;
        }

        public float getRainfall()
        {
            return rainfall;
        }

        public int getWaterColor()
        {
            return waterColor;
        }

        public boolean isSnowEnabled()
        {
            return enableSnow;
        }

        public boolean isRainEnabled()
        {
            return enableRain;
        }

        public Properties setBaseHeight(float baseHeightIn)
        {
            baseHeight = baseHeightIn;
            return this;
        }

        public Properties setHeightVariation(float heightVariationIn)
        {
            heightVariation = heightVariationIn;
            return this;
        }


        public Properties setTemperature(float temperatureIn)
        {
            temperature = temperatureIn;
            return this;
        }


        public Properties setRainfall(float rainfallIn)
        {
            rainfall = rainfallIn;
            return this;
        }

        public Properties setWaterColor(int waterColorIn)
        {
            waterColor = waterColorIn;
            return this;
        }

        public Properties setRainDisabled()
        {
            enableRain = false;
            return this;
        }


        public Properties setSnowEnabled()
        {
            enableSnow = true;
            return this;
        }
    }
}
