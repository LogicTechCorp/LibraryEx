/*
 * LibraryEx
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

package logictechcorp.libraryex.village;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.util.ConfigHelper;
import logictechcorp.libraryex.util.RandomHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;

public class Trade extends MerchantRecipe
{
    protected Tuple<Integer, Integer> inputOneMinMax;
    protected Tuple<Integer, Integer> inputTwoMinMax;
    protected Tuple<Integer, Integer> outputMinMax;
    protected Tuple<Integer, Integer> tradesAvailableMinMax;
    protected int tradeLevel;

    public Trade(Config config)
    {
        super(ConfigHelper.getItemStack(config, "inputOne"), ConfigHelper.getItemStack(config, "inputTwo"), ConfigHelper.getItemStack(config, "output"), 0, config.getOrElse("maxTradesAvailable", 7));

        this.inputOneMinMax = new Tuple<>(this.getItemToBuy().getCount(), this.getItemToBuy().getMaxStackSize());
        this.inputTwoMinMax = new Tuple<>(this.getSecondItemToBuy().getCount(), this.getSecondItemToBuy().getMaxStackSize());
        this.outputMinMax = new Tuple<>(this.getItemToSell().getCount(), this.getItemToSell().getMaxStackSize());
        this.tradesAvailableMinMax = new Tuple<>(config.getOrElse("minTradesAvailable", 1), config.getOrElse("maxTradesAvailable", 1));
        this.tradeLevel = config.getOrElse("tradeLevel", 1);
    }

    public Trade(ItemStack inputOne, ItemStack inputTwo, ItemStack output, Tuple<Integer, Integer> tradesAvailableMinMax, int tradeLevel)
    {
        super(inputOne, inputTwo, output, 0, tradesAvailableMinMax.getSecond());

        this.inputOneMinMax = new Tuple<>(this.getItemToBuy().getCount(), this.getItemToBuy().getMaxStackSize());
        this.inputTwoMinMax = new Tuple<>(this.getSecondItemToBuy().getCount(), this.getSecondItemToBuy().getMaxStackSize());
        this.outputMinMax = new Tuple<>(this.getItemToSell().getCount(), this.getItemToSell().getMaxStackSize());
        this.tradesAvailableMinMax = tradesAvailableMinMax;
        this.tradeLevel = tradeLevel;
    }

    public MerchantRecipe randomize()
    {
        ItemStack inputOneStack = this.getItemToBuy().copy();
        ItemStack inputTwoStack = this.getSecondItemToBuy().copy();
        ItemStack outputStack = this.getItemToSell().copy();

        inputOneStack.setCount(RandomHelper.getNumberInRange(this.inputOneMinMax.getFirst(), this.inputOneMinMax.getSecond(), RandomHelper.getRand()));

        if(this.hasSecondItemToBuy())
        {
            inputTwoStack.setCount(RandomHelper.getNumberInRange(this.inputTwoMinMax.getFirst(), this.inputTwoMinMax.getSecond(), RandomHelper.getRand()));
        }

        outputStack.setCount(RandomHelper.getNumberInRange(this.outputMinMax.getFirst(), this.outputMinMax.getSecond(), RandomHelper.getRand()));
        int tradesAvailable = RandomHelper.getNumberInRange(this.tradesAvailableMinMax.getFirst(), this.tradesAvailableMinMax.getSecond(), RandomHelper.getRand());

        return new MerchantRecipe(inputOneStack, inputTwoStack, outputStack, 0, tradesAvailable);
    }

    public int getTradeLevel()
    {
        return this.tradeLevel;
    }
}
