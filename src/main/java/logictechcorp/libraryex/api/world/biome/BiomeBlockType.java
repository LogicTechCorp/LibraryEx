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

package logictechcorp.libraryex.api.world.biome;

public enum BiomeBlockType
{
    SURFACE_BLOCK("surfaceBlock"),
    SUBSURFACE_BLOCK("subsurfaceBlock"),
    CAVE_CEILING_BLOCK("caveCeilingBlock"),
    CAVE_WALL_BLOCK("caveWallBlock"),
    CAVE_FLOOR_BLOCK("caveFloorBlock"),
    FLUID_BLOCK("fluidBlock");

    private String identifier;

    BiomeBlockType(String identifier)
    {
        this.identifier = identifier;
    }

    public static BiomeBlockType getFromIdentifier(String identifier)
    {
        for(BiomeBlockType type : BiomeBlockType.values())
        {
            if(type.toString().equals(identifier))
            {
                return type;
            }
        }

        return SURFACE_BLOCK;
    }

    @Override
    public String toString()
    {
        return this.identifier;
    }
}
