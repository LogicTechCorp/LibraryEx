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

package logictechcorp.libraryex.block;

import logictechcorp.libraryex.block.builder.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockRising extends BlockMod
{
    public static boolean riseInstantly;

    public BlockRising(ResourceLocation registryName, BlockBuilder builder)
    {
        super(registryName, builder);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        if(rand.nextInt(16) == 0)
        {
            BlockPos newPos = pos.up();

            if(canRiseThrough(world.getBlockState(newPos)))
            {
                double posX = (double) ((float) pos.getX() + rand.nextFloat());
                double posY = (double) pos.getY() - 0.05D;
                double posZ = (double) ((float) pos.getZ() + rand.nextFloat());
                world.spawnParticle(EnumParticleTypes.FALLING_DUST, posX, posY, posZ, 0.0D, 0.5D, 0.0D, Block.getStateId(state));
            }
        }
    }

    @Override
    public int tickRate(World worldIn)
    {
        return 2;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if(!worldIn.isRemote)
        {
            this.rise(worldIn, pos);
        }
    }

    private void rise(World world, BlockPos pos)
    {
        if((world.isAirBlock(pos.down()) || canRiseThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0)
        {
            if(!riseInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            {
                if(!world.isRemote)
                {
                    //EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, world.getBlockState(pos));
                    //onStartRising();
                    //world.spawnEntity(entityfallingblock);
                }
            }
            else
            {
                IBlockState state = world.getBlockState(pos);
                world.setBlockToAir(pos);
                BlockPos blockpos;

                for(blockpos = pos.down(); (world.isAirBlock(blockpos) || canRiseThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down())
                {

                }

                if(blockpos.getY() > 0)
                {
                    world.setBlockState(blockpos.up(), state);
                }
            }
        }
    }

    protected void onStartRising()
    {

    }

    public static boolean canRiseThrough(IBlockState state)
    {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

    public void onFinishRising(World world, BlockPos pos, IBlockState p_176502_3_, IBlockState p_176502_4_)
    {
    }

    public void onBroken(World world, BlockPos pos)
    {
    }
}
