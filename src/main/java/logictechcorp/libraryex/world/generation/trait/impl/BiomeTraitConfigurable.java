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
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTraitConfigurable;

/**
 * The base class for a biome trait that can be configured from json.
 */
public abstract class BiomeTraitConfigurable extends BiomeTrait implements IBiomeTraitConfigurable
{
    public BiomeTraitConfigurable(int generationAttempts, boolean randomizeGenerationAttempts, double generationProbability, int minimumGenerationHeight, int maximumGenerationHeight)
    {
        super(generationAttempts, randomizeGenerationAttempts, generationProbability, minimumGenerationHeight, maximumGenerationHeight);
    }

    public BiomeTraitConfigurable(Builder builder)
    {
        super(builder);
    }

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
}
