package logictechcorp.libraryex.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipeBuilder implements ISerializableRecipe
{
    private final Item result;
    private final int count;
    private final List<String> patterns;
    private final Map<Character, Ingredient> keys;
    private String group;
    private String name;

    ShapedRecipeBuilder(IItemProvider itemProvider, int count)
    {
        this.result = itemProvider.asItem();
        this.count = count;
        this.patterns = new ArrayList<>();
        this.keys = new LinkedHashMap<>();
    }

    public ShapedRecipeBuilder pattern(String pattern)
    {
        if(!this.patterns.isEmpty() && pattern.length() != this.patterns.get(0).length())
        {
            throw new IllegalArgumentException("The pattern \"" + pattern + "\" does not have a width of " + this.patterns.get(0).length());
        }
        this.patterns.add(pattern);
        return this;
    }

    public ShapedRecipeBuilder key(char key, IItemProvider itemProvider)
    {
        if(this.keys.containsKey(key))
        {
            throw new IllegalArgumentException("The key '" + key + "' was already defined");
        }
        else if(key == ' ')
        {
            throw new IllegalArgumentException("The key '(whitespace)' cannot be used");
        }

        this.keys.put(key, Ingredient.fromItems(itemProvider));
        return this;
    }

    public ShapedRecipeBuilder group(String group)
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

        JsonArray patternArray = new JsonArray();

        for(String pattern : this.patterns)
        {
            patternArray.add(pattern);
        }

        jsonObject.add("pattern", patternArray);

        JsonObject keyObject = new JsonObject();

        for(Map.Entry<Character, Ingredient> entry : this.keys.entrySet())
        {
            keyObject.add(String.valueOf(entry.getKey()), entry.getValue().serialize());
        }

        jsonObject.add("key", keyObject);

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
        return IRecipeSerializer.CRAFTING_SHAPED;
    }
}
