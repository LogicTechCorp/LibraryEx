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

package logictechcorp.libraryex.item;

import logictechcorp.libraryex.block.BlockModSlab;
import logictechcorp.libraryex.item.builder.ItemBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockModSlab extends ItemBlockMod
{
    public ItemBlockModSlab(BlockModSlab block, ItemBuilder builder)
    {
        super(block, builder);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        IBlockState state = world.getBlockState(pos);

        if(((BlockModSlab) this.block).isSingle(state))
        {
            boolean flag = state.getValue(BlockModSlab.TYPE) == BlockModSlab.SlabType.TOP;

            if((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag))
            {
                return true;
            }
        }

        return ((BlockModSlab) this.block).isSingle(world.getBlockState(pos.offset(side))) || super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);

        if(!stack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, stack))
        {
            IBlockState state = world.getBlockState(pos);

            if(((BlockModSlab) this.block).isSingle(state))
            {
                BlockModSlab.SlabType type = state.getValue(BlockModSlab.TYPE);

                if((facing == EnumFacing.UP && type == BlockModSlab.SlabType.BOTTOM || facing == EnumFacing.DOWN && type == BlockModSlab.SlabType.TOP))
                {
                    IBlockState newState = this.block.getDefaultState().withProperty(BlockModSlab.TYPE, BlockModSlab.SlabType.DOUBLE);
                    AxisAlignedBB boundingBox = newState.getCollisionBoundingBox(world, pos);

                    if(boundingBox != Block.NULL_AABB && world.checkNoEntityCollision(boundingBox.offset(pos)) && world.setBlockState(pos, newState, 11))
                    {
                        SoundType soundtype = this.block.getSoundType(newState, world, pos, player);
                        world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        stack.shrink(1);

                        if(player instanceof EntityPlayerMP)
                        {
                            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
                        }
                    }

                    return EnumActionResult.SUCCESS;
                }
            }

            return this.tryPlace(player, stack, world, pos.offset(facing)) ? EnumActionResult.SUCCESS : super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    private boolean tryPlace(EntityPlayer player, ItemStack stack, World world, BlockPos pos)
    {
        if(((BlockModSlab) this.block).isSingle(world.getBlockState(pos)))
        {
            IBlockState placeState = this.block.getDefaultState().withProperty(BlockModSlab.TYPE, BlockModSlab.SlabType.DOUBLE);
            AxisAlignedBB boundingBox = placeState.getCollisionBoundingBox(world, pos);

            if(boundingBox != Block.NULL_AABB && world.checkNoEntityCollision(boundingBox.offset(pos)) && world.setBlockState(pos, placeState, 11))
            {
                SoundType soundtype = this.block.getSoundType(placeState, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);
            }

            return true;
        }

        return false;
    }
}
