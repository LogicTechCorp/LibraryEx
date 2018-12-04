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
