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

package logictechcorp.libraryex.utility;

import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;

public class BlockStateHelper
{
    public static IProperty getProperty(BlockState state, String propertyName)
    {
        for(IProperty property : state.getProperties())
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
        for(Object value : property.getAllowedValues())
        {
            if(value.toString().equalsIgnoreCase(propertyValue))
            {
                return value instanceof Comparable ? (Comparable) value : null;
            }
        }

        return null;
    }
}
