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

package lex.world.gen.feature;

import lex.LibEx;
import lex.util.NumberHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public abstract class FeatureLibEx extends WorldGenerator implements IFeature
{
    protected int generationAttempts;
    protected boolean randomizeGenerationAttempts;
    protected float generationProbability;
    protected int minHeight;
    protected int maxHeight;

    protected FeatureLibEx(FeatureBuilder builder)
    {
        generationAttempts = builder.generationAttempts;
        randomizeGenerationAttempts = builder.randomizeGenerationAttempts;
        generationProbability = builder.generationProbability;
        minHeight = builder.minHeight;
        maxHeight = builder.maxHeight;
    }

    @Override
    public abstract boolean generate(World world, Random rand, BlockPos pos);

    @Override
    public int getGenerationAttempts()
    {
        return generationAttempts;
    }

    @Override
    public int getGenerationAttempts(Random rand)
    {
        int attempts = generationAttempts;

        if(generationProbability > 0.0F && generationProbability < 1.0F && rand.nextFloat() > generationProbability)
        {
            attempts = 0;
        }
        if(randomizeGenerationAttempts)
        {
            attempts = NumberHelper.getNumberInRange(1, attempts, rand);
        }

        return attempts;
    }

    @Override
    public boolean randomizeGenerationAttempts()
    {
        return randomizeGenerationAttempts;
    }

    @Override
    public float getGenerationProbability()
    {
        return generationProbability;
    }

    @Override
    public int getMinHeight()
    {
        return minHeight;
    }

    @Override
    public int getMaxHeight()
    {
        return maxHeight;
    }

    public static abstract class LibExFeatureBuilder extends FeatureBuilder
    {
        public LibExFeatureBuilder(String name)
        {
            setRegistryName(new ResourceLocation(LibEx.MOD_ID + ":" + name));
        }
    }
}
