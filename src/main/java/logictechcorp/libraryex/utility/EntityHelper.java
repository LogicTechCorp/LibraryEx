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

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

public class EntityHelper
{
    private static final Map<EntityType<?>, ResourceLocation> ENTITY_RESOURCE_LOCATION_CACHE = new HashMap<>();

    public static ResourceLocation getEntityLocation(EntityType<?> entityType)
    {
        return ENTITY_RESOURCE_LOCATION_CACHE.computeIfAbsent(entityType, EntityType::getKey);
    }

    public static boolean shouldAttackEntity(LivingEntity target, LivingEntity owner)
    {
        if(!(target instanceof CreeperEntity) && !(target instanceof FlyingEntity))
        {
            if(target instanceof WolfEntity)
            {
                WolfEntity wolf = (WolfEntity) target;

                if(wolf.isTamed() && wolf.getOwner() == owner)
                {
                    return false;
                }
            }

            if(target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target))
            {
                return false;
            }
            else
            {
                return !(target instanceof AbstractHorseEntity) || !((AbstractHorseEntity) target).isTame();
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean isInBlock(Entity entity, BlockState... states)
    {
        AxisAlignedBB boundingBox = entity.getBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);
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
                    BlockState checkState = entity.getEntityWorld().getBlockState(pooledMutableBlockPos.setPos(posX, posY, posZ));

                    for(BlockState state : states)
                    {
                        if(state == checkState)
                        {
                            pooledMutableBlockPos.close();
                            return true;
                        }

                    }
                }
            }
        }

        pooledMutableBlockPos.close();
        return false;
    }

}
