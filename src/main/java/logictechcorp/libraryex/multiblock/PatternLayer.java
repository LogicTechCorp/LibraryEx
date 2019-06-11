/*
 * Elemental Sciences 2
 * Copyright (c) by Jezza
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
 *
 * Original: https://github.com/Jezza/Elemental-Sciences-2/blob/a2a11fded297b5a27287bf82a672aa05943894da/es_common/me/jezzadabomb/es2/api/multiblock/DimensionalPattern.java
 * (Edited to work with multiple mods)
 */

package logictechcorp.libraryex.multiblock;

import logictechcorp.libraryex.api.tileentity.multiblock.IPatternComponent;

public class PatternLayer implements IPatternComponent
{
    private PatternRow[] rows;

    PatternLayer(PatternRow... rows)
    {
        this.rows = rows;
    }

    @Override
    public Type getType()
    {
        return Type.LAYER;
    }

    public int[] getRowLengths()
    {
        int[] lengths = new int[this.rows.length];

        for(int i = 0; i < this.rows.length; i++)
        {
            lengths[i] = this.rows[i].getSections().length();
        }

        return lengths;
    }

    public PatternRow[] getRows()
    {
        return this.rows;
    }
}
