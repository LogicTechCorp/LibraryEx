/*
 * LibraryEx
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
 *
 */

package logictechcorp.libraryex.block.properties;

import net.minecraft.util.IStringSerializable;

public enum SlabType implements IStringSerializable
{
    TOP("top"),
    BOTTOM("bottom"),
    DOUBLE("double");

    private final String name;

    SlabType(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public static SlabType fromMeta(int meta)
    {
        if(meta < 0 || meta >= values().length)
        {
            meta = 0;
        }

        return values()[meta];
    }
}
