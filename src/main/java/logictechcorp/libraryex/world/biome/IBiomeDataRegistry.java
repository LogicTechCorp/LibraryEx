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

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.util.Map;

public interface IBiomeDataRegistry
{
    /**
     * Called to register biome data.
     *
     * @param biomeData The biome data that is to be registered.
     */
    void registerBiomeData(IBiomeData biomeData);

    /**
     * Called to unregister biome data.
     *
     * @param biome The biome that the data is registered to.
     */
    void unregisterBiomeData(Biome biome);

    /**
     * Called to check if a biome has associated data.
     *
     * @param biome The biome to check against.
     * @return Whether the biome has associated data.
     */
    boolean hasBiomeData(Biome biome);

    /**
     * Called to get a biome data associated with a biome.
     *
     * @param biome The biome to get the biome data for.
     * @return The biome data associated with the biome.
     */
    IBiomeData getBiomeData(Biome biome);

    /**
     * Called to get a map containing biome data registry names and their biome data instance.
     *
     * @return A map containing biome data registry names and their biome data instance.
     */
    Map<ResourceLocation, IBiomeData> getBiomeData();

    /**
     * Called to get a map containing biome data and their biome entry instance.
     *
     * @return A map containing biome data and their biome entry instance.
     */
    Map<IBiomeData, BiomeManager.BiomeEntry> getBiomeEntries();

}
