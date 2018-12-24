package logictechcorp.libraryex.block;

import logictechcorp.libraryex.block.builder.BlockBuilder;
import logictechcorp.libraryex.block.properties.UnlistedPropertyDynamic;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class BlockDynamic extends BlockMod
{
    private final TexturePlacement texturePlacement;
    public static final UnlistedPropertyDynamic DYNAMIC = new UnlistedPropertyDynamic();

    public BlockDynamic(ResourceLocation registryName, TexturePlacement texturePlacement, BlockBuilder builder)
    {
        super(registryName, builder);
        this.texturePlacement = texturePlacement;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{DYNAMIC});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState maskedState = this.getDynamicState(state, world, pos);
            extendedState = extendedState.withProperty(DYNAMIC, maskedState);
            return extendedState;
        }

        return state;
    }

    public abstract IBlockState getDynamicState(IBlockState state, IBlockAccess world, BlockPos pos);

    public TexturePlacement getTexturePlacement()
    {
        return this.texturePlacement;
    }

    public ModelResourceLocation getModelLocation()
    {
        return new ModelResourceLocation(this.getRegistryName().toString(), "normal");
    }

    public enum TexturePlacement
    {
        OVER,
        UNDER
    }
}
