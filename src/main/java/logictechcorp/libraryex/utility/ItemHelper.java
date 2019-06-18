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

package logictechcorp.libraryex.utility;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ItemHelper
{
    private static final List<Potion> POTION_TYPES = getPotions();

    public static ItemStack getRandomlyEnchantedBook(int amountOfEnchantments)
    {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);

        for(int i = 0; i < amountOfEnchantments; i++)
        {
            EnchantmentHelper.addRandomEnchantment(RandomHelper.getRandom(), stack, 8 + RandomHelper.getRandom().nextInt(25), true);
        }

        return stack;
    }

    public static ItemStack getRandomPotion()
    {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.addPotionToItemStack(stack, POTION_TYPES.get(RandomHelper.getRandom().nextInt(POTION_TYPES.size())));
        return stack;
    }

    private static List<Potion> getPotions()
    {
        List<Potion> potionTypes = new ArrayList<>((ForgeRegistries.POTION_TYPES.getValues()));
        potionTypes.remove(Potions.EMPTY);
        potionTypes.remove(Potions.WATER);
        potionTypes.remove(Potions.MUNDANE);
        potionTypes.remove(Potions.THICK);
        potionTypes.remove(Potions.AWKWARD);
        return potionTypes;
    }
}
