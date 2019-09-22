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

public class ApplyBonusFunctionJson implements IEntryFunction
{
    public static final String NAME = "minecraft:apply_bonus";

    static
    {
        EntryFunctions.ENTRY_FUNCTIONS.put(NAME, ApplyBonusFunctionJson::new);
    }

    private String enchantment;
    private String formula;

    public ApplyBonusFunctionJson()
    {
        this.enchantment = "";
        this.formula = "";
    }

    public void setEnchantment(String enchantment)
    {
        this.enchantment = enchantment;
    }

    public void setFormula(String formula)
    {
        this.formula = formula;
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("function", NAME);
        jsonObject.addProperty("enchantment", this.enchantment);
        jsonObject.addProperty("formula", this.formula);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonElement jsonElement)
    {
        if(jsonElement instanceof JsonObject)
        {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            this.enchantment = jsonObject.get("enchantment").getAsString();
            this.formula = jsonObject.get("formula").getAsString();
        }
    }
}
