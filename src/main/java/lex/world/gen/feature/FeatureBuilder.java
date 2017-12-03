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
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class FeatureBuilder extends IForgeRegistryEntry.Impl<FeatureBuilder>
{
    protected int generationAttempts;
    protected boolean randomizeGenerationAttempts;
    protected float generationProbability;
    protected int minHeight;
    protected int maxHeight;

    public FeatureBuilder configure(IConfig config)
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

        return this;
    }

    public abstract IFeature create();
}
