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
import logictechcorp.libraryex.util.RandomHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import java.util.Random;

public class Trade extends MerchantRecipe
{
    protected TradeStack output;
    protected TradeStack inputOne;
    protected TradeStack inputTwo;
    protected int minTradeCount;
    protected int maxTradeCount;
    protected int tradeLevel;

    public Trade(TradeStack output, TradeStack inputOne, TradeStack inputTwo, int minTradeCount, int maxTradeCount, int tradeLevel)
    {
        super(inputOne.getItemStack(), inputTwo.getItemStack(), output.getItemStack(), 0, maxTradeCount);
        this.output = output;
        this.inputOne = inputOne;
        this.inputTwo = inputTwo;
        this.minTradeCount = minTradeCount;
        this.maxTradeCount = maxTradeCount;
        this.tradeLevel = tradeLevel;
    }

    public Trade(Config config)
    {
        this(new TradeStack(config, "inputOne"), new TradeStack(config, "inputTwo"), new TradeStack(config, "output"), config.getOrElse("minTradeCount", 1), config.getOrElse("maxTradeCount", 8), config.getOrElse("tradeLevel", 1));
    }

    public MerchantRecipe randomize(Random rand)
    {
        ItemStack inputOneStack = this.getItemToBuy().copy();
        ItemStack inputTwoStack = this.getSecondItemToBuy().copy();
        ItemStack outputStack = this.getItemToSell().copy();

        inputOneStack.setCount(RandomHelper.getNumberInRange(this.inputOne.getMinCount(), this.inputOne.getMaxCount(), rand));

        if(this.hasSecondItemToBuy())
        {
            inputTwoStack.setCount(RandomHelper.getNumberInRange(this.inputTwo.getMinCount(), this.inputTwo.maxCount, rand));
        }

        outputStack.setCount(RandomHelper.getNumberInRange(this.output.getMinCount(), this.output.getMaxCount(), rand));
        int tradesAvailable = RandomHelper.getNumberInRange(this.minTradeCount, this.maxTradeCount, rand);

        return new MerchantRecipe(inputOneStack, inputTwoStack, outputStack, 0, tradesAvailable);
    }

    public TradeStack getOutput()
    {
        return this.output;
    }

    public TradeStack getInputOne()
    {
        return this.inputOne;
    }

    public TradeStack getInputTwo()
    {
        return this.inputTwo;
    }

    public int getMinTradeCount()
    {
        return this.minTradeCount;
    }

    public int getMaxTradeCount()
    {
        return this.maxTradeCount;
    }

    public int getTradeLevel()
    {
        return this.tradeLevel;
    }
}
