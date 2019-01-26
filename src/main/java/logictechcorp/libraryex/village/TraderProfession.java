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

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class TraderProfession<P extends TraderProfession<P, C>, C extends TraderProfession.Career> extends IForgeRegistryEntry.Impl<P>
{
    private ResourceLocation name;
    private List<C> careers = new ArrayList<>();

    public TraderProfession(ResourceLocation name)
    {
        this.name = name;
        this.setRegistryName(this.name);
    }

    public void registerCareer(C career)
    {
        if(career.getProfession() == this)
        {
            if(!this.careers.contains(career))
            {
                career.setId(this.careers.size());
                this.careers.add(career);
            }
            else
            {
                throw new IllegalArgumentException(String.format("Attempted to register the %s career, but it is already registered.", career.getName()));
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("Attempted to register the %s career with the %s profession, instead of the %s profession.", career.getName(), this, career.getProfession()));
        }
    }

    public ResourceLocation getName()
    {
        return this.name;
    }

    public List<C> getCareers()
    {
        return this.careers;
    }

    public C getCareer(int id)
    {
        if(id < 0 || id > this.careers.size())
        {
            return this.careers.get(0);
        }

        return this.careers.get(id);
    }

    public C getCareer(ResourceLocation careerName)
    {
        return this.careers.stream().filter(career -> careerName.equals(career.getName())).findFirst().orElse(null);
    }

    public C getRandomCareer(Random random)
    {
        return this.careers.get(random.nextInt(this.careers.size()));
    }

    public static abstract class Career<P extends TraderProfession<P, C>, C extends TraderProfession.Career<P, C>>
    {
        private ResourceLocation name;
        private P profession;
        private ResourceLocation lootTable;
        private ResourceLocation texture;
        private ResourceLocation alternateTexture;
        private int id;
        private final List<ConfigurableTrade> trades;

        protected Career(ResourceLocation name, P profession, ResourceLocation lootTable, ResourceLocation texture, ResourceLocation alternateTexture)
        {
            this.name = name;
            this.profession = profession;
            this.lootTable = lootTable;
            this.texture = texture;
            this.alternateTexture = alternateTexture;
            this.trades = new ArrayList<>();
        }

        public void addTrade(ConfigurableTrade trade)
        {
            if(this.trades.stream().noneMatch(trade::equals))
            {
                this.trades.add(trade);
            }
        }

        public void removeTrade(ConfigurableTrade trade)
        {
            this.trades.remove(trade);
        }

        public ResourceLocation getName()
        {
            return this.name;
        }

        public P getProfession()
        {
            return this.profession;
        }

        public ResourceLocation getLootTable()
        {
            return this.lootTable;
        }

        public ResourceLocation getTexture()
        {
            return this.texture;
        }

        public ResourceLocation getAlternateTexture()
        {
            return this.alternateTexture;
        }

        public int getId()
        {
            return this.id;
        }

        public List<ConfigurableTrade> getTrades()
        {
            return ImmutableList.copyOf(this.trades);
        }

        public List<ConfigurableTrade> getTradesForLevel(int tradeLevel)
        {
            return this.trades.stream().filter(k -> tradeLevel == k.getTradeLevel()).collect(Collectors.toList());
        }

        void setId(int id)
        {
            this.id = id;
        }
    }
}
