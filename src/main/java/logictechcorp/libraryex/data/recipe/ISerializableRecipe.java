package logictechcorp.libraryex.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public interface ISerializableRecipe
{
    default JsonObject serialize()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", ForgeRegistries.RECIPE_SERIALIZERS.getKey(this.getRecipeSerializer()).toString());
        return jsonObject;
    }

    default boolean isValid()
    {
        return this.getName() != null && !this.getName().isEmpty();
    }

    String getName();

    IRecipeSerializer<?> getRecipeSerializer();
}
