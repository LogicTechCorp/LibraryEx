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

import java.util.Random;

public class RandomHelper
{
    private static final Random RANDOM = new Random();

    public static int getNumberInRange(int min, int max)
    {
        return getNumberInRange(min, max, RANDOM);
    }

    public static int getNumberInRange(int min, int max, Random random)
    {
        return random.nextInt(max - min + 1) + min;
    }

    public static <E extends Enum> E getRandomEnum(Class<? extends E> cls)
    {
        return getRandomEnum(cls, RANDOM);
    }

    public static <E extends Enum> E getRandomEnum(Class<? extends E> cls, Random random)
    {
        return cls.getEnumConstants()[random.nextInt(cls.getEnumConstants().length)];
    }

    public static Random getRandom()
    {
        return RANDOM;
    }
}
