/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.api.village;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public abstract class Trade extends MerchantRecipe implements ITrade
{
    protected int tradeLevel;

    public Trade(ItemStack inputOne, ItemStack inputTwo, ItemStack output, int toolUses, int maxTradesAvailable, int tradeLevelIn)
    {
        super(inputOne, inputTwo, output, toolUses, maxTradesAvailable);
        tradeLevel = tradeLevelIn;
    }

    @Override
    public int getTradeLevel()
    {
        return tradeLevel;
    }
}
