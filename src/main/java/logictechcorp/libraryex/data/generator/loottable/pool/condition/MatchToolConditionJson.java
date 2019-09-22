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

package logictechcorp.libraryex.data.generator.loottable.pool.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ldtteam.datagenerators.loot_table.pool.conditions.IPoolCondition;
import com.ldtteam.datagenerators.loot_table.pool.conditions.PoolConditions;
import net.minecraft.advancements.criterion.ItemPredicate;

public class MatchToolConditionJson implements IPoolCondition
{
    public static final String NAME = "minecraft:match_tool";

    static
    {
        PoolConditions.POOL_CONDITIONS.put(NAME, MatchToolConditionJson::new);
    }

    private ItemPredicate predicate;

    public MatchToolConditionJson()
    {
        this.predicate = ItemPredicate.ANY;
    }

    public void setPredicate(ItemPredicate predicate)
    {
        this.predicate = predicate;
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("condition", NAME);
        jsonObject.add("predicate", this.predicate.serialize());
        return jsonObject;
    }

    @Override
    public void deserialize(JsonElement jsonElement)
    {
        if(jsonElement instanceof JsonObject)
        {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if(!jsonObject.has("predicate"))
            {
                jsonObject.add("predicate", ItemPredicate.ANY.serialize());
            }

            this.predicate = ItemPredicate.deserialize(jsonObject.get("predicate"));
        }

        this.predicate = ItemPredicate.ANY;
    }
}
