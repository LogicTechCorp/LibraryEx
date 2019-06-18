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

import logictechcorp.libraryex.api.IModData;
import logictechcorp.libraryex.api.world.biome.data.IBiomeData;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;

public abstract class BiomeMod<T extends IBiomeData> extends Biome
{
    public BiomeMod(IModData data, Biome.Builder builder, String name)
    {
        super(builder);
        this.setRegistryName(data.getModId() + ":" + name);
        this.getSpawns(EntityClassification.MONSTER).clear();
        this.getSpawns(EntityClassification.CREATURE).clear();
        this.getSpawns(EntityClassification.WATER_CREATURE).clear();
        this.getSpawns(EntityClassification.AMBIENT).clear();
    }

    public abstract T getBiomeData();
}
