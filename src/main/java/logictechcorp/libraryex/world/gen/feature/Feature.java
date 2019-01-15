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

package logictechcorp.libraryex.world.gen.feature;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import logictechcorp.libraryex.util.ConfigHelper;
import logictechcorp.libraryex.util.RandomHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public abstract class Feature extends WorldGenerator
{
    protected int genAttempts;
    protected double genProbability;
    protected boolean randomizeGenAttempts;
    protected int minGenHeight;
    protected int maxGenHeight;

    public Feature(Config config)
    {
        this.genAttempts = config.getOrElse("genAttempts", 4);
        this.genProbability = config.getOrElse("genProbability", 1.0D);
        this.randomizeGenAttempts = config.getOrElse("randomizeGenAttempts", false);
        ConfigHelper.rename(config, "minHeight", "minGenHeight");
        ConfigHelper.rename(config, "maxHeight", "maxGenHeight");
        this.minGenHeight = config.getOrElse("minGenHeight", 0);
        this.maxGenHeight = config.getOrElse("maxGenHeight", 255);
    }

    public Feature(int genAttempts, double genProbability, boolean randomizeGenAttempts, int minGenHeight, int maxGenHeight)
    {
        this.genAttempts = genAttempts;
        this.genProbability = genProbability;
        this.randomizeGenAttempts = randomizeGenAttempts;
        this.minGenHeight = minGenHeight;
        this.maxGenHeight = maxGenHeight;
    }

    @Override
    public abstract boolean generate(World world, Random rand, BlockPos pos);

    public Config serialize()
    {
        Config config = TomlFormat.newConcurrentConfig();
        config.add("maxGenHeight", this.maxGenHeight);
        config.add("minGenHeight", this.minGenHeight);
        config.add("randomizeGenAttempts", this.randomizeGenAttempts);
        config.add("genProbability", this.genProbability);
        config.add("genAttempts", this.genAttempts);
        config.add("feature", FeatureRegistry.getFeatureRegistryName(this.getClass()).toString());
        return config;
    }

    public int getGenAttempts()
    {
        return this.genAttempts;
    }

    public int getGenAttempts(Random rand)
    {
        int attempts = this.genAttempts;

        if(this.genProbability > 0.0D && this.genProbability < 1.0D && rand.nextDouble() > this.genProbability)
        {
            attempts = 0;
        }
        if(this.randomizeGenAttempts)
        {
            attempts = RandomHelper.getNumberInRange(1, attempts, rand);
        }

        return attempts;
    }

    public double getGenProbability()
    {
        return this.genProbability;
    }

    public boolean randomizeGenAttempts()
    {
        return this.randomizeGenAttempts;
    }

    public int getMinHeight()
    {
        return this.minGenHeight;
    }

    public int getMaxHeight()
    {
        return this.maxGenHeight;
    }
}
