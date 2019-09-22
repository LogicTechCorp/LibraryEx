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

package logictechcorp.libraryex.data.generator.loottable.pool.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ldtteam.datagenerators.loot_table.pool.entry.functions.EntryFunctions;
import com.ldtteam.datagenerators.loot_table.pool.entry.functions.IEntryFunction;

public class ExplosionDecayFunctionJson implements IEntryFunction
{
    public static final String NAME = "minecraft:explosion_decay";

    static
    {
        EntryFunctions.ENTRY_FUNCTIONS.put(NAME, ApplyBonusFunctionJson::new);
    }

    public ExplosionDecayFunctionJson()
    {
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("function", NAME);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonElement jsonElement)
    {
    }
}
