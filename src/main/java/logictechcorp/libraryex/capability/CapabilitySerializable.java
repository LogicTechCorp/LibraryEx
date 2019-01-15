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

package logictechcorp.libraryex.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilitySerializable<C> implements ICapabilitySerializable<NBTBase>
{
    private final Capability<C> capability;
    private final C instance;

    public CapabilitySerializable(Capability<C> capability)
    {
        this.capability = capability;
        this.instance = capability.getDefaultInstance();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return this.capability == capability;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return this.capability == capability ? this.capability.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return this.capability.writeNBT(this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase base)
    {
        this.capability.readNBT(this.instance, null, base);
    }
}
