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

import com.google.gson.JsonPrimitive;
import lex.config.Config;
import lex.util.NumberHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public abstract class Feature extends WorldGenerator
{
    protected int genAttempts;
    protected float genProbability;
    protected boolean randomizeGenAttempts;
    protected int minGenHeight;
    protected int maxGenHeight;

    public Feature(Config config)
    {
        this.genAttempts = config.getInt("genAttempts", 4);
        this.genProbability = config.getFloat("genProbability", 1.0F);
        this.randomizeGenAttempts = config.getBoolean("randomizeGenAttempts", false);

        if(config.hasData("minHeight"))
        {
            this.minGenHeight = config.getInt("minHeight");
            config.removeData("minHeight");
            config.addData("minGenHeight", new JsonPrimitive(this.minGenHeight));
        }
        else
        {
            this.minGenHeight = config.getInt("minGenHeight", 0);
        }

        if(config.hasData("maxHeight"))
        {
            this.maxGenHeight = config.getInt("maxHeight");
            config.removeData("maxHeight");
            config.addData("maxGenHeight", new JsonPrimitive(this.maxGenHeight));
        }
        else
        {
            this.maxGenHeight = config.getInt("maxGenHeight", 255);
        }
    }

    public Feature(int genAttempts, float genProbability, boolean randomizeGenAttempts, int minGenHeight, int maxGenHeight)
    {
        this.genAttempts = genAttempts;
        this.genProbability = genProbability;
        this.randomizeGenAttempts = randomizeGenAttempts;
        this.minGenHeight = minGenHeight;
        this.maxGenHeight = maxGenHeight;
    }

    @Override
    public abstract boolean generate(World world, Random rand, BlockPos pos);

    public int getGenAttempts()
    {
        return this.genAttempts;
    }

    public int getGenAttempts(Random rand)
    {
        int attempts = this.genAttempts;

        if(this.genProbability > 0.0F && this.genProbability < 1.0F && rand.nextFloat() > this.genProbability)
        {
            attempts = 0;
        }
        if(this.randomizeGenAttempts)
        {
            attempts = NumberHelper.getNumberInRange(1, attempts, rand);
        }

        return attempts;
    }

    public float getGenProbability()
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
