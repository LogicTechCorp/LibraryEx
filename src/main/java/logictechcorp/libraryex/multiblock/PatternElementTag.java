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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.Tag;

public class PatternElementTag extends PatternElement
{
    private Tag<Block>[] tags;

    PatternElementTag(BlockState state, char identifier, Tag<Block>... tags)
    {
        super(state, identifier);
        this.tags = tags;
    }

    @Override
    public boolean matches(BlockState state)
    {
        for(Tag<Block> tag : this.tags)
        {
            if(state.isIn(tag))
            {
                return true;
            }
        }

        return super.matches(state);
    }
}
