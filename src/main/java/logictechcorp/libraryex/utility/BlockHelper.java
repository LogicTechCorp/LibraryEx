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

package logictechcorp.libraryex.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ForgeHooks;

public class BlockHelper
{
    public static boolean mine3x3(World world, ItemStack stack, BlockPos pos, PlayerEntity player)
    {
        BlockRayTraceResult traceResult = WorldHelper.rayTraceFromEntity(world, player, 4.5D, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE);

        if(traceResult == null)
        {
            return true;
        }

        Direction sideHit = traceResult.getFace();

        BlockPos startPos;
        BlockPos endPos;

        if(sideHit.getAxis() == Direction.Axis.X)
        {
            startPos = new BlockPos(0, 1, 1);
            endPos = new BlockPos(0, -1, -1);
        }
        else if(sideHit.getAxis() == Direction.Axis.Y)
        {
            startPos = new BlockPos(1, 0, 1);
            endPos = new BlockPos(-1, 0, -1);
        }
        else
        {
            startPos = new BlockPos(1, 1, 0);
            endPos = new BlockPos(-1, -1, 0);
        }

        Iterable<BlockPos> posIter = BlockPos.getAllInBoxMutable(startPos, endPos);
        BlockState originalState = world.getBlockState(pos);
        float originalStrength = originalState.getPlayerRelativeBlockHardness(player, world, pos);

        boolean canHarvestBedrock = false;

        if(originalState.getBlock() == Blocks.BEDROCK)
        {
            if(player.dimension == DimensionType.NETHER && pos.getY() >= 120)
            {
                canHarvestBedrock = true;
            }
        }

        for(BlockPos testPos : posIter)
        {
            testPos = testPos.add(pos);

            if(testPos.equals(pos))
            {
                continue;
            }

            BlockState testState = world.getBlockState(testPos);
            float testStrength = testState.getPlayerRelativeBlockHardness(player, world, testPos);
            boolean canBeHarvested = ForgeHooks.canHarvestBlock(testState, player, world, testPos);

            if(originalState.getMaterial() == testState.getMaterial() && testStrength > 0.0F && (originalStrength / testStrength) <= 10.0F || canHarvestBedrock)
            {
                if(canBeHarvested && stack.canHarvestBlock(originalState) || canBeHarvested)
                {
                    BlockHelper.tryToHarvest(world, testState, testPos, player, sideHit);
                }
            }
        }

        if(canHarvestBedrock)
        {
            BlockHelper.tryToHarvest(world, originalState, pos, player, sideHit);
        }

        return false;
    }

    /**
     * A method that harvests blocks when they aren't able to normally
     * <p>
     * Written by VapourDrive here:
     * https://github.com/VapourDrive/Hammerz/blob/55d31b8f8fd463d127110de04b2562605604e85c/src/main/java/vapourdrive/hammerz/utils/BlockUtils.java#L21
     *
     * @author VapourDrive
     */
    public static boolean tryToHarvest(World world, BlockState state, BlockPos pos, PlayerEntity player, Direction side)
    {
        Block block = state.getBlock();

        if(world.isAirBlock(pos))
        {
            return false;
        }

        ServerPlayerEntity playerMP = null;

        if(player instanceof ServerPlayerEntity)
        {
            playerMP = (ServerPlayerEntity) player;
        }

        ItemStack stack = player.getHeldItemMainhand();

        if(stack.isEmpty())
        {
            return false;
        }
        if(!(stack.getItem().getToolTypes(stack).contains(state.getHarvestTool()) || stack.getItem().getDestroySpeed(stack, state) > 1.0F))
        {
            return false;
        }
        if(!ForgeHooks.canHarvestBlock(state, player, world, pos))
        {

            return false;
        }

        int event = 0;

        if(playerMP != null)
        {
            event = ForgeHooks.onBlockBreakEvent(world, world.getWorldInfo().getGameType(), playerMP, pos);

            if(event == -1)
            {
                return false;
            }
        }

        world.playEvent(playerMP, 2001, pos, Block.getStateId(state));

        if(player.abilities.isCreativeMode)
        {
            if(!world.isRemote)
            {
                block.onBlockHarvested(world, pos, state, player);
            }
            if(block.removedByPlayer(state, world, pos, player, false, world.getFluidState(pos)))
            {
                block.onPlayerDestroy(world, pos, state);
            }
            if(!world.isRemote)
            {
                if(playerMP != null)
                {
                    playerMP.connection.sendPacket(new SChangeBlockPacket(world, pos));
                }
            }
            else
            {
                Minecraft.getInstance().getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, side));
            }
            return true;
        }
        if(!world.isRemote)
        {
            if(playerMP != null)
            {
                block.onBlockHarvested(world, pos, state, player);

                if(block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos)))
                {
                    block.onPlayerDestroy(world, pos, state);
                    block.harvestBlock(world, player, pos, state, null, stack);
                    block.dropXpOnBlockBreak(world, pos, event);
                }

                playerMP.connection.sendPacket(new SChangeBlockPacket(world, pos));
            }
        }
        else
        {
            if(block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos)))
            {
                block.onPlayerDestroy(world, pos, state);
            }

            Minecraft.getInstance().getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, side));
        }
        return true;
    }

    public static float getEnchantPower(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }

}
