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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.loading.FMLPaths;

public class WorldHelper
{
    public static BlockRayTraceResult rayTraceFromEntity(World world, Entity entity, double range, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode)
    {
        float f = 1.0F;
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * f;
        float f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * f;
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) f;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) f;

        if(!world.isRemote && entity instanceof PlayerEntity)
        {
            d1 += 1.62D;
        }

        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) f;
        Vec3d vec = new Vec3d(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double adjustedRange = range;

        if(entity instanceof ServerPlayerEntity)
        {
            adjustedRange = ((ServerPlayerEntity) entity).interactionManager.player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        }

        Vec3d adjustedVec = vec.add((double) f7 * adjustedRange, (double) f6 * adjustedRange, (double) f8 * adjustedRange);
        return world.rayTraceBlocks(new RayTraceContext(vec, adjustedVec, blockMode, fluidMode, entity));
    }

    public static boolean isDaytime(World world)
    {
        long time = world.getDayTime();
        return time >= 1000 && time < 13000;
    }

    public static boolean isChunkLoaded(World world, ChunkPos chunkPos)
    {
        Chunk chunk = world.getChunkProvider().getChunk(chunkPos.x, chunkPos.z, false);
        return chunk != null && chunk.isModified();
    }

    public static String getSaveDirectory(World world)
    {
        MinecraftServer server = world.getServer();
        String worldName = world.getWorldInfo().getWorldName();

        if(server != null)
        {
            return server.getActiveAnvilConverter().getSaveLoader(worldName, server).getWorldDirectory().toString();
        }

        return FMLPaths.GAMEDIR.get().toString();
    }
}
