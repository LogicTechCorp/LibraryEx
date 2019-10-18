package logictechcorp.libraryex.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipeBuilder implements ISerializableRecipe
{
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients;
    private String group;
    private String name;

    ShapelessRecipeBuilder(IItemProvider itemProvider, int count)
    {
        this.result = itemProvider.asItem();
        this.count = count;
        this.ingredients = new ArrayList<>();
    }

    public ShapelessRecipeBuilder ingredient(IItemProvider itemProvider, int amount)
    {
        Ingredient ingredient = Ingredient.fromItems(itemProvider);

        for(int i = 0; i < amount; i++)
        {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public ShapelessRecipeBuilder group(String group)
    {
        if(this.group != null)
        {
            throw new IllegalArgumentException("The group \"" + group + "\" cannot be set as it was already defined");
        }

        this.group = group;
        return this;
    }

    public ISerializableRecipe build(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject jsonObject = ISerializableRecipe.super.serialize();

        if(this.group != null && !this.group.isEmpty())
        {
            jsonObject.addProperty("group", this.group);
        }

        JsonArray ingredientArray = new JsonArray();

        for(Ingredient ingredient : this.ingredients)
        {
            ingredientArray.add(ingredient.serialize());
        }

        jsonObject.add("ingredients", ingredientArray);

        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());

        if(this.count > 1)
        {
            resultObject.addProperty("count", this.count);
        }

        jsonObject.add("result", resultObject);
        return jsonObject;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public IRecipeSerializer<?> getRecipeSerializer()
    {
        return IRecipeSerializer.CRAFTING_SHAPELESS;
    }
}
