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

package logictechcorp.libraryex.world.generation;

public enum GenerationStage
{
    TERRAIN_ALTERATION("terrain_alteration"),
    DECORATION("decoration"),
    PLANT_DECORATION("plant_decoration"),
    ORE("ore"),
    STRUCTURE("structure");

    private String identifier;

    GenerationStage(String identifier)
    {
        this.identifier = identifier;
    }

    public static GenerationStage getFromIdentifier(String identifier)
    {
        for(GenerationStage stage : values())
        {
            if(stage.toString().equals(identifier))
            {
                return stage;
            }
        }

        return DECORATION;
    }

    @Override
    public String toString()
    {
        return this.identifier;
    }
}