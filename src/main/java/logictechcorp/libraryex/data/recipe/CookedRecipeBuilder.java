package logictechcorp.libraryex.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class CookedRecipeBuilder implements ISerializableRecipe
{
    private final CookingRecipeSerializer<?> serializer;
    private final Item result;
    private Ingredient ingredient;
    private float experience;
    private int cookTime;
    private String group;
    private String name;

    CookedRecipeBuilder(CookingRecipeSerializer<?> serializer, IItemProvider itemProvider)
    {
        this.serializer = serializer;
        this.result = itemProvider.asItem();
    }

    public CookedRecipeBuilder ingredient(IItemProvider itemProvider)
    {
        if(this.ingredient != null)
        {
            throw new IllegalArgumentException("The ingredient cannot be set as it was already defined");
        }

        this.ingredient = Ingredient.fromItems(itemProvider);
        return this;
    }

    public CookedRecipeBuilder experience(float experience)
    {
        this.experience = experience;
        return this;
    }

    public CookedRecipeBuilder cookTime(int cookTime)
    {
        this.cookTime = cookTime;
        return this;
    }

    public CookedRecipeBuilder group(String group)
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

        jsonObject.add("ingredient", this.ingredient.serialize());
        jsonObject.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
        jsonObject.addProperty("experience", this.experience);
        jsonObject.addProperty("cookingtime", this.cookTime);
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
        return this.serializer;
    }
}
