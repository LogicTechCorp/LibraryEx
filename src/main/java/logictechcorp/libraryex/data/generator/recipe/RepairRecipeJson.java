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

package logictechcorp.libraryex.data.generator.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ldtteam.datagenerators.IJsonSerializable;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import com.ldtteam.datagenerators.recipes.RecipeResultJson;
import logictechcorp.libraryex.LibraryEx;
import net.minecraft.util.JSONUtils;

public class RepairRecipeJson implements IJsonSerializable
{
    private static final String TYPE = LibraryEx.MOD_ID + ":crafting_repair";

    private RecipeResultJson result = new RecipeResultJson();
    private RecipeIngredientKeyJson ingredient = new RecipeIngredientKeyJson();
    private int repairAmount = 1;

    public RepairRecipeJson()
    {
    }

    public RepairRecipeJson(RecipeResultJson result, RecipeIngredientKeyJson ingredient, int repairAmount)
    {
        this.result = result;
        this.ingredient = ingredient;
        this.repairAmount = repairAmount;
    }

    @Override
    public JsonElement serialize()
    {
        JsonObject returnValue = new JsonObject();
        returnValue.addProperty("type", TYPE);
        returnValue.add("broken_item", this.result.serialize());
        returnValue.add("repair_item", this.ingredient.serialize());
        returnValue.add("repair_amount", new JsonPrimitive(this.repairAmount));
        return returnValue;

    }

    @Override
    public void deserialize(JsonElement jsonElement)
    {
        JsonObject recipeJson = jsonElement.getAsJsonObject();
        this.result.deserialize(recipeJson.get("result"));
        this.ingredient.deserialize(recipeJson.get("result"));
        this.repairAmount = JSONUtils.getInt(recipeJson, "repair_amount");
    }

    public RecipeResultJson getResult()
    {
        return this.result;
    }
}
