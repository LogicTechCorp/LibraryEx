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

package lex.world.gen.structure;

import com.google.common.collect.ImmutableMap;
import lex.api.world.gen.structure.IStructure;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class StructureRegistry
{
    private static final Map<ResourceLocation, IStructure> STRUCTURES = new HashMap<>();

    public static IStructure registerStructure(IStructure structure)
    {
        if(!STRUCTURES.containsKey(structure.getName()))
        {
            STRUCTURES.put(structure.getName(), structure);
        }
        else
        {
            return getStructure(structure.getName());
        }

        return structure;
    }

    public static IStructure getStructure(ResourceLocation name)
    {
        return STRUCTURES.get(name);
    }

    public static Map<ResourceLocation, IStructure> getStructures()
    {
        return ImmutableMap.copyOf(STRUCTURES);
    }
}
