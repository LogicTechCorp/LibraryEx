package logictechcorp.libraryex.client.model.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelContainer
{
    @SideOnly(Side.CLIENT)
    void registerModel();
}
