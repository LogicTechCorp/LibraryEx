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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldtteam.datagenerators.recipes.RecipeIngredientJson;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import com.ldtteam.datagenerators.recipes.RecipeResultJson;
import com.ldtteam.datagenerators.recipes.shaped.ShapedPatternJson;
import com.ldtteam.datagenerators.recipes.shaped.ShapedRecipeJson;
import com.ldtteam.datagenerators.recipes.shapeless.ShaplessRecipeJson;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class RecipeGenerator implements IDataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;
    private final String modId;
    private final List<ShapedRecipeJson> shapedRecipeJsons;
    private final List<ShaplessRecipeJson> shapelessRecipeJsons;
    private final Set<String> recipeNames;

    public RecipeGenerator(DataGenerator generator, String modId)
    {
        this.generator = generator;
        this.modId = modId;
        this.shapedRecipeJsons = new ArrayList<>();
        this.shapelessRecipeJsons = new ArrayList<>();
        this.recipeNames = new HashSet<>();
    }

    @Override
    public void act(DirectoryCache cache)
    {
        this.shapedRecipeJsons.forEach(recipeJson ->
        {
            Path recipePath = this.generator.getOutputFolder().resolve("data/" + this.modId + "/recipes/").resolve(this.getRecipeName(recipeJson.getResult().getItem()) + ".json");

            try
            {
                IDataProvider.save(GSON, cache, recipeJson.serialize(), recipePath);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        });
        this.shapelessRecipeJsons.forEach(recipeJson ->
        {
            Path recipePath = this.generator.getOutputFolder().resolve("data/" + this.modId + "/recipes/").resolve(this.getRecipeName(recipeJson.getResult().getItem()) + ".json");

            try
            {
                IDataProvider.save(GSON, cache, recipeJson.serialize(), recipePath);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    public ShapedRecipeBuilder createShapedRecipe(ItemStack result, String group)
    {
        return new ShapedRecipeBuilder(this, result, group);
    }

    public ShapelessRecipeBuilder createShapelessRecipe(ItemStack result, String group)
    {
        return new ShapelessRecipeBuilder(this, result, group);
    }

    private String getRecipeName(String name)
    {
        String baseRegistryName = this.modId + ":" + name;
        String retRegistryName = baseRegistryName;
        int index = 0;

        while(this.recipeNames.contains(retRegistryName))
        {
            index++;
            retRegistryName = this.modId + ":" + baseRegistryName + "_" + index;
        }

        this.recipeNames.add(retRegistryName);
        return retRegistryName;
    }

    @Override
    public String getName()
    {
        return this.modId + " Recipe Generator";
    }

    public List<ShapedRecipeJson> getShapedRecipeJsons()
    {
        return this.shapedRecipeJsons;
    }

    public List<ShaplessRecipeJson> getShapelessRecipeJsons()
    {
        return this.shapelessRecipeJsons;
    }

    private class ShapedRecipeBuilder
    {
        private final RecipeGenerator recipeGenerator;
        private final List<String> patterns;
        private final Map<String, RecipeIngredientKeyJson> keys;
        private final ItemStack result;
        private final String group;

        private ShapedRecipeBuilder(RecipeGenerator recipeGenerator, ItemStack result, String group)
        {
            this.recipeGenerator = recipeGenerator;
            this.patterns = new ArrayList<>();
            this.keys = new HashMap<>();
            this.result = result;
            this.group = group;
        }

        public ShapedRecipeBuilder pattern(String pattern)
        {
            if(pattern.length() > 3 || this.patterns.size() > 3)
            {
                throw new IndexOutOfBoundsException("There are too many rows for this recipe");
            }

            this.patterns.add(pattern);
            return this;
        }

        public ShapedRecipeBuilder key(char key, Item item)
        {
            this.keys.put(Character.toString(key), new RecipeIngredientKeyJson(new RecipeIngredientJson(item.getRegistryName().toString(), false)));
            return this;
        }

        public ShapedRecipeBuilder key(char key, Tag<?> tag)
        {
            this.keys.put(Character.toString(key), new RecipeIngredientKeyJson(new RecipeIngredientJson(tag.getId().toString(), true)));
            return this;
        }

        public RecipeGenerator build()
        {
            ShapedPatternJson patternJson = new ShapedPatternJson();
            patternJson.setPartA(this.patterns.get(0));
            patternJson.setPartB(this.patterns.get(1));

            if(this.patterns.size() > 2)
            {
                patternJson.setPartB(this.patterns.get(2));
            }

            this.recipeGenerator.getShapedRecipeJsons().add(new ShapedRecipeJson(this.group, patternJson, this.keys, new RecipeResultJson(this.result.getCount(), this.result.getItem().getRegistryName().toString())));
            return this.recipeGenerator;
        }
    }

    private class ShapelessRecipeBuilder
    {
        private final RecipeGenerator recipeGenerator;
        private final List<RecipeIngredientKeyJson> keys;
        private final ItemStack result;
        private final String group;

        private ShapelessRecipeBuilder(RecipeGenerator recipeGenerator, ItemStack result, String group)
        {
            this.recipeGenerator = recipeGenerator;
            this.keys = new ArrayList<>();
            this.result = result;
            this.group = group;
        }

        public ShapelessRecipeBuilder key(Item item)
        {
            this.keys.add(new RecipeIngredientKeyJson(new RecipeIngredientJson(item.getRegistryName().toString(), false)));
            return this;
        }

        public ShapelessRecipeBuilder key(Tag<?> tag)
        {
            this.keys.add(new RecipeIngredientKeyJson(new RecipeIngredientJson(tag.getId().toString(), true)));
            return this;
        }

        public RecipeGenerator build()
        {
            this.recipeGenerator.getShapelessRecipeJsons().add(new ShaplessRecipeJson(this.group, this.keys, new RecipeResultJson(this.result.getCount(), this.result.getItem().getRegistryName().toString())));
            return this.recipeGenerator;
        }
    }
}
