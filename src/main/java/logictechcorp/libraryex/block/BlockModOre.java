package logictechcorp.libraryex.block;

import logictechcorp.libraryex.block.builder.BlockBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockModOre extends BlockMod
{
    public BlockModOre(ResourceLocation registryName, BlockBuilder builder)
    {
        super(registryName, builder);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random rand)
    {
        if(fortune > 0)
        {
            int i = rand.nextInt(fortune + 2) - 1;

            if(i < 0)
            {
                i = 0;
            }

            return this.quantityDropped(rand) * (i + 1);
        }
        else
        {
            return this.quantityDropped(rand);
        }
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
    {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        return MathHelper.getInt(rand, 2, 5);
    }
}
