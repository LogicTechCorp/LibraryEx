package lex.biome;

import lex.config.IConfigEx;
import net.minecraft.block.state.IBlockState;

public interface IBiomeEx
{
    void configure(IConfigEx config);

    IBlockState getTopBlock();

    IBlockState getFillerBlock();

    IBlockState getCaveCeilingBlock();

    IBlockState getCaveWallBlock();

    IBlockState getCaveFloorBlock();

    IBlockState getOceanBlock();

    int getWeight();
}
