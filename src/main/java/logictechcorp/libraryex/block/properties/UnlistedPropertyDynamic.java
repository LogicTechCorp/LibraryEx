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

package logictechcorp.libraryex.block.properties;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyDynamic implements IUnlistedProperty<IBlockState>
{
    @Override
    public String getName()
    {
        return "UnlistedPropertyDynamic";
    }

    @Override
    public boolean isValid(IBlockState value)
    {
        return true;
    }

    @Override
    public Class<IBlockState> getType()
    {
        return IBlockState.class;
    }

    @Override
    public String valueToString(IBlockState value)
    {
        return value.toString();
    }
}
