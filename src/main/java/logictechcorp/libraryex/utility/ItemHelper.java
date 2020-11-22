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
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemHelper
{
    private static final List<PotionType> POTION_TYPES = getPotions();

    public static ItemStack getRandomlyEnchantedBook(int level)
    {
        return EnchantmentHelper.addRandomEnchantment(RandomHelper.getRandom(), new ItemStack(Items.BOOK), RandomHelper.getRandom().nextInt(10) + 5 + level, true);
    }

    public static ItemStack getRandomPotion()
    {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), POTION_TYPES.get(RandomHelper.getRandom().nextInt(POTION_TYPES.size())));
    }

    public static boolean isOreDict(String id, Item item)
    {
        for(ItemStack stack : OreDictionary.getOres(id))
        {
            if(stack.getItem() == item)
            {
                return true;
            }
        }

        return false;
    }

    private static List<PotionType> getPotions()
    {
        List<PotionType> potionTypes = new ArrayList<>((ForgeRegistries.POTION_TYPES.getValuesCollection()));
        potionTypes.remove(PotionTypes.EMPTY);
        potionTypes.remove(PotionTypes.WATER);
        potionTypes.remove(PotionTypes.MUNDANE);
        potionTypes.remove(PotionTypes.THICK);
        potionTypes.remove(PotionTypes.AWKWARD);
        return potionTypes;
    }
}
