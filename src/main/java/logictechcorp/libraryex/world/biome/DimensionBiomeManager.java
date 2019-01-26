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

package logictechcorp.libraryex.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DimensionBiomeManager
{
    private final List<BiomeInfo> biomeInfo = new ArrayList<>();
    private final Map<BiomeInfo, BiomeManager.BiomeEntry> biomeEntries = new ConcurrentHashMap<>();

    public abstract void readBiomeInfoFromConfigs();

    public abstract void writeBiomeInfoToConfigs();

    public void addBiome(BiomeInfo biomeInfo)
    {
        if(biomeInfo == null)
        {
            return;
        }

        if(!this.biomeInfo.contains(biomeInfo))
        {
            this.biomeInfo.add(biomeInfo);
            this.biomeEntries.put(biomeInfo, new BiomeManager.BiomeEntry(biomeInfo.getBiome(), biomeInfo.getWeight()));
        }
    }

    public void removeBiome(BiomeInfo biomeInfo)
    {
        this.biomeEntries.remove(biomeInfo);
    }

    public List<BiomeInfo> getAllBiomeInfo()
    {
        return ImmutableList.copyOf(this.biomeInfo);
    }

    public List<BiomeManager.BiomeEntry> getBiomeEntries()
    {
        return ImmutableList.copyOf(this.biomeEntries.values());
    }

    public BiomeInfo getBiomeInfo(Biome biome)
    {
        for(BiomeInfo biomeInfo : this.biomeInfo)
        {
            if(biome == biomeInfo.getBiome())
            {
                return biomeInfo;
            }
        }

        return null;
    }

    public abstract File getBiomeInfoSaveFile(BiomeInfo biomeInfo);
}
