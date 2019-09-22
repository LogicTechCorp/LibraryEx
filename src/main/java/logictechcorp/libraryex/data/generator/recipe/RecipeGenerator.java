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
import net.minecraft.block.Block;
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

    public ShapedRecipeBuilder createShapedRecipe(ItemStack output)
    {
        return new ShapedRecipeBuilder(this, output);
    }

    public ShapelessRecipeBuilder createShapelessRecipe(ItemStack output)
    {
        return new ShapelessRecipeBuilder(this, output);
    }

    public RecipeGenerator addShapelessRecipe(ItemStack output, Object input)
    {
        return this.createShapelessRecipe(output).key(input).build();
    }

    public RecipeGenerator addShapelessRecipe(ItemStack output, Object inputOne, Object inputTwo)
    {
        return this.createShapelessRecipe(output).key(inputOne).key(inputTwo).build();
    }

    public RecipeGenerator addShapelessRecipe(ItemStack output, Object inputOne, Object inputTwo, Object inputThree)
    {
        return this.createShapelessRecipe(output).key(inputOne).key(inputTwo).key(inputThree).build();
    }

    public RecipeGenerator add1x2Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("#").pattern("#").key('#', input).build();
    }

    public RecipeGenerator add1x3Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("#").pattern("#").pattern("#").key('#', input).build();
    }

    public RecipeGenerator add2x1Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("##").key('#', input).build();
    }

    public RecipeGenerator add2x2Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("##").pattern("##").key('#', input).build();
    }

    public RecipeGenerator add2x3Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("##").pattern("##").pattern("##").key('#', input).build();
    }

    public RecipeGenerator add3x1Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("###").key('#', input).build();
    }

    public RecipeGenerator add3x2Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("###").pattern("###").key('#', input).build();
    }

    public RecipeGenerator add3x3Recipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("###").pattern("###").pattern("###").key('#', input).build();
    }

    public RecipeGenerator addSurroundedRecipe(ItemStack output, Item surrounding, Item center)
    {
        return this.createShapedRecipe(output).pattern("###").pattern("#*#").pattern("###").key('#', surrounding).key('*', center).build();
    }

    public RecipeGenerator addCrossRecipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern(" # ").pattern("###").pattern(" # ").key('#', input).build();
    }

    public RecipeGenerator addFilledCrossRecipe(ItemStack output, Item surrounding, Item center)
    {
        return this.createShapedRecipe(output).pattern(" # ").pattern("#*#").pattern(" # ").key('#', surrounding).key('*', center).build();
    }

    public RecipeGenerator addStairRecipe(ItemStack output, Object input)
    {
        return this.createShapedRecipe(output).pattern("#  ").pattern("## ").pattern("###").key('#', input).build();
    }

    public RecipeGenerator addFenceRecipe(ItemStack output, Object sides, Object center)
    {
        return this.createShapedRecipe(output).pattern("#*#").pattern("#*#").key('#', sides).key('*', center).build();
    }

    public RecipeGenerator addWallRecipe(ItemStack output, Object input)
    {
        return this.add3x2Recipe(output, input);
    }

    private String getRecipeName(String name)
    {
        String baseRegistryName = name.substring(name.lastIndexOf(":") + 1);
        String retRegistryName = baseRegistryName;
        int index = 0;

        while(this.recipeNames.contains(retRegistryName))
        {
            index++;
            retRegistryName = baseRegistryName + "_" + index;
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

    public class ShapedRecipeBuilder
    {
        private final RecipeGenerator recipeGenerator;
        private final List<String> patterns;
        private final Map<String, RecipeIngredientKeyJson> keys;
        private final ItemStack output;
        private String group;

        private ShapedRecipeBuilder(RecipeGenerator recipeGenerator, ItemStack output)
        {
            this.recipeGenerator = recipeGenerator;
            this.patterns = new ArrayList<>();
            this.keys = new HashMap<>();
            this.output = output;
            this.group = null;
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

        public ShapedRecipeBuilder key(char key, Object input)
        {
            if(input instanceof Item)
            {
                return this.key(key, (Item)input);
            }
            else if(input instanceof Block)
            {
                return this.key(key, (Block) input);
            }
            else if(input instanceof Tag<?>)
            {
                return this.key(key, (Tag<?>) input);
            }

            return this;
        }

        public ShapedRecipeBuilder key(char key, Item item)
        {
            this.keys.put(Character.toString(key), new RecipeIngredientKeyJson(new RecipeIngredientJson(item.getRegistryName().toString(), false)));
            return this;
        }

        public ShapedRecipeBuilder key(char key, Block block)
        {
            this.keys.put(Character.toString(key), new RecipeIngredientKeyJson(new RecipeIngredientJson(block.getRegistryName().toString(), false)));
            return this;
        }

        public ShapedRecipeBuilder key(char key, Tag<?> tag)
        {
            this.keys.put(Character.toString(key), new RecipeIngredientKeyJson(new RecipeIngredientJson(tag.getId().toString(), true)));
            return this;
        }
        
        public ShapedRecipeBuilder group(String group)
        {
            this.group = group;
            return this;
        }

        public RecipeGenerator build()
        {
            ShapedPatternJson patternJson = new ShapedPatternJson();
            patternJson.setPartA(this.patterns.get(0));

            if(this.patterns.size() > 1)
            {
                patternJson.setPartB(this.patterns.get(1));
            }

            if(this.patterns.size() > 2)
            {
                patternJson.setPartC(this.patterns.get(2));
            }
            else
            {
                patternJson.setPartC(null);
            }

            this.recipeGenerator.getShapedRecipeJsons().add(new ShapedRecipeJson(this.group, patternJson, this.keys, new RecipeResultJson(this.output.getCount(), this.output.getItem().getRegistryName().toString())));
            return this.recipeGenerator;
        }
    }

    public class ShapelessRecipeBuilder
    {
        private final RecipeGenerator recipeGenerator;
        private final List<RecipeIngredientKeyJson> keys;
        private final ItemStack output;
        private String group;

        private ShapelessRecipeBuilder(RecipeGenerator recipeGenerator, ItemStack output)
        {
            this.recipeGenerator = recipeGenerator;
            this.keys = new ArrayList<>();
            this.output = output;
            this.group = null;
        }

        public ShapelessRecipeBuilder key(Object input)
        {
            if(input instanceof Item)
            {
                return this.key((Item)input);
            }
            else if(input instanceof Block)
            {
                return this.key((Block) input);
            }
            else if(input instanceof Tag<?>)
            {
                return this.key((Tag<?>) input);
            }

            return this;
        }

        public ShapelessRecipeBuilder key(Item item)
        {
            this.keys.add(new RecipeIngredientKeyJson(new RecipeIngredientJson(item.getRegistryName().toString(), false)));
            return this;
        }

        public ShapelessRecipeBuilder key(Block block)
        {
            this.keys.add(new RecipeIngredientKeyJson(new RecipeIngredientJson(block.getRegistryName().toString(), false)));
            return this;
        }

        public ShapelessRecipeBuilder key(Tag<?> tag)
        {
            this.keys.add(new RecipeIngredientKeyJson(new RecipeIngredientJson(tag.getId().toString(), true)));
            return this;
        }

        public ShapelessRecipeBuilder group(String group)
        {
            this.group = group;
            return this;
        }

        public RecipeGenerator build()
        {
            this.recipeGenerator.getShapelessRecipeJsons().add(new ShaplessRecipeJson(this.group, this.keys, new RecipeResultJson(this.output.getCount(), this.output.getItem().getRegistryName().toString())));
            return this.recipeGenerator;
        }
    }
}
