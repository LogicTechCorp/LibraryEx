package logictechcorp.libraryex.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.*;

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

    public C getCareer(ResourceLocation id)
    {
        for(C career : this.careers)
        {
            if(career.getName().equals(id))
            {
                return career;
            }
        }

        return null;
    }

    public C getRandomCareer(Random rand)
    {
        return this.careers.get(rand.nextInt(this.careers.size()));
    }

    public abstract class Career
    {
        private ResourceLocation name;
        private P profession;
        private ResourceLocation lootTable;
        private ResourceLocation texture;
        private ResourceLocation alternateTexture;
        private int id;
        private final Map<Integer, List<Trade>> trades;

        protected Career(ResourceLocation name, P profession, ResourceLocation lootTable, ResourceLocation texture, ResourceLocation alternateTexture)
        {
            this.name = name;
            this.profession = profession;
            this.lootTable = lootTable;
            this.texture = texture;
            this.alternateTexture = alternateTexture;
            this.trades = new HashMap<>();
        }

        public void addTrade(Trade trade)
        {
            this.trades.computeIfAbsent(trade.getTradeLevel(), k -> new ArrayList<>()).add(trade);
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

        public Map<Integer, List<Trade>> getTrades()
        {
            return ImmutableMap.copyOf(this.trades);
        }

        public List<Trade> getTrades(int level)
        {
            return ImmutableList.copyOf(this.trades.computeIfAbsent(level, k -> new ArrayList<>()));
        }

        void setId(int id)
        {
            this.id = id;
        }
    }
}
