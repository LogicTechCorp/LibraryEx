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
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class BiomeWrapperManager
{
    private static final Map<Biome, BiomeWrapper> WRAPPED_BIOME_MAP = new HashMap<>();

    public static void wrapBiome(Biome biome, IConfig config)
    {
        if(!WRAPPED_BIOME_MAP.containsKey(biome))
        {
            WRAPPED_BIOME_MAP.put(biome, new BiomeWrapper(biome, config));
        }
    }

    public static BiomeWrapper getBiomeWrapper(Biome biome)
    {
        return WRAPPED_BIOME_MAP.get(biome);
    }
}
