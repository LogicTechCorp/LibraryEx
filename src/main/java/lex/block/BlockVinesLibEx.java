/*
 * LibEx
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

package lex.block;

import lex.IModData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class BlockVinesLibEx extends BlockLibEx implements IShearable
{
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[]{UP, NORTH, SOUTH, WEST, EAST};
    protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);

    public BlockVinesLibEx(IModData data, String name, Material material)
    {
        super(data, name, material);
        setSoundType(SoundType.PLANT);
        setDefaultState(blockState.getBaseState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
        setTickRandomly(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = state.getActualState(source, pos);
        int i = 0;
        AxisAlignedBB axisAlignedBB = FULL_BLOCK_AABB;

        if(state.getValue(UP))
        {
            axisAlignedBB = UP_AABB;
            i++;
        }

        if(state.getValue(NORTH))
        {
            axisAlignedBB = NORTH_AABB;
            i++;
        }

        if(state.getValue(EAST))
        {
            axisAlignedBB = EAST_AABB;
            i++;
        }

        if(state.getValue(SOUTH))
        {
            axisAlignedBB = SOUTH_AABB;
            i++;
        }

        if(state.getValue(WEST))
        {
            axisAlignedBB = WEST_AABB;
            i++;
        }

        return i == 1 ? axisAlignedBB : FULL_BLOCK_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if(!world.isRemote)
        {
            if(world.rand.nextInt(4) == 0)
            {
                int j = 5;
                boolean flag = false;
                label181:

                for(int posX = -4; posX <= 4; posX++)
                {
                    for(int posZ = -4; posZ <= 4; posZ++)
                    {
                        for(int posY = -1; posY <= 1; posY++)
                        {
                            if(world.getBlockState(pos.add(posX, posY, posZ)).getBlock() == this)
                            {
                                j--;

                                if(j <= 0)
                                {
                                    flag = true;
                                    break label181;
                                }
                            }
                        }
                    }
                }

                EnumFacing facing = EnumFacing.random(rand);
                BlockPos upPos = pos.up();

                if(facing == EnumFacing.UP && pos.getY() < 255 && world.isAirBlock(upPos))
                {
                    IBlockState originalState = state;

                    for(EnumFacing horizontalFacing : EnumFacing.Plane.HORIZONTAL)
                    {
                        if(rand.nextBoolean() && canAttachTo(world, upPos, horizontalFacing.getOpposite()))
                        {
                            originalState = originalState.withProperty(getPropertyFor(horizontalFacing), true);
                        }
                        else
                        {
                            originalState = originalState.withProperty(getPropertyFor(horizontalFacing), false);
                        }
                    }

                    if(originalState.getValue(NORTH) || originalState.getValue(EAST) || originalState.getValue(SOUTH) || originalState.getValue(WEST))
                    {
                        world.setBlockState(upPos, originalState, 2);
                    }
                }
                else if(facing.getAxis().isHorizontal() && !state.getValue(getPropertyFor(facing)))
                {
                    if(!flag)
                    {
                        BlockPos offsetPos = pos.offset(facing);
                        IBlockState checkState = world.getBlockState(offsetPos);

                        if(checkState.getMaterial() == Material.AIR)
                        {
                            EnumFacing rotatedFacing = facing.rotateY();
                            EnumFacing reverseRotatedFacing = facing.rotateYCCW();
                            boolean rotated = state.getValue(getPropertyFor(rotatedFacing));
                            boolean reverseRotated = state.getValue(getPropertyFor(reverseRotatedFacing));
                            BlockPos rotatedPos = offsetPos.offset(rotatedFacing);
                            BlockPos reverseRotatedPos = offsetPos.offset(reverseRotatedFacing);

                            if(rotated && canAttachTo(world, rotatedPos.offset(rotatedFacing), rotatedFacing))
                            {
                                world.setBlockState(offsetPos, getDefaultState().withProperty(getPropertyFor(rotatedFacing), true), 2);
                            }
                            else if(reverseRotated && canAttachTo(world, reverseRotatedPos.offset(reverseRotatedFacing), reverseRotatedFacing))
                            {
                                world.setBlockState(offsetPos, getDefaultState().withProperty(getPropertyFor(reverseRotatedFacing), true), 2);
                            }
                            else if(rotated && world.isAirBlock(rotatedPos) && canAttachTo(world, rotatedPos, facing))
                            {
                                world.setBlockState(rotatedPos, getDefaultState().withProperty(getPropertyFor(facing.getOpposite()), true), 2);
                            }
                            else if(reverseRotated && world.isAirBlock(reverseRotatedPos) && canAttachTo(world, reverseRotatedPos, facing))
                            {
                                world.setBlockState(reverseRotatedPos, getDefaultState().withProperty(getPropertyFor(facing.getOpposite()), true), 2);
                            }
                        }
                        else if(checkState.getBlockFaceShape(world, offsetPos, facing) == BlockFaceShape.SOLID)
                        {
                            world.setBlockState(pos, state.withProperty(getPropertyFor(facing), true), 2);
                        }
                    }
                }
                else
                {
                    if(pos.getY() > 1)
                    {
                        BlockPos downPos = pos.down();
                        IBlockState checkState = world.getBlockState(downPos);
                        Block checkBlock = checkState.getBlock();

                        if(checkState.getMaterial() == Material.AIR)
                        {
                            IBlockState originalState = state;

                            for(EnumFacing horizontalFacing : EnumFacing.Plane.HORIZONTAL)
                            {
                                if(rand.nextBoolean())
                                {
                                    originalState = originalState.withProperty(getPropertyFor(horizontalFacing), false);
                                }
                            }

                            if(originalState.getValue(NORTH) || originalState.getValue(EAST) || originalState.getValue(SOUTH) || originalState.getValue(WEST))
                            {
                                world.setBlockState(downPos, originalState, 2);
                            }
                        }
                        else if(checkBlock == this)
                        {
                            IBlockState originalCheckState = checkState;

                            for(EnumFacing horizontalFacing : EnumFacing.Plane.HORIZONTAL)
                            {
                                PropertyBool bool = getPropertyFor(horizontalFacing);

                                if(rand.nextBoolean() && state.getValue(bool))
                                {
                                    originalCheckState = originalCheckState.withProperty(bool, true);
                                }
                            }

                            if(originalCheckState.getValue(NORTH) || originalCheckState.getValue(EAST) || originalCheckState.getValue(SOUTH) || originalCheckState.getValue(WEST))
                            {
                                world.setBlockState(downPos, originalCheckState, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return side != EnumFacing.DOWN && side != EnumFacing.UP && canAttachTo(world, pos, side);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if(!world.isRemote && !recheckGrownSides(world, pos, state))
        {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState state = getDefaultState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false);
        return facing.getAxis().isHorizontal() ? state.withProperty(getPropertyFor(facing.getOpposite()), true) : state;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
        return true;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return Collections.singletonList(new ItemStack(this, 1));
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileEntity, ItemStack stack)
    {
        if(!world.isRemote && stack.getItem() == Items.SHEARS)
        {
            player.addStat(StatList.getBlockStats(this));
            spawnAsEntity(world, pos, new ItemStack(this, 1, 0));
        }
        else
        {
            super.harvestBlock(world, player, pos, state, tileEntity, stack);
        }
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation)
    {
        switch(rotation)
        {
            case CLOCKWISE_180:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror)
    {
        switch(mirror)
        {
            case LEFT_RIGHT:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
            default:
                return super.withMirror(state, mirror);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        BlockPos blockpos = pos.up();
        return state.withProperty(UP, world.getBlockState(blockpos).getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(SOUTH, (meta & 1) > 0).withProperty(WEST, (meta & 2) > 0).withProperty(NORTH, (meta & 4) > 0).withProperty(EAST, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = 0;

        if(state.getValue(SOUTH))
        {
            meta |= 1;
        }

        if(state.getValue(WEST))
        {
            meta |= 2;
        }

        if(state.getValue(NORTH))
        {
            meta |= 4;
        }

        if(state.getValue(EAST))
        {
            meta |= 8;
        }

        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, UP, NORTH, EAST, SOUTH, WEST);
    }

    public boolean canAttachTo(World world, BlockPos pos, EnumFacing facing)
    {
        Block block = world.getBlockState(pos.up()).getBlock();
        return isAcceptableNeighbor(world, pos.offset(facing.getOpposite()), facing) && (block == Blocks.AIR || block == Blocks.VINE || isAcceptableNeighbor(world, pos.up(), EnumFacing.UP));
    }

    private boolean isAcceptableNeighbor(World world, BlockPos pos, EnumFacing facing)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlockFaceShape(world, pos, facing) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(state.getBlock());
    }

    protected static boolean isExceptBlockForAttaching(Block block)
    {
        return block instanceof BlockShulkerBox || block == Blocks.BEACON || block == Blocks.CAULDRON || block == Blocks.GLASS || block == Blocks.STAINED_GLASS || block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD || block == Blocks.TRAPDOOR;
    }

    private boolean recheckGrownSides(World world, BlockPos pos, IBlockState state)
    {
        IBlockState originalState = state;

        for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
        {
            PropertyBool bool = getPropertyFor(facing);

            if(state.getValue(bool) && !canAttachTo(world, pos, facing.getOpposite()))
            {
                IBlockState checkState = world.getBlockState(pos.up());

                if(checkState.getBlock() != this || !checkState.getValue(bool))
                {
                    state = state.withProperty(bool, false);
                }
            }
        }

        if(getNumGrownFaces(state) == 0)
        {
            return false;
        }
        else
        {
            if(originalState != state)
            {
                world.setBlockState(pos, state, 2);
            }

            return true;
        }
    }

    public static PropertyBool getPropertyFor(EnumFacing side)
    {
        switch(side)
        {
            case UP:
                return UP;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            default:
                throw new IllegalArgumentException(side + " is an invalid choice");
        }
    }

    public static int getNumGrownFaces(IBlockState state)
    {
        int grownSides = 0;

        for(PropertyBool bool : ALL_FACES)
        {
            if(state.getValue(bool))
            {
                grownSides++;
            }
        }

        return grownSides;
    }
}
