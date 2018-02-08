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

package lex.api.world.biome;

import com.google.common.collect.ImmutableList;
import lex.api.world.gen.feature.IFeature;
import lex.world.gen.GenerationStage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BiomeWrapper implements IBiomeWrapper
{
    protected Biome biome;
    protected int weight;
    protected Map<String, IBlockState> blocks = new HashMap<>();
    protected Map<GenerationStage, List<IFeature>> generationStageFeatures = new HashMap<>();

    public BiomeWrapper(Biome biomeIn)
    {
        biome = biomeIn;
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
    public List<IFeature> getFeatures(GenerationStage generationStage)
    {
        return ImmutableList.copyOf(generationStageFeatures.computeIfAbsent(generationStage, k -> new ArrayList<>()));
    }
}
