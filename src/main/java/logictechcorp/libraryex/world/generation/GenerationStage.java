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

import logictechcorp.libraryex.api.world.generation.IGeneratorStage;

public enum GenerationStage implements IGeneratorStage
{
    PRE_DECORATE("pre_decorate"),
    DECORATE("decorate"),
    POST_DECORATE("post_decorate"),
    PRE_ORE("pre_ore"),
    ORE("ore"),
    POST_ORE("post_ore");

    private String identifier;

    GenerationStage(String identifier)
    {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier()
    {
        return this.identifier;
    }
}
