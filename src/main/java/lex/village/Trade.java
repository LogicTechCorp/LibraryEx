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

package lex.village;

import lex.config.IConfig;
import lex.util.NumberHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class Trade extends MerchantRecipe implements ITrade
{
    private int tradeLevel;
    private IConfig config;

    public Trade(IConfig configIn)
    {
        super(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, 0, 0);
        parse(configIn);
        config = configIn;
    }

    @Override
    public void parse(IConfig config)
    {
        itemToBuy = config.getItem("inputOne");
        secondItemToBuy = config.getItem("inputTwo");
        itemToSell = config.getItem("output");
        maxTradeUses = NumberHelper.getNumberInRange(config.getInt("minTradesAvailable", 1), config.getInt("maxTradesAvailable", 7), NumberHelper.getRand());
        rewardsExp = config.getBoolean("rewardExp", true);
        tradeLevel = config.getInt("tradeLevel", 1);
    }

    @Override
    public int getTradeLevel()
    {
        return tradeLevel;
    }

    @Override
    public IConfig getConfig()
    {
        return config;
    }
}
