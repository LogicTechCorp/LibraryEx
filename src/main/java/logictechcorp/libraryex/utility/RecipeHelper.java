/*
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 *
 * Original: https://github.com/Glitchfiend/BiomesOPlenty/blob/30c8762cdb2572e21847524d8050e43dcb09b95f/src/main/java/biomesoplenty/common/util/inventory/CraftingUtil.java
 * (Edited to work with multiple mods)
 */

package logictechcorp.libraryex.utility;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import logictechcorp.libraryex.LibraryEx;
import logictechcorp.libraryex.api.IModData;
import logictechcorp.libraryex.item.crafting.RecipeRepairItemMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RecipeHelper
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> USED_REGISTRY_NAMES = new HashSet<>();
    private static final Map<OreIngredient, String> ORE_INGREDIENTS = new HashMap<>();

    private static IRecipe createShapelessRecipe(IModData data, ItemStack output, Object... inputs)
    {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for(Object input : inputs)
        {
            Ingredient ingredient = CraftingHelper.getIngredient(input);

            if(ingredient instanceof OreIngredient)
            {
                ORE_INGREDIENTS.put((OreIngredient) ingredient, (String) input);
            }

            ingredients.add(ingredient);
        }

        if(ingredients.isEmpty())
        {
            throw new IllegalArgumentException("No ingredients for shapeless recipe!");
        }
        else if(ingredients.size() > 9)
        {
            throw new IllegalArgumentException("Too many ingredients for shapeless recipe!");
        }

        return new ShapelessRecipes(data.getModId(), output, ingredients).setRegistryName(generateRegistryName(data, output));
    }

    private static IRecipe createShapedRecipe(IModData data, ItemStack output, Object... inputs)
    {
        ArrayList<String> pattern = new ArrayList<>();
        Map<String, Ingredient> key = new HashMap<>();
        Iterator inputItr = Arrays.asList(inputs).iterator();

        while(inputItr.hasNext())
        {
            Object object = inputItr.next();

            if(object instanceof String)
            {
                String string = (String) object;

                if(string.length() > 3)
                {
                    throw new IllegalArgumentException("Invalid string length for recipe " + string.length());
                }

                if(pattern.size() <= 2)
                {
                    pattern.add(string);
                }
                else
                {
                    throw new IllegalArgumentException("Recipe has too many crafting rows!");
                }
            }
            else if(object instanceof Character)
            {
                key.put(((Character) object).toString(), CraftingHelper.getIngredient(inputItr.next()));
            }
            else
            {
                throw new IllegalArgumentException("Unexpected argument of type " + object.getClass().toString());
            }
        }

        int width = pattern.get(0).length();
        int height = pattern.size();
        key.put(" ", Ingredient.EMPTY);

        NonNullList<Ingredient> ingredients = deserializeIngredients(pattern.toArray(new String[0]), key, width, height);
        return new ShapedRecipes(data.getModId(), width, height, ingredients, output).setRegistryName(generateRegistryName(data, output));
    }

    private static IRecipe createRepairRecipe(IModData data, ItemStack output, Object input, int repairAmount)
    {
        Ingredient ingredient = CraftingHelper.getIngredient(input);

        if(ingredient == null)
        {
            throw new IllegalArgumentException("Invalid type for ingredient " + input.getClass().toString());
        }

        if(ingredient instanceof OreIngredient)
        {
            ORE_INGREDIENTS.put((OreIngredient) ingredient, (String) input);
        }

        return new RecipeRepairItemMod(output, ingredient, repairAmount).setRegistryName(generateRegistryName(data, output));
    }

    private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight)
    {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = new HashSet<>(keys.keySet());
        set.remove(" ");

        for(int i = 0; i < pattern.length; i++)
        {
            for(int j = 0; j < pattern[i].length(); j++)
            {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);

                if(ingredient == null)
                {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                ingredients.set(j + patternWidth * i, ingredient);
            }
        }

        if(!set.isEmpty())
        {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        else
        {
            return ingredients;
        }
    }

    private static ResourceLocation generateRegistryName(IModData data, ItemStack output)
    {
        ResourceLocation baseRegistryName = new ResourceLocation(data.getModId() + ":" + output.getItem().getRegistryName().getPath());
        ResourceLocation retRegistryName = baseRegistryName;
        int index = 0;

        while(USED_REGISTRY_NAMES.contains(retRegistryName.toString()))
        {
            index++;
            retRegistryName = new ResourceLocation(data.getModId() + ":" + baseRegistryName.getPath() + "_" + index);
        }

        USED_REGISTRY_NAMES.add(retRegistryName.toString());
        return retRegistryName;
    }

    private static void writeRecipeJson(IRecipe recipe)
    {
        ResourceLocation registryName = recipe.getRegistryName();

        if(recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes)
        {
            String json = createRecipeJson(recipe);
            File recipeFile = new File(LibraryEx.CONFIG_DIRECTORY, registryName.getNamespace() + "/recipes/" + registryName.getPath() + ".json");

            try
            {
                FileUtils.writeStringToFile(recipeFile, json, StandardCharsets.US_ASCII);
            }
            catch(Exception e)
            {
                LibraryEx.LOGGER.error("Could not write recipe to file " + recipeFile.getName());
            }
        }
    }

    private static String createRecipeJson(IRecipe recipe)
    {
        JsonObject root = new JsonObject();

        if(!recipe.getGroup().isEmpty())
        {
            root.add("group", new JsonPrimitive(recipe.getGroup()));
        }

        if(recipe instanceof ShapedRecipes)
        {
            ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
            BiMap<String, Ingredient> keyMap = createShapedRecipeKey(recipe.getIngredients());
            String[] pattern = createShapedPattern(shapedRecipe, keyMap);

            String type = "minecraft:crafting_shaped";

            JsonArray patternJsonArray = new JsonArray();

            for(String row : pattern)
            {
                patternJsonArray.add(row);
            }

            JsonObject keyObj = new JsonObject();

            for(String key : keyMap.keySet())
            {
                Ingredient ingredient = keyMap.get(key);

                if(ingredient instanceof OreIngredient)
                {
                    type = "forge:ore_shaped";
                }

                keyObj.add(key, createIngredientJson(ingredient));
            }

            root.add("type", new JsonPrimitive(type));
            root.add("pattern", patternJsonArray);
            root.add("key", keyObj);
        }
        else if(recipe instanceof ShapelessRecipes)
        {
            String type = "minecraft:crafting_shapeless";

            JsonArray ingredientsArray = new JsonArray();

            for(Ingredient ingredient : recipe.getIngredients())
            {
                if(ingredient.getMatchingStacks().length > 1 && !(ingredient instanceof OreIngredient))
                {
                    throw new IllegalArgumentException("Cannot create key for ingredient matching multiple stacks!");
                }
                else if(ingredient.getMatchingStacks().length == 0)
                {
                    continue;
                }

                if(ingredient instanceof OreIngredient)
                {
                    type = "forge:ore_shapeless";
                }

                ingredientsArray.add(createIngredientJson(ingredient));
            }

            root.add("type", new JsonPrimitive(type));
            root.add("ingredients", ingredientsArray);
        }
        root.add("result", createItemStackJson(recipe.getRecipeOutput()));

        return GSON.toJson(root);
    }

    private static JsonObject createIngredientJson(Ingredient ingredient)
    {
        JsonObject ret;

        if(ingredient instanceof OreIngredient)
        {
            ret = new JsonObject();
            ret.add("type", new JsonPrimitive("forge:ore_dict"));
            ret.add("ore", new JsonPrimitive(ORE_INGREDIENTS.get(ingredient)));
        }
        else
        {
            ret = createItemStackJson(ingredient.getMatchingStacks()[0]);
        }

        return ret;
    }

    private static JsonObject createItemStackJson(ItemStack stack)
    {
        JsonObject ret = new JsonObject();
        ret.add("item", new JsonPrimitive(stack.getItem().getRegistryName().toString()));

        if(stack.getCount() != 1)
        {
            ret.add("count", new JsonPrimitive(stack.getCount()));
        }
        if(stack.getMetadata() != 0 || stack.getHasSubtypes())
        {
            ret.add("data", new JsonPrimitive(stack.getMetadata()));
        }

        return ret;
    }

    private static String[] createShapedPattern(ShapedRecipes recipe, BiMap<String, Ingredient> keyMap)
    {
        String[] pattern = new String[recipe.recipeHeight];

        for(int i = 0; i < pattern.length; i++)
        {
            pattern[i] = "";
        }

        if(keyMap.isEmpty())
        {
            return pattern;
        }

        for(int i = 0; i < recipe.getIngredients().size(); i++)
        {
            Ingredient ingredient = recipe.getIngredients().get(i);
            String key = " ";

            if(ingredient.getMatchingStacks().length > 1 && !(ingredient instanceof OreIngredient))
            {
                throw new IllegalArgumentException("Cannot generate json for a recipe matching multiple stacks!");
            }
            else if(ingredient.getMatchingStacks().length != 0)
            {
                key = keyMap.inverse().get(ingredient);
            }

            int row = i / recipe.recipeWidth;
            pattern[row] += key;
        }

        return pattern;
    }

    private static BiMap<String, Ingredient> createShapedRecipeKey(NonNullList<Ingredient> ingredients)
    {
        Set<Ingredient> ingredientsSet = new HashSet<>(ingredients);
        BiMap<String, Ingredient> key = HashBiMap.create();
        int fallbackCount = 0;

        for(Ingredient ingredient : ingredientsSet)
        {
            if(ingredient.getMatchingStacks().length > 1 && !(ingredient instanceof OreIngredient))
            {
                throw new IllegalArgumentException("Cannot create key for ingredient matching multiple stacks!");
            }
            else if(ingredient.getMatchingStacks().length == 0)
            {
                continue;
            }

            if(fallbackCount == 0)
            {
                key.put(getFallbackRecipeKey(fallbackCount), ingredient);
                fallbackCount++;
                continue;
            }

            String letterKey = ingredient.getMatchingStacks()[0].getItem().getRegistryName().getPath().substring(0, 1).toUpperCase();

            if(!key.containsKey(letterKey))
            {
                key.put(letterKey, ingredient);
            }
            else
            {
                String fallbackKey = getFallbackRecipeKey(fallbackCount);

                if(key.containsKey(fallbackKey))
                {
                    throw new RuntimeException("Fallback key " + fallbackKey + " is already present!");
                }

                key.put(fallbackKey, ingredient);
                fallbackCount++;
            }
        }

        return key;
    }

    private static String getFallbackRecipeKey(int index)
    {
        switch(index)
        {
            case 1:
                return "*";
            case 2:
                return "@";
            case 3:
                return "%";
            case 4:
                return "+";
            case 5:
                return "-";
            case 6:
                return "~";
            case 7:
                return "=";
            case 8:
                return "?";
            default:
                return "#";
        }
    }

    public static IRecipe addShapedRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return createShapedRecipe(data, output, inputs);
    }

    public static IRecipe addShapelessRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return createShapelessRecipe(data, output, inputs);
    }

    public static IRecipe addRepairRecipe(IModData data, ItemStack output, Object ingredient, int repairAmount)
    {
        return createRepairRecipe(data, output, ingredient, repairAmount);
    }

    public static void addSmelting(ItemStack output, ItemStack input, float xp)
    {
        FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }

    public static void addBrewing(PotionType input, Item reagent, PotionType output)
    {
        PotionHelper.addMix(input, reagent, output);
    }

    public static void addBrewing(PotionType input, Ingredient ingredient, PotionType output)
    {
        PotionHelper.addMix(input, ingredient, output);
    }

    public static IRecipe add1x2Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "#", "#", '#', input);
    }

    public static IRecipe add1x3Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "#", "#", "#", '#', input);
    }

    public static IRecipe add2x1Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "##", '#', input);
    }

    public static IRecipe add2x2Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "##", "##", '#', input);
    }

    public static IRecipe add2x3Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "##", "##", "##", '#', input);
    }

    public static IRecipe add3x1Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "###", '#', input);
    }

    public static IRecipe add3x2Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "###", "###", '#', input);
    }

    public static IRecipe add3x3Recipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "###", "###", "###", '#', input);
    }

    public static IRecipe addSurroundedRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, "###", "#*#", "###", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addCrossRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, " # ", "###", " # ", '#', input);
    }

    public static IRecipe addFilledCrossRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, " # ", "#*#", " # ", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addStairRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "#  ", "## ", "###", '#', input);
    }

    public static IRecipe addFenceRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, "#*#", "#*#", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addWallRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "###", "###", '#', input);
    }

    public static IRecipe addSwordRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, " # ", " # ", " * ", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addPickaxeRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, "###", " * ", " * ", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addShovelRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, " # ", " * ", " * ", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addAxeRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, "##", "#*", " *", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addHoeRecipe(IModData data, ItemStack output, Object... inputs)
    {
        return addShapedRecipe(data, output, "##", " *", " *", '#', inputs[0], '*', inputs[1]);
    }

    public static IRecipe addHelmetRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "###", "# #", '#', input);
    }

    public static IRecipe addChestplateRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "# #", "###", "###", '#', input);
    }

    public static IRecipe addLeggingsRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "###", "# #", "# #", '#', input);
    }

    public static IRecipe addBootsRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "# #", "# #", '#', input);
    }

    public static IRecipe addBoatRecipe(IModData data, ItemStack output, Object input)
    {
        return addShapedRecipe(data, output, "# #", "###", '#', input);
    }
}
