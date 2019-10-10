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

package logictechcorp.libraryex.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Supplier;

public class ModSpawnEggItem extends SpawnEggItem
{
    private final Supplier<EntityType<?>> entityTypeSupplier;

    public ModSpawnEggItem(Supplier<EntityType<?>> entityTypeSupplier, int primaryColor, int secondaryColor, Properties properties)
    {
        super(null, primaryColor, secondaryColor, properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    @Override
    public EntityType<?> getType(CompoundNBT compound)
    {
        if(compound != null && compound.contains("EntityTag", 10))
        {
            CompoundNBT entityTag = compound.getCompound("EntityTag");

            if(entityTag.contains("id", 8))
            {
                return EntityType.byKey(entityTag.getString("id")).orElse(this.entityTypeSupplier.get());
            }
        }

        return this.entityTypeSupplier.get();
    }
}
