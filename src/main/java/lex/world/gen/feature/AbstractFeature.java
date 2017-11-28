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

import lex.config.IConfig;
import lex.util.NumberHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public abstract class AbstractFeature extends WorldGenerator implements IFeature
{
    private int generationAttempts;
    private boolean randomizeGenerationAttempts;
    private float generationProbability;
    private int minHeight;
    private int maxHeight;

    AbstractFeature(AbstractBuilder builder)
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

    public abstract static class AbstractBuilder<B extends AbstractBuilder<B, F>, F extends IFeature> implements IFeatureBuilder<B, F>
    {
        int generationAttempts;
        boolean randomizeGenerationAttempts;
        float generationProbability;
        int minHeight;
        int maxHeight;

        @Override
        public B configure(IConfig config)
        {
            generationAttempts = config.getInt("generationAttempts", 8);
            randomizeGenerationAttempts = config.getBoolean("randomizeGenerationAttempts", false);
            generationProbability = config.getFloat("generationProbability", 1.0F);
            minHeight = config.getInt("minHeight", 32);
            maxHeight = config.getInt("maxHeight", 96);

            if(generationAttempts < 0)
            {
                generationAttempts = (generationAttempts * -1);
            }
            if(generationProbability < 0)
            {
                generationProbability = (generationProbability * -1.0F);
            }
            if(minHeight > maxHeight)
            {
                int minHeightHolder = minHeight;
                minHeight = maxHeight;
                maxHeight = minHeightHolder;
            }

            return (B) this;
        }
    }
}
