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

package lex.pattern;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Row implements IRow
{
    protected List<Character> sections;

    public Row()
    {
        this(new ArrayList<>());
    }

    public Row(List<Character> sectionsIn)
    {
        sections = sectionsIn;
    }

    @Override
    public void addSection(char character)
    {
        sections.add(character);
    }

    @Override
    public List<Character> getSections()
    {
        return ImmutableList.copyOf(sections);
    }
}
