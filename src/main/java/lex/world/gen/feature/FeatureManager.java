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

import java.util.HashMap;
import java.util.Map;

public class FeatureManager
{
    private static final Map<String, IFeatureBuilder> FEATURE_BUILDER_MAP = new HashMap<>();

    public static void addFeatureBuilder(String key, IFeatureBuilder builder)
    {
        if(!FEATURE_BUILDER_MAP.containsKey(key) && builder != null)
        {
            FEATURE_BUILDER_MAP.put(key, builder);
        }
    }

    public static IFeature createFeature(String key, IConfig config)
    {
        if(FEATURE_BUILDER_MAP.containsKey(key) && config != null)
        {
            return FEATURE_BUILDER_MAP.get(key).configure(config).create();
        }

        return null;
    }

    static
    {
        addFeatureBuilder("scattered", new FeatureScattered.Builder());
    }
}
