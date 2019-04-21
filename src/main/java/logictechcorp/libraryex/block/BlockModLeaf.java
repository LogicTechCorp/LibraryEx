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

import logictechcorp.libraryex.block.builder.BlockProperties;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public abstract class BlockModLeaf extends BlockMod implements IShearable
{
    public static final PropertyBool DECAY = PropertyBool.create("decay");
    private int[] surroundings;

    public BlockModLeaf(ResourceLocation registryName, BlockProperties properties)
    {
        super(registryName, properties);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DECAY, true));
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
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random random)
    {
        if(world.isRainingAt(pos.up()) && !world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) && random.nextInt(15) == 1)
        {
            world.spawnParticle(EnumParticleTypes.DRIP_WATER, pos.getX() + random.nextFloat(), pos.getY() - 0.05D, pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return Blocks.LEAVES.isOpaqueCube(state);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if(!world.isRemote)
        {
            if((state.getValue(DECAY)))
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
                    world.setBlockState(pos, state.withProperty(DECAY, false), 4);
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
            for(int offsetX = -1; offsetX <= 1; offsetX++)
            {
                for(int offsetY = -1; offsetY <= 1; offsetY++)
                {
                    for(int offsetZ = -1; offsetZ <= 1; offsetZ++)
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
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileEntity, ItemStack stack)
    {
        if(!world.isRemote && stack.getItem() == Items.SHEARS)
        {
            StatBase statBase = StatList.getBlockStats(this);

            if(statBase != null)
            {
                player.addStat(statBase);
            }

            spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(this)));
        }
        else
        {
            super.harvestBlock(world, player, pos, state, tileEntity, stack);
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(this));
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        if(!state.getValue(DECAY))
        {
            world.setBlockState(pos, state.withProperty(DECAY, true), 4);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random random = world instanceof World ? ((World) world).rand : RandomHelper.getRandom();
        int chance = this.getRareDropChance();

        if(fortune > 0)
        {
            chance -= 2 << fortune;

            if(chance < 10)
            {
                chance = 10;
            }
        }
        if(random.nextInt(chance) == 0)
        {
            ItemStack drop = new ItemStack(this.getItemDropped(state, random, fortune), 1, this.damageDropped(state));

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
            this.dropRareItem((World) world, pos, chance);
        }

        drops.addAll(this.captureDrops(false));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DECAY, meta != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(DECAY) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, DECAY);
    }

    protected void destroy(World world, BlockPos pos)
    {
        this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
    }

    @Override
    public abstract Item getItemDropped(IBlockState state, Random random, int fortune);

    protected abstract void dropRareItem(World world, BlockPos pos, int chance);

    protected abstract int getRareDropChance();
}
