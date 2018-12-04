/*
 * LibraryEx
 * Copyright (c) 2017-2018 by MineEx
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

import logictechcorp.libraryex.IModData;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class BlockLeavesLibEx extends BlockLibEx implements IShearable
{
    public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
    public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
    private int[] surroundings;

    public BlockLeavesLibEx(IModData data, String name, Material material)
    {
        super(data, name, material);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return Blocks.LEAVES.getRenderLayer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return Blocks.LEAVES.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand)
    {
        if(world.isRainingAt(pos.up()) && !world.getBlockState(pos.down()).isTopSolid() && rand.nextInt(15) == 1)
        {
            world.spawnParticle(EnumParticleTypes.DRIP_WATER, pos.getX() + rand.nextFloat(), pos.getY() - 0.05D, pos.getZ() + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return Blocks.LEAVES.isOpaqueCube(state);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if(!world.isRemote)
        {
            if((state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)))
            {
                int posX = pos.getX();
                int posY = pos.getY();
                int posZ = pos.getZ();

                if(this.surroundings == null)
                {
                    this.surroundings = new int[32768];
                }

                if(world.isAreaLoaded(new BlockPos(posX - 5, posY - 5, posZ - 5), new BlockPos(posX + 5, posY + 5, posZ + 5)))
                {
                    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                    for(int offsetX = -4; offsetX <= 4; offsetX++)
                    {
                        for(int offsetY = -4; offsetY <= 4; offsetY++)
                        {
                            for(int offsetZ = -4; offsetZ <= 4; offsetZ++)
                            {
                                IBlockState testState = world.getBlockState(mutableBlockPos.setPos(posX + offsetX, posY + offsetY, posZ + offsetZ));
                                Block testBlock = testState.getBlock();

                                if(!testBlock.canSustainLeaves(testState, world, mutableBlockPos.setPos(posX + offsetX, posY + offsetY, posZ + offsetZ)))
                                {
                                    if(testBlock.isLeaves(testState, world, mutableBlockPos.setPos(posX + offsetX, posY + offsetY, posZ + offsetZ)))
                                    {
                                        this.surroundings[(offsetX + 16) * 1024 + (offsetY + 16) * 32 + offsetZ + 16] = -2;
                                    }
                                    else
                                    {
                                        this.surroundings[(offsetX + 16) * 1024 + (offsetY + 16) * 32 + offsetZ + 16] = -1;
                                    }
                                }
                                else
                                {
                                    this.surroundings[(offsetX + 16) * 1024 + (offsetY + 16) * 32 + offsetZ + 16] = 0;
                                }
                            }
                        }
                    }

                    for(int check = 1; check <= 4; check++)
                    {
                        for(int localX = -4; localX <= 4; localX++)
                        {
                            for(int localY = -4; localY <= 4; localY++)
                            {
                                for(int localZ = -4; localZ <= 4; localZ++)
                                {
                                    if(this.surroundings[(localX + 16) * 1024 + (localY + 16) * 32 + localZ + 16] == check - 1)
                                    {
                                        if(this.surroundings[(localX + 16 - 1) * 1024 + (localY + 16) * 32 + localZ + 16] == -2)
                                        {
                                            this.surroundings[(localX + 16 - 1) * 1024 + (localY + 16) * 32 + localZ + 16] = check;
                                        }

                                        if(this.surroundings[(localX + 16 + 1) * 1024 + (localY + 16) * 32 + localZ + 16] == -2)
                                        {
                                            this.surroundings[(localX + 16 + 1) * 1024 + (localY + 16) * 32 + localZ + 16] = check;
                                        }

                                        if(this.surroundings[(localX + 16) * 1024 + (localY + 16 - 1) * 32 + localZ + 16] == -2)
                                        {
                                            this.surroundings[(localX + 16) * 1024 + (localY + 16 - 1) * 32 + localZ + 16] = check;
                                        }

                                        if(this.surroundings[(localX + 16) * 1024 + (localY + 16 + 1) * 32 + localZ + 16] == -2)
                                        {
                                            this.surroundings[(localX + 16) * 1024 + (localY + 16 + 1) * 32 + localZ + 16] = check;
                                        }

                                        if(this.surroundings[(localX + 16) * 1024 + (localY + 16) * 32 + (localZ + 16 - 1)] == -2)
                                        {
                                            this.surroundings[(localX + 16) * 1024 + (localY + 16) * 32 + (localZ + 16 - 1)] = check;
                                        }

                                        if(this.surroundings[(localX + 16) * 1024 + (localY + 16) * 32 + localZ + 16 + 1] == -2)
                                        {
                                            this.surroundings[(localX + 16) * 1024 + (localY + 16) * 32 + localZ + 16 + 1] = check;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(this.surroundings[16912] >= 0)
                {
                    world.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4);
                }
                else
                {
                    this.destroy(world, pos);
                }
            }
        }
    }

    @Override
    public int quantityDropped(Random random)
    {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        if(world.isAreaLoaded(new BlockPos(posX - 2, posY - 2, posZ - 2), new BlockPos(posX + 2, posY + 2, posZ + 2)))
        {
            for(int offsetX = -1; offsetX <= 1; ++offsetX)
            {
                for(int offsetY = -1; offsetY <= 1; ++offsetY)
                {
                    for(int offsetZ = -1; offsetZ <= 1; ++offsetZ)
                    {
                        BlockPos newPos = pos.add(offsetX, offsetY, offsetZ);
                        IBlockState checkState = world.getBlockState(newPos);

                        if(checkState.getBlock().isLeaves(checkState, world, newPos))
                        {
                            checkState.getBlock().beginLeavesDecay(checkState, world, newPos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        if(!state.getValue(CHECK_DECAY))
        {
            world.setBlockState(pos, state.withProperty(CHECK_DECAY, true), 4);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        int chance = this.getSaplingDropChance(state);

        if(fortune > 0)
        {
            chance -= 2 << fortune;

            if(chance < 10)
            {
                chance = 10;
            }
        }
        if(rand.nextInt(chance) == 0)
        {

            ItemStack drop = new ItemStack(this.getItemDropped(state, rand, fortune), 1, this.damageDropped(state));

            if(!drop.isEmpty())
            {
                drops.add(drop);
            }
        }

        chance = 200;

        if(fortune > 0)
        {
            chance -= 10 << fortune;

            if(chance < 40)
            {
                chance = 40;
            }
        }

        this.captureDrops(true);

        if(world instanceof World)
        {
            this.dropRareItem((World) world, pos, state, chance);
        }

        drops.addAll(this.captureDrops(false));
    }

    @Override
    public abstract Item getItemDropped(IBlockState state, Random rand, int fortune);

    protected abstract void dropRareItem(World world, BlockPos pos, IBlockState state, int chance);

    protected abstract int getSaplingDropChance(IBlockState state);

    private void destroy(World world, BlockPos pos)
    {
        this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
    }
}
