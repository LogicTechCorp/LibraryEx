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
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockModSlab extends BlockMod
{
    public static final PropertyEnum<SlabType> TYPE = PropertyEnum.create("type", SlabType.class);

    protected static final AxisAlignedBB AABB_TOP_HALF = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    public BlockModSlab(ResourceLocation registryName, BlockProperties properties)
    {
        super(registryName, properties);
        this.setLightOpacity(255);
        this.useNeighborBrightness = true;
    }

    @Override
    public void getSubBlocks(CreativeTabs creativeTab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if(this.isDouble(state))
        {
            return super.shouldSideBeRendered(state, world, pos, side);
        }
        else if(side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(state, world, pos, side))
        {
            return false;
        }
        return super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if(this.isDouble(state))
        {
            return FULL_BLOCK_AABB;
        }
        else
        {
            return state.getValue(TYPE) == SlabType.TOP ? AABB_TOP_HALF : AABB_BOTTOM_HALF;
        }
    }

    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return ((BlockModSlab) state.getBlock()).isDouble(state) || state.getValue(TYPE) == SlabType.TOP;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return this.isDouble(state);
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return state.getValue(TYPE) == SlabType.DOUBLE;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        if(ForgeModContainer.disableStairSlabCulling)
        {
            return super.doesSideBlockRendering(state, world, pos, face);
        }

        if(state.isOpaqueCube())
        {
            return true;
        }

        SlabType type = state.getValue(TYPE);
        return (type == SlabType.TOP && face == EnumFacing.UP) || (type == SlabType.BOTTOM && face == EnumFacing.DOWN);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        if(((BlockModSlab) state.getBlock()).isDouble(state))
        {
            return BlockFaceShape.SOLID;
        }
        else if(face == EnumFacing.UP && state.getValue(TYPE) == SlabType.TOP)
        {
            return BlockFaceShape.SOLID;
        }
        else
        {
            return face == EnumFacing.DOWN && state.getValue(TYPE) == SlabType.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = this.getStateFromMeta(meta);
        return (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? state.withProperty(TYPE, SlabType.BOTTOM) : state.withProperty(TYPE, SlabType.TOP));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        return state.getValue(TYPE) == SlabType.DOUBLE ? 2 : 1;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return SlabType.BOTTOM.ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, SlabType.fromMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TYPE);
    }

    public boolean isSingle(IBlockState state)
    {
        return state.getBlock() == this && state != this.getDefaultState().withProperty(BlockModSlab.TYPE, SlabType.DOUBLE);
    }

    public boolean isDouble(IBlockState state)
    {
        return state.getBlock() == this && state == this.getDefaultState().withProperty(BlockModSlab.TYPE, SlabType.DOUBLE);
    }

    public enum SlabType implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom"),
        DOUBLE("double");

        private final String name;

        SlabType(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return this.name;
        }

        @Override
        public String getName()
        {
            return this.name;
        }

        public static SlabType fromMeta(int meta)
        {
            if(meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
    }
}
