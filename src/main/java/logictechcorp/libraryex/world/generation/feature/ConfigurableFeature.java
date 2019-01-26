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

package logictechcorp.libraryex.world.generation.feature;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public abstract class ConfigurableFeature extends WorldGenerator
{
    protected int generationAttempts;
    protected double generationProbability;
    protected boolean randomizeGenerationAttempts;
    protected int minGenerationHeight;
    protected int maxGenerationHeight;

    public ConfigurableFeature(Config config)
    {
        this.generationAttempts = config.getOrElse("generationAttempts", 4);
        this.generationProbability = config.getOrElse("generationProbability", 1.0D);
        this.randomizeGenerationAttempts = config.getOrElse("randomizeGenerationAttempts", false);
        ConfigHelper.rename(config, "minHeight", "minGenerationHeight");
        ConfigHelper.rename(config, "maxHeight", "maxGenerationHeight");
        this.minGenerationHeight = config.getOrElse("minGenerationHeight", 0);
        this.maxGenerationHeight = config.getOrElse("maxGenerationHeight", 255);
    }

    public ConfigurableFeature(int generationAttempts, double generationProbability, boolean randomizeGenerationAttempts, int minGenerationHeight, int maxGenerationHeight)
    {
        this.generationAttempts = generationAttempts;
        this.generationProbability = generationProbability;
        this.randomizeGenerationAttempts = randomizeGenerationAttempts;
        this.minGenerationHeight = minGenerationHeight;
        this.maxGenerationHeight = maxGenerationHeight;
    }

    @Override
    public abstract boolean generate(World world, Random random, BlockPos pos);

    public Config serialize()
    {
        Config config = TomlFormat.newConcurrentConfig();
        config.add("feature", FeatureRegistry.getFeatureRegistryName(this.getClass()).toString());
        config.add("generationAttempts", this.generationAttempts);
        config.add("generationProbability", this.generationProbability);
        config.add("randomizeGenerationAttempts", this.randomizeGenerationAttempts);
        config.add("minGenerationHeight", this.minGenerationHeight);
        config.add("maxGenerationHeight", this.maxGenerationHeight);
        return config;
    }

    public int getGenerationAttempts()
    {
        return this.generationAttempts;
    }

    public int getRandomizedGenerationAttempts(Random random)
    {
        int attempts = this.generationAttempts;

        if(this.generationProbability > 0.0D && this.generationProbability < 1.0D && random.nextDouble() > this.generationProbability)
        {
            attempts = 0;
        }
        if(this.randomizeGenerationAttempts)
        {
            attempts = RandomHelper.getNumberInRange(1, attempts, random);
        }

        return attempts;
    }

    public double getGenerationProbability()
    {
        return this.generationProbability;
    }

    public boolean randomizeGenerationAttempts()
    {
        return this.randomizeGenerationAttempts;
    }

    public int getMinHeight()
    {
        return this.minGenerationHeight;
    }

    public int getMaxHeight()
    {
        return this.maxGenerationHeight;
    }
}
