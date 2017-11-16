package lex.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public enum BiomeType
{
    END(Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState()),
    OVERWORLD(Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState()),
    NETHER(Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState());

    private IBlockState associatedLandBlock;
    private IBlockState associatedLiquidBlock;

    BiomeType(IBlockState associatedBlockIn, IBlockState associatedLiquidBlockIn)
    {
        associatedLandBlock = associatedBlockIn;
        associatedLiquidBlock = associatedLiquidBlockIn;
    }

    public IBlockState getAssociatedLandBlock()
    {
        return associatedLandBlock;
    }

    public IBlockState getAssociatedLiquidBlock()
    {
        return associatedLiquidBlock;
    }
}
