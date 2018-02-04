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

package lex.api.pattern;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Layer implements ILayer
{
    protected List<IRow> rows;

    public Layer()
    {
        this(new ArrayList<>());
    }

    public Layer(List<IRow> rowsIn)
    {
        rows = rowsIn;
    }

    @Override
    public void addRow(IRow row)
    {
        rows.add(row);
    }

    @Override
    public List<IRow> getRows()
    {
        return ImmutableList.copyOf(rows);
    }
}
