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

package logictechcorp.libraryex.multiblock;

import logictechcorp.libraryex.utility.BlockHelper;
import net.minecraft.block.state.IBlockState;

public class PatternElementOre extends PatternElement
{
    private final String[] ores;

    PatternElementOre(IBlockState state, char identifier, String... ores)
    {
        super(state, identifier);
        this.ores = ores;
    }

    @Override
    public boolean matches(IBlockState state)
    {
        for(String ore : this.ores)
        {
            if(BlockHelper.isOreDict(state.getBlock(), ore))
            {
                return true;
            }
        }

        return super.matches(state);
    }
}
