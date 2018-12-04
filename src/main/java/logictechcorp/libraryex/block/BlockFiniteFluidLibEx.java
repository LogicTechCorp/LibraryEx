package logictechcorp.libraryex.block;

import com.google.common.base.CaseFormat;
import logictechcorp.libraryex.IModData;
import logictechcorp.libraryex.client.model.item.IModelContainer;
import logictechcorp.libraryex.client.model.item.ItemModelHandler;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFiniteFluidLibEx extends BlockFluidFinite implements IModelContainer
{
    private IModData data;

    public BlockFiniteFluidLibEx(IModData data, String name, Fluid fluid, Material material)
    {
        super(fluid, material);
        this.data = data;
        this.setRegistryName(data.getModId() + ":" + name);
        this.setTranslationKey(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.getRegistryName().toString()));
        this.setCreativeTab(data.getCreativeTab());
        data.getModelContainers().add(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModel()
    {
        ItemModelHandler.registerModel(this.data, this);
    }
}
