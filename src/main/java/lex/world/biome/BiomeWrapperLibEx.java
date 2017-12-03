/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.world.biome;

import com.google.common.collect.ImmutableList;
import lex.LibEx;
import lex.world.gen.GenerationStage;
import lex.world.gen.feature.IFeature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BiomeWrapperLibEx implements IBiomeWrapper
{
    private Biome biome;
    private int weight;
    private Map<String, IBlockState> blocks = new HashMap<>();
    private Map<GenerationStage, List<IFeature>> generationStageFeatureMap = new HashMap<>();

    BiomeWrapperLibEx(BiomeWrapperBuilder builder)
    {
        biome = builder.biome;
        weight = builder.weight;
        blocks = builder.blocks;
        generationStageFeatureMap = builder.generationStageFeatures;
    }

    @Override
    public Biome getBiome()
    {
        return biome;
    }

    @Override
    public IBlockState getBlock(String key, IBlockState fallbackValue)
    {
        IBlockState value = getBlock(key);

        if(value == null)
        {
            blocks.put(key, fallbackValue);
            return fallbackValue;
        }

        return value;
    }

    @Override
    public IBlockState getBlock(String key)
    {
        return blocks.get(key);
    }

    @Override
    public List<IBlockState> getBlocks()
    {
        return ImmutableList.copyOf(blocks.values());
    }

    @Override
    public int getWeight()
    {
        return weight;
    }

    @Override
    public List<IFeature> getFeatureList(GenerationStage generationStage)
    {
        return ImmutableList.copyOf(generationStageFeatureMap.computeIfAbsent(generationStage, k -> new ArrayList<>()));
    }

    static abstract class LibExBiomeWrapperBuilder extends BiomeWrapperBuilder
    {
        LibExBiomeWrapperBuilder(String name)
        {
            setRegistryName(new ResourceLocation(LibEx.MOD_ID + ":" + name));
        }
    }
}
