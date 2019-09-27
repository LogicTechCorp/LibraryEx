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

package logictechcorp.libraryex.item.crafting;

import com.google.gson.JsonObject;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RepairRecipe implements ICraftingRecipe
{
    private ResourceLocation recipeId;
    private ItemStack brokenStack;
    private Ingredient repairIngredient;
    private int repairAmount;

    public RepairRecipe(ResourceLocation recipeId, ItemStack brokenStack, Ingredient repairIngredient, int repairAmount)
    {
        this.recipeId = recipeId;
        this.brokenStack = brokenStack;
        this.repairIngredient = repairIngredient;
        this.repairAmount = repairAmount;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world)
    {
        ItemStack brokenStack = ItemStack.EMPTY;
        ItemStack repairStack = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if(stack.getItem() == this.brokenStack.getItem())
            {
                if(!brokenStack.isEmpty())
                {
                    return false;
                }

                brokenStack = stack;
            }
            else if(this.repairIngredient.test(stack))
            {
                if(!repairStack.isEmpty())
                {
                    return false;
                }

                repairStack = stack;
            }
        }

        return !brokenStack.isEmpty() && brokenStack.getDamage() > 0 && !repairStack.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory)
    {
        ItemStack brokenStack = ItemStack.EMPTY;
        ItemStack repairStack = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if(stack.getItem() == this.brokenStack.getItem())
            {
                if(!brokenStack.isEmpty())
                {
                    return ItemStack.EMPTY;
                }

                brokenStack = stack;
            }
            else if(this.repairIngredient.test(stack))
            {
                if(!repairStack.isEmpty())
                {
                    return ItemStack.EMPTY;
                }

                repairStack = stack;
            }
        }

        ItemStack output = brokenStack.copy();
        output.attemptDamageItem(-this.repairAmount, RandomHelper.getRandom(), null);
        return output;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return LibraryExRecipeSerializers.CRAFTING_REPAIR;
    }

    public ItemStack getBrokenStack()
    {
        return this.brokenStack;
    }

    public Ingredient getRepairIngredient()
    {
        return this.repairIngredient;
    }

    public int getRepairAmount()
    {
        return this.repairAmount;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RepairRecipe>
    {
        @Override
        public RepairRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            JsonObject brokenObject = JSONUtils.getJsonObject(json, "broken_item");
            JsonObject repairObject = JSONUtils.getJsonObject(json, "repair_item");

            ItemStack brokenStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getString(brokenObject, "item"))));
            Ingredient ingredient = Ingredient.EMPTY;
            int repairAmount = JSONUtils.getInt(json, "repair_amount");

            if(repairObject.has("item"))
            {
                ingredient = Ingredient.fromStacks(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getString(repairObject, "item")))));
            }
            if(repairObject.has("tag"))
            {
                ingredient = Ingredient.fromTag(new ItemTags.Wrapper(new ResourceLocation(JSONUtils.getString(repairObject, "tag"))));
            }

            return new RepairRecipe(recipeId, brokenStack, ingredient, repairAmount);
        }

        @Override
        public RepairRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            ItemStack brokenStack = buffer.readItemStack();
            ItemStack repairStack = buffer.readItemStack();
            int repairAmount = buffer.readVarInt();
            return new RepairRecipe(recipeId, brokenStack, Ingredient.fromStacks(repairStack), repairAmount);
        }

        @Override
        public void write(PacketBuffer buffer, RepairRecipe recipe)
        {
            buffer.writeItemStack(recipe.getBrokenStack());
            buffer.writeItemStack(recipe.getRepairIngredient().getMatchingStacks()[0]);
            buffer.writeVarInt(recipe.getRepairAmount());
        }
    }
}
