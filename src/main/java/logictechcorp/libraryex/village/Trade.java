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

package logictechcorp.libraryex.village;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import logictechcorp.libraryex.util.ConfigHelper;
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

    public MerchantRecipe randomize(Random rand)
    {
        ItemStack outputStack = this.output.getItemStack().copy();
        ItemStack inputOneStack = this.inputOne.getItemStack().copy();
        ItemStack inputTwoStack = this.inputTwo.getItemStack().copy();

        outputStack.setCount(RandomHelper.getNumberInRange(this.output.getMinCount(), this.output.getMaxCount(), rand));
        inputOneStack.setCount(RandomHelper.getNumberInRange(this.inputOne.getMinCount(), this.inputOne.getMaxCount(), rand));

        if(this.hasSecondItemToBuy())
        {
            inputTwoStack.setCount(RandomHelper.getNumberInRange(this.inputTwo.getMinCount(), this.inputTwo.maxCount, rand));
        }

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

    public String getIdentifier()
    {
        return identifier;
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
        Config tradeConfig = TomlFormat.newConcurrentConfig();
        ConfigHelper.setItemStackSimple(tradeConfig, "output", this.output.getItemStack());

        if(!tradeConfig.contains("output.minCount"))
        {
            tradeConfig.add("output.minCount", this.output.getMinCount());
        }
        if(!tradeConfig.contains("output.maxCount"))
        {
            tradeConfig.add("output.maxCount", this.output.getMaxCount());
        }

        ConfigHelper.setItemStackSimple(tradeConfig, "inputOne", this.inputOne.getItemStack());

        if(!tradeConfig.contains("inputOne.minCount"))
        {
            tradeConfig.add("inputOne.minCount", this.output.getMinCount());
        }
        if(!tradeConfig.contains("inputOne.maxCount"))
        {
            tradeConfig.add("inputOne.maxCount", this.output.getMaxCount());
        }

        if(!this.inputTwo.getItemStack().isEmpty())
        {
            ConfigHelper.setItemStackSimple(tradeConfig, "inputTwo", this.inputTwo.getItemStack());

            if(!tradeConfig.contains("inputTwo.minCount"))
            {
                tradeConfig.add("inputTwo.minCount", this.output.getMinCount());
            }
            if(!tradeConfig.contains("inputTwo.maxCount"))
            {
                tradeConfig.add("inputTwo.maxCount", this.output.getMaxCount());
            }
        }

        tradeConfig.add("minTradeCount", this.minTradeCount);
        tradeConfig.add("maxTradeCount", this.maxTradeCount);
        tradeConfig.add("tradeLevel", this.tradeLevel);
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
            return object == this || ((Trade) object).getIdentifier().equals(this.getIdentifier());
        }

        return false;
    }
}
