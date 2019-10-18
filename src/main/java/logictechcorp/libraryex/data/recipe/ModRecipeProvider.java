package logictechcorp.libraryex.data.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logictechcorp.libraryex.item.crafting.LibraryExRecipeSerializers;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModRecipeProvider implements IDataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final DataGenerator generator;
    private final List<ISerializableRecipe> serializableRecipes;

    public ModRecipeProvider(String modId, DataGenerator generator)
    {
        this.modId = modId;
        this.generator = generator;
        this.serializableRecipes = new ArrayList<>();
    }

    @Override
    public void act(DirectoryCache cache)
    {
        this.serializableRecipes.stream().filter(ISerializableRecipe::isValid).forEach(recipe ->
        {
            Path recipePath = this.generator.getOutputFolder().resolve("data/" + this.modId + "/recipes/").resolve(recipe.getName() + ".json");

            try
            {
                IDataProvider.save(GSON, cache, recipe.serialize(), recipePath);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    public ShapedRecipeBuilder addShapedRecipe(IItemProvider itemProvider, int count)
    {
        ShapedRecipeBuilder recipe = new ShapedRecipeBuilder(itemProvider, count);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public ShapelessRecipeBuilder addShapelessRecipe(IItemProvider itemProvider, int count)
    {
        ShapelessRecipeBuilder recipe = new ShapelessRecipeBuilder(itemProvider, count);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public CookedRecipeBuilder addFurnaceRecipe(IItemProvider itemProvider)
    {
        CookedRecipeBuilder recipe = new CookedRecipeBuilder(IRecipeSerializer.SMELTING, itemProvider);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public CookedRecipeBuilder addBlastFurnaceRecipe(IItemProvider itemProvider)
    {
        CookedRecipeBuilder recipe = new CookedRecipeBuilder(IRecipeSerializer.BLASTING, itemProvider);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public CookedRecipeBuilder addSmokerRecipe(IItemProvider itemProvider)
    {
        CookedRecipeBuilder recipe = new CookedRecipeBuilder(IRecipeSerializer.SMOKING, itemProvider);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public CookedRecipeBuilder addCampfireRecipe(IItemProvider itemProvider)
    {
        CookedRecipeBuilder recipe = new CookedRecipeBuilder(IRecipeSerializer.CAMPFIRE_COOKING, itemProvider);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public SingleItemRecipeBuilder addStonecutterRecipe(IItemProvider itemProvider, int count)
    {
        SingleItemRecipeBuilder recipe = new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTING, itemProvider, count);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    public SingleItemRecipeBuilder addRepairRecipe(IItemProvider itemProvider, int count)
    {
        SingleItemRecipeBuilder recipe = new SingleItemRecipeBuilder(LibraryExRecipeSerializers.CRAFTING_REPAIR.get(), itemProvider, count);
        this.serializableRecipes.add(recipe);
        return recipe;
    }

    @Override
    public String getName()
    {
        return this.modId + " recipes";
    }
}
