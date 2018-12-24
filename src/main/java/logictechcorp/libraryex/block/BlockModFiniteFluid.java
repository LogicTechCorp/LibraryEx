package logictechcorp.libraryex.block;

import logictechcorp.libraryex.block.builder.BlockBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class BlockModFiniteFluid extends BlockFluidFinite
{
    public BlockModFiniteFluid(ResourceLocation registryName, Fluid fluid, BlockBuilder builder)
    {
        super(fluid, builder.getMaterial());
        this.setRegistryName(registryName);
        this.setSoundType(builder.getSoundType());
        this.setCreativeTab(builder.getCreativeTab());
        this.setLightLevel(builder.getLightLevel());
        this.setHarvestLevel(builder.getHarvestTool(), builder.getHarvestLevel());
        this.setHardness(builder.getHardness());
        this.setResistance(builder.getResistance());
        this.setTickRandomly(builder.needsRandomTick());
        this.setTranslationKey(registryName.toString());
    }
}
