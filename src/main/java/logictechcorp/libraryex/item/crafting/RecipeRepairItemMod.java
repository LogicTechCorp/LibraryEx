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

import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeRepairItemMod extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private final ItemStack brokenStack;
    private final Ingredient repairIngredient;
    private final int repairAmount;

    public RecipeRepairItemMod(ItemStack brokenStack, Ingredient repairIngredient, int repairAmount)
    {
        this.brokenStack = brokenStack;
        this.repairIngredient = repairIngredient;
        this.repairAmount = repairAmount;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world)
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
            else if(this.repairIngredient.apply(stack))
            {
                if(!repairStack.isEmpty())
                {
                    return false;
                }

                repairStack = stack;
            }
        }

        return !brokenStack.isEmpty() && brokenStack.getItemDamage() > 0 && !repairStack.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
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
            else if(this.repairIngredient.apply(stack))
            {
                if(!repairStack.isEmpty())
                {
                    return ItemStack.EMPTY;
                }

                repairStack = stack;
            }
        }

        ItemStack output = brokenStack.copy();
        output.attemptDamageItem(this.repairAmount, RandomHelper.getRandom(), null);
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
}
