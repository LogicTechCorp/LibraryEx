/*
 * LibraryEx
 * Copyright (c) 2017-2019 by LogicTechCorp
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

package logictechcorp.libraryex.world.generation.trait.impl;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.api.LibraryExAPI;
import logictechcorp.libraryex.utility.RandomHelper;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTrait;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTraitBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * The base class for a biome trait.
 */
public abstract class BiomeTrait implements IBiomeTrait
{
    protected int generationAttempts;
    protected boolean randomizeGenerationAttempts;
    protected double generationProbability;
    protected int minimumGenerationHeight;
    protected int maximumGenerationHeight;

    protected BiomeTrait(Builder builder)
    {
        this.generationAttempts = builder.generationAttempts;
        this.randomizeGenerationAttempts = builder.randomizeGenerationAttempts;
        this.generationProbability = builder.generationProbability;
        this.minimumGenerationHeight = builder.minimumGenerationHeight;
        this.maximumGenerationHeight = builder.maximumGenerationHeight;
    }

    @Override
    public abstract boolean generate(World world, BlockPos pos, Random random);

    @Override
    public void readFromConfig(Config config)
    {
        this.generationAttempts = config.getOrElse("generationAttempts", 4);
        this.randomizeGenerationAttempts = config.getOrElse("randomizeGenerationAttempts", false);
        this.generationProbability = config.getOrElse("generationProbability", 1.0D);
        this.minimumGenerationHeight = config.getOrElse("minimumGenerationHeight", 0);
        this.maximumGenerationHeight = config.getOrElse("maximumGenerationHeight", 255);
    }

    @Override
    public void writeToConfig(Config config)
    {
        config.add("trait", LibraryExAPI.getInstance().getBiomeTraitRegistry().getBiomeTraitName(this.getClass()).toString());
        config.add("generationAttempts", this.generationAttempts);
        config.add("randomizeGenerationAttempts", this.randomizeGenerationAttempts);
        config.add("generationProbability", this.generationProbability);
        config.add("minimumGenerationHeight", this.minimumGenerationHeight);
        config.add("maximumGenerationHeight", this.maximumGenerationHeight);
    }

    @Override
    public boolean useRandomizedGenerationAttempts()
    {
        return this.randomizeGenerationAttempts;
    }

    @Override
    public int getGenerationAttempts(World world, BlockPos pos, Random random)
    {
        int attempts = 0;

        if(this.generationProbability >= random.nextDouble())
        {
            attempts = this.generationAttempts;

            if(this.randomizeGenerationAttempts)
            {
                attempts = RandomHelper.getNumberInRange(1, attempts, random);
            }
        }

        return attempts;
    }

    @Override
    public double getGenerationProbability(World world, BlockPos pos, Random random)
    {
        return this.generationProbability;
    }

    @Override
    public int getMinimumGenerationHeight(World world, BlockPos pos, Random random)
    {
        return this.minimumGenerationHeight;
    }

    @Override
    public int getMaximumGenerationHeight(World world, BlockPos pos, Random random)
    {
        return this.maximumGenerationHeight;
    }

    public static abstract class Builder implements IBiomeTraitBuilder<BiomeTrait>
    {
        protected int generationAttempts;
        protected boolean randomizeGenerationAttempts;
        protected double generationProbability;
        protected int minimumGenerationHeight;
        protected int maximumGenerationHeight;

        public Builder()
        {
            this.generationAttempts = 4;
            this.randomizeGenerationAttempts = false;
            this.generationProbability = 1.0D;
            this.minimumGenerationHeight = 2;
            this.maximumGenerationHeight = 60;
        }

        public Builder generationAttempts(int generationAttempts)
        {
            this.generationAttempts = generationAttempts;
            return this;
        }

        public Builder randomizeGenerationAttempts(boolean randomizeGenerationAttempts)
        {
            this.randomizeGenerationAttempts = randomizeGenerationAttempts;
            return this;
        }

        public Builder generationProbability(double generationProbability)
        {
            this.generationProbability = generationProbability;
            return this;
        }

        public Builder minimumGenerationHeight(int minimumGenerationHeight)
        {
            this.minimumGenerationHeight = minimumGenerationHeight;
            return this;
        }

        public Builder maximumGenerationHeight(int maximumGenerationHeight)
        {
            this.maximumGenerationHeight = maximumGenerationHeight;
            return this;
        }
    }
}
