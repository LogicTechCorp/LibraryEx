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

package lex.world.biome;

import lex.config.IConfig;

import java.util.HashMap;
import java.util.Map;

public class BiomeWrapperManager
{
    private static final Map<String, IBiomeWrapperBuilder> BIOME_WRAPPER_BUILDERS = new HashMap<>();

    public static void addBiomeWrapperBuilder(String key, IBiomeWrapperBuilder builder)
    {
        if(!BIOME_WRAPPER_BUILDERS.containsKey(key) && builder != null)
        {
            BIOME_WRAPPER_BUILDERS.put(key, builder);
        }
    }

    public static IBiomeWrapper createBiomeWrapper(String key, IConfig config)
    {
        if(BIOME_WRAPPER_BUILDERS.containsKey(key) && config != null)
        {
            return BIOME_WRAPPER_BUILDERS.get(key).configure(config).create();
        }

        return null;
    }

    static
    {
        addBiomeWrapperBuilder("overworld", new OverworldBiomeWrapper.Builder());
        addBiomeWrapperBuilder("nether", new NetherBiomeWrapper.Builder());
        addBiomeWrapperBuilder("end", new EndBiomeWrapper.Builder());
    }
}
