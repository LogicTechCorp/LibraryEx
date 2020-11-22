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

import net.minecraft.util.WeightedRandom;

import java.util.List;
import java.util.Random;

public class WeightedHelper
{
    public static NamedItem getRandomNamedItem(Random random, List<NamedItem> variants)
    {
        return WeightedRandom.getRandomItem(random, variants);
    }

    public static class NamedItem extends WeightedRandom.Item
    {
        private final String name;

        public NamedItem(String name, int weight)
        {
            super(weight);
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
