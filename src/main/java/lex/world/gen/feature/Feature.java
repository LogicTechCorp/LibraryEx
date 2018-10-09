/*
 * LibEx
 * Copyright (c) 2017-2018 by MineEx
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

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.json.JsonFormat;
import lex.util.ConfigHelper;
import lex.util.RandomHelper;
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
        this.genAttempts = ConfigHelper.getOrSet(config, "genAttempts", 4);
        this.genProbability = ConfigHelper.getOrSet(config, "genProbability", 1.0D);
        this.randomizeGenAttempts = ConfigHelper.getOrSet(config, "randomizeGenAttempts", false);

        if(config.contains("minHeight"))
        {
            this.minGenHeight = ConfigHelper.getOrSet(config, "minHeight", 0);
            config.remove("minHeight");
            ConfigHelper.getOrSet(config, "minGenHeight", this.minGenHeight);
        }
        else
        {
            this.minGenHeight = ConfigHelper.getOrSet(config, "minGenHeight", 0);
        }

        if(config.contains("maxHeight"))
        {
            this.maxGenHeight = ConfigHelper.getOrSet(config, "maxHeight", 255);
            config.remove("maxHeight");
            ConfigHelper.getOrSet(config, "maxGenHeight", this.maxGenHeight);
        }
        else
        {
            this.maxGenHeight = ConfigHelper.getOrSet(config, "maxGenHeight", 255);
        }
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
        Config config = JsonFormat.newConcurrentConfig();
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
            attempts = RandomHelper.getRandomNumberInRange(1, attempts, rand);
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
