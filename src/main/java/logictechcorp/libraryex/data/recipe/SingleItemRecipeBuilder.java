package logictechcorp.libraryex.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class SingleItemRecipeBuilder implements ISerializableRecipe
{
    private final IRecipeSerializer<?> serializer;
    private final Item result;
    private final int count;
    private Ingredient ingredient;
    private String group;
    private String name;

    SingleItemRecipeBuilder(IRecipeSerializer<?> serializer, IItemProvider itemProvider, int count)
    {
        this.serializer = serializer;
        this.result = itemProvider.asItem();
        this.count = count;
    }

    public SingleItemRecipeBuilder ingredient(IItemProvider itemProvider)
    {
        if(this.ingredient != null)
        {
            throw new IllegalArgumentException("The ingredient cannot be set as it was already defined");
        }

        this.ingredient = Ingredient.fromItems(itemProvider);
        return this;
    }

    public SingleItemRecipeBuilder group(String group)
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
        jsonObject.addProperty("count", this.count);
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
