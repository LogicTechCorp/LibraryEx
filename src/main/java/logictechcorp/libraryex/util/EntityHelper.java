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

package logictechcorp.libraryex.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityHelper
{
    private static final Map<Class<? extends EntityLivingBase>, String> ENTITY_RESOURCE_LOCATION_CACHE = new HashMap<>();

    public static String getEntityLocation(EntityLivingBase entity)
    {
        Class<? extends EntityLivingBase> cls = entity.getClass();
        return ENTITY_RESOURCE_LOCATION_CACHE.computeIfAbsent(cls, k -> {
            ResourceLocation location = EntityList.getKey(k);
            return location != null ? location.toString() : null;
        });
    }

    public static Entity getFromUUID(MinecraftServer server, UUID uuid)
    {
        if(server != null && uuid != null)
        {
            return server.getEntityFromUuid(uuid);
        }

        return null;
    }

    public static boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
    {
        if(!(target instanceof EntityCreeper) && !(target instanceof EntityFlying))
        {
            if(target instanceof EntityWolf)
            {
                EntityWolf wolf = (EntityWolf) target;

                if(wolf.isTamed() && wolf.getOwner() == owner)
                {
                    return false;
                }
            }

            if(target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target))
            {
                return false;
            }
            else
            {
                return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean isInBlock(Entity entity, IBlockState... states)
    {
        AxisAlignedBB boundingBox = entity.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);
        int minX = MathHelper.floor(boundingBox.minX);
        int maxX = MathHelper.ceil(boundingBox.maxX);
        int minY = MathHelper.floor(boundingBox.minY);
        int maxY = MathHelper.ceil(boundingBox.maxY);
        int minZ = MathHelper.floor(boundingBox.minZ);
        int maxZ = MathHelper.ceil(boundingBox.maxZ);
        BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain();

        for(int posX = minX; posX < maxX; posX++)
        {
            for(int posY = minY; posY < maxY; posY++)
            {
                for(int posZ = minZ; posZ < maxZ; posZ++)
                {
                    IBlockState checkState = entity.getEntityWorld().getBlockState(pooledMutableBlockPos.setPos(posX, posY, posZ));

                    for(IBlockState state : states)
                    {
                        if(state == checkState)
                        {
                            pooledMutableBlockPos.release();
                            return true;
                        }

                    }
                }
            }
        }

        pooledMutableBlockPos.release();
        return false;
    }

}
