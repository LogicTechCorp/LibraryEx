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

import com.electronwill.nightconfig.core.Config;
import lex.util.ConfigHelper;
import lex.util.RandomHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class Trade extends MerchantRecipe
{
    protected int tradeLevel;
    private Config config;

    public Trade(Config config)
    {
        super(ConfigHelper.getOrSetItemStack(config, "inputOne", null), ConfigHelper.getOrSetItemStack(config, "inputTwo", null), ConfigHelper.getOrSetItemStack(config, "output", null), 0, ConfigHelper.getOrSet(config, "maxTradesAvailable", 7));
        this.tradeLevel = ConfigHelper.getOrSet(config, "tradeLevel", 1);
        this.config = config;
    }

    public MerchantRecipe randomize()
    {
        ItemStack outputStack = this.getItemToSell().copy();
        ItemStack inputOneStack = this.getItemToBuy().copy();
        ItemStack inputTwoStack = this.getSecondItemToBuy().copy();
        int tradesAvailable = RandomHelper.getNumberInRange(ConfigHelper.getOrSet(this.config, "minTradesAvailable", 1), ConfigHelper.getOrSet(this.config, "maxTradesAvailable", 7), RandomHelper.getRand());

        Config outputConfig = ConfigHelper.getOrSet(this.config, "output", null);
        Config inputOneConfig = ConfigHelper.getOrSet(this.config, "inputOne", null);
        Config inputTwoConfig = ConfigHelper.getOrSet(this.config, "inputTwo", null);

        outputStack.setCount(RandomHelper.getNumberInRange(outputConfig.getOrElse("minStackSize", 1), outputConfig.getOrElse("maxStackSize", 8), RandomHelper.getRand()));
        inputOneStack.setCount(RandomHelper.getNumberInRange(inputOneConfig.getOrElse("minStackSize", 1), inputOneConfig.getOrElse("maxStackSize", 8), RandomHelper.getRand()));

        if(inputTwoConfig != null)
        {
            inputTwoStack.setCount(RandomHelper.getNumberInRange(inputTwoConfig.getOrElse("minStackSize", 1), inputTwoConfig.getOrElse("maxStackSize", 8), RandomHelper.getRand()));
        }

        return new MerchantRecipe(inputOneStack, inputTwoStack, outputStack, 0, tradesAvailable);
    }

    public int getTradeLevel()
    {
        return this.tradeLevel;
    }
}
