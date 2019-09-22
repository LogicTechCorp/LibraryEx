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

package logictechcorp.libraryex.data.generator.loottable.pool.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ldtteam.datagenerators.loot_table.pool.conditions.IPoolCondition;
import com.ldtteam.datagenerators.loot_table.pool.conditions.PoolConditions;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryTypeEnum;
import com.ldtteam.datagenerators.loot_table.pool.entry.children.EntryChildren;
import com.ldtteam.datagenerators.loot_table.pool.entry.children.IEntryChild;
import com.ldtteam.datagenerators.loot_table.pool.entry.functions.EntryFunctions;
import com.ldtteam.datagenerators.loot_table.pool.entry.functions.IEntryFunction;

import java.util.ArrayList;
import java.util.List;

public class ItemEntryChildJson implements IEntryChild
{
    static
    {
        EntryChildren.ENTRY_CHILDREN.put(EntryTypeEnum.ITEM, ItemEntryChildJson::new);
    }

    private List<IPoolCondition> conditions;
    private List<IEntryFunction> functions;
    private String name;

    public ItemEntryChildJson()
    {
        this.conditions = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.name = "";
    }

    public void setConditions(List<IPoolCondition> conditions)
    {
        this.conditions = conditions;
    }

    public void setFunctions(List<IEntryFunction> functions)
    {
        this.functions = functions;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject jsonObject = new JsonObject();
        JsonArray conditionArray = new JsonArray();
        JsonArray functionArray = new JsonArray();

        for(IPoolCondition condition : this.conditions)
        {
            conditionArray.add(condition.serialize());
        }

        for(IEntryFunction function : this.functions)
        {
            functionArray.add(function.serialize());
        }

        jsonObject.addProperty("type", EntryTypeEnum.ITEM.getName());

        if(conditionArray.size() > 0)
        {
            jsonObject.add("conditions", conditionArray);
        }
        if(functionArray.size() > 0)
        {
            jsonObject.add("functions", functionArray);
        }

        jsonObject.addProperty("name", this.name);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonElement jsonElement)
    {
        if(jsonElement instanceof JsonObject)
        {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if(jsonObject.has("conditions"))
            {
                JsonArray conditionArray = jsonObject.getAsJsonArray("conditions");

                for(JsonElement condition : conditionArray)
                {
                    if(condition instanceof JsonObject)
                    {
                        this.conditions.add(PoolConditions.deserializeCondition(jsonElement));
                    }
                }
            }

            if(jsonObject.has("functions"))
            {
                JsonArray functionArray = jsonObject.getAsJsonArray("functions");

                for(JsonElement function : functionArray)
                {
                    if(function instanceof JsonObject)
                    {
                        this.functions.add(EntryFunctions.deserializeFunction(jsonElement));
                    }
                }
            }

            this.name = jsonObject.get("name").getAsString();
        }
    }
}
