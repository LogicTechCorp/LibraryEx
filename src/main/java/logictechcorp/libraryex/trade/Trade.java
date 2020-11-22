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

package logictechcorp.libraryex.trade;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import java.util.LinkedHashMap;
import java.util.Random;

public class Trade extends MerchantRecipe
{
    protected TradeStack output;
    protected TradeStack inputOne;
    protected TradeStack inputTwo;
    protected int minTradeCount;
    protected int maxTradeCount;
    protected int tradeLevel;
    private String identifier;

    public Trade(TradeStack output, TradeStack inputOne, TradeStack inputTwo, int minTradeCount, int maxTradeCount, int tradeLevel)
    {
        super(inputOne.getItemStack(), inputTwo.getItemStack(), output.getItemStack(), 0, maxTradeCount);
        this.output = output;
        this.inputOne = inputOne;
        this.inputTwo = inputTwo;
        this.minTradeCount = minTradeCount;
        this.maxTradeCount = maxTradeCount;
        this.tradeLevel = tradeLevel;

        ItemStack outputStack = this.output.getItemStack().copy();
        ItemStack inputOneStack = this.inputOne.getItemStack().copy();
        this.identifier = outputStack.getItem().getRegistryName() + "@" + outputStack.getItemDamage();
        this.identifier += "=" + inputOneStack.getItem().getRegistryName() + "@" + inputOneStack.getItemDamage();

        if(this.hasSecondItemToBuy())
        {
            ItemStack inputTwoStack = this.inputTwo.getItemStack().copy();
            this.identifier += "+" + inputTwoStack.getItem().getRegistryName() + "@" + inputTwoStack.getItemDamage();
        }
    }

    public Trade(Config config)
    {
        this(new TradeStack(config, "output"), new TradeStack(config, "inputOne"), new TradeStack(config, "inputTwo"), config.getOrElse("minTradeCount", 1), config.getOrElse("maxTradeCount", 8), config.getOrElse("tradeLevel", 1));
    }

    public MerchantRecipe randomize(Random random)
    {
        ItemStack outputStack = this.output.getItemStack().copy();
        ItemStack inputOneStack = this.inputOne.getItemStack().copy();
        ItemStack inputTwoStack = this.inputTwo.getItemStack().copy();

        outputStack.setCount(RandomHelper.getNumberInRange(this.output.getMinCount(), this.output.getMaxCount(), random));
        inputOneStack.setCount(RandomHelper.getNumberInRange(this.inputOne.getMinCount(), this.inputOne.getMaxCount(), random));

        if(this.hasSecondItemToBuy())
        {
            inputTwoStack.setCount(RandomHelper.getNumberInRange(this.inputTwo.getMinCount(), this.inputTwo.getMaxCount(), random));
        }

        int tradesAvailable = RandomHelper.getNumberInRange(this.minTradeCount, this.maxTradeCount, random);

        return new MerchantRecipe(inputOneStack, inputTwoStack, outputStack, 0, tradesAvailable);
    }

    public String getIdentifier()
    {
        return this.identifier;
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

    public Config getAsConfig()
    {
        Config tradeConfig = JsonFormat.newConfig(LinkedHashMap::new);
        ConfigHelper.setItemStackComplex(tradeConfig, "output", this.output.getItemStack());
        tradeConfig.set("output.minCount", this.output.getMinCount());
        tradeConfig.set("output.maxCount", this.output.getMaxCount());

        ConfigHelper.setItemStackComplex(tradeConfig, "inputOne", this.inputOne.getItemStack());
        tradeConfig.set("inputOne.minCount", this.inputOne.getMinCount());
        tradeConfig.set("inputOne.maxCount", this.inputOne.getMaxCount());

        if(this.hasSecondItemToBuy())
        {
            ConfigHelper.setItemStackComplex(tradeConfig, "inputTwo", this.inputTwo.getItemStack());
            tradeConfig.set("inputTwo.minCount", this.inputTwo.getMinCount());
            tradeConfig.set("inputTwo.maxCount", this.inputTwo.getMaxCount());
        }

        tradeConfig.set("minTradeCount", this.minTradeCount);
        tradeConfig.set("maxTradeCount", this.maxTradeCount);
        tradeConfig.set("tradeLevel", this.tradeLevel);
        return tradeConfig;
    }

    @Override
    public boolean hasSecondItemToBuy()
    {
        return !this.inputTwo.getItemStack().isEmpty();
    }

    @Override
    public boolean equals(Object object)
    {
        if(object instanceof Trade)
        {
            return object == this || ((Trade) object).getIdentifier().equals(this.identifier);
        }

        return false;
    }
}
