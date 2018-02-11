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

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper
{
    public static IProperty getProperty(IBlockState state, String propertyName)
    {
        for(IProperty property : state.getProperties().keySet())
        {
            if(property.getName().equalsIgnoreCase(propertyName))
            {
                return property;
            }
        }

        return null;
    }

    public static Comparable getPropertyValue(IProperty property, String propertyValue)
    {
        for(Comparable value : (ImmutableSet<Comparable>) property.getAllowedValues())
        {
            if(value.toString().equalsIgnoreCase(propertyValue))
            {
                return value;
            }
        }

        return null;
    }
}
