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

import com.google.gson.JsonElement;

public class ConfigHelper
{
    public static boolean isString(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isString();
    }

    public static boolean isInt(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isNumber();
    }

    public static boolean isFloat(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBoolean(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isPrimitive(JsonElement element)
    {
        return !isNull(element) && element.isJsonPrimitive();
    }

    public static boolean isObject(JsonElement element)
    {
        return !isNull(element) && element.isJsonObject();
    }

    public static boolean isArray(JsonElement element)
    {
        return !isNull(element) && element.isJsonArray();
    }

    public static boolean isNull(JsonElement element)
    {
        return element == null || element.isJsonNull();
    }
}
