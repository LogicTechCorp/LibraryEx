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

import lex.api.config.IConfig;
import lex.api.world.gen.feature.IFeature;
import lex.util.NumberHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public abstract class Feature extends WorldGenerator implements IFeature
{
    protected int genAttempts;
    protected float genProbability;
    protected boolean randomizeGenAttempts;
    protected int minHeight;
    protected int maxHeight;

    public Feature(IConfig config)
    {
        genAttempts = config.getInt("genAttempts", 4);
        genProbability = config.getFloat("genProbability", 1.0F);
        randomizeGenAttempts = config.getBoolean("randomizeGenAttempts", false);
        minHeight = config.getInt("minHeight", 16);
        maxHeight = config.getInt("maxHeight", 112);
    }

    @Override
    public abstract boolean generate(World world, Random rand, BlockPos pos);

    @Override
    public int getGenAttempts()
    {
        return genAttempts;
    }

    @Override
    public int getGenAttempts(Random rand)
    {
        int attempts = genAttempts;

        if(genProbability > 0.0F && genProbability < 1.0F && rand.nextFloat() > genProbability)
        {
            attempts = 0;
        }
        if(randomizeGenAttempts)
        {
            attempts = NumberHelper.getNumberInRange(1, attempts, rand);
        }

        return attempts;
    }

    @Override
    public float getGenProbability()
    {
        return genProbability;
    }

    @Override
    public boolean randomizeGenAttempts()
    {
        return randomizeGenAttempts;
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
}
