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

package lex.util;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class EntityHelper
{
    private static Map<Class<? extends EntityLivingBase>, String> RESOURCE_LOCATION_CACHE = new HashMap<>();

    public static String getEntityLocation(EntityLivingBase entity)
    {
        Class<? extends EntityLivingBase> cls = entity.getClass();
        return RESOURCE_LOCATION_CACHE.computeIfAbsent(cls, k -> {
            ResourceLocation location = EntityList.getKey(k);
            return location != null ? location.toString() : null;
        });
    }

    public static boolean contains(String[] haystack, String needle)
    {
        if(needle == null)
        {
            for(String hay : haystack)
            {
                if(hay == null)
                {
                    return true;
                }
            }
        }
        else
        {
            for(String hay : haystack)
            {
                if(needle.equals(hay))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
