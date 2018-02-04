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
import lex.api.world.gen.feature.Feature;

public abstract class FeatureLibEx extends Feature
{
    public FeatureLibEx(IConfig config)
    {
        super(config.getInt("genAttempts", 4), config.getFloat("genProbability", 1.0F), config.getBoolean("randomizeGenAttempts", false), config.getInt("minHeight", 16), config.getInt("maxHeight", 112));

    }
}
