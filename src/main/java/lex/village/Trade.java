/*
 * LibEx
 * Copyright (c) 2017-2018 by MineEx
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

import lex.config.Config;
import lex.util.NumberHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class Trade extends MerchantRecipe
{
    protected int tradeLevel;
    private Config config;

    public Trade(Config config)
    {
        super(config.getItem("inputOne"), config.getItem("inputTwo"), config.getItem("output"), 0, config.getInt("maxTradesAvailable", 7));
        tradeLevel = config.getInt("tradeLevel", 1);
        this.config = config;
    }

    public MerchantRecipe randomize()
    {
        ItemStack outputStack = getItemToSell().copy();
        ItemStack inputOneStack = getItemToBuy().copy();
        ItemStack inputTwoStack = getSecondItemToBuy().copy();
        int tradesAvailable = NumberHelper.getNumberInRange(config.getInt("minTradesAvailable", 1), config.getInt("maxTradesAvailable", 7), NumberHelper.getRand());

        Config outputConfig = config.getDataBranch("output");
        Config inputOneConfig = config.getDataBranch("inputOne");
        Config inputTwoConfig = config.getDataBranch("inputTwo");

        outputStack.setCount(NumberHelper.getNumberInRange(outputConfig.getInt("minStackSize", 1), outputConfig.getInt("maxStackSize", 8), NumberHelper.getRand()));
        inputOneStack.setCount(NumberHelper.getNumberInRange(inputOneConfig.getInt("minStackSize", 1), inputOneConfig.getInt("maxStackSize", 8), NumberHelper.getRand()));

        if(inputTwoConfig != null)
        {
            inputTwoStack.setCount(NumberHelper.getNumberInRange(inputTwoConfig.getInt("minStackSize", 1), inputTwoConfig.getInt("maxStackSize", 8), NumberHelper.getRand()));
        }

        return new MerchantRecipe(inputOneStack, inputTwoStack, outputStack, 0, tradesAvailable);
    }

    public int getTradeLevel()
    {
        return tradeLevel;
    }
}
