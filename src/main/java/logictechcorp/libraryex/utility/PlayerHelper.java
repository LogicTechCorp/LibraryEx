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
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class PlayerHelper
{
    public static EntityRayTraceResult getRayTracedEntity(PlayerEntity player, World world, float partialTicks)
    {
        if(player != null)
        {
            if(world != null)
            {

                boolean creative = player.isCreative();
                double reachDistance = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();

                if(!creative)
                {
                    reachDistance -= 0.5F;
                }

                Vec3d playerEyePosition = player.getEyePosition(partialTicks);
                boolean flag = false;
                double modifiedReachDistance = reachDistance;

                if(creative)
                {
                    modifiedReachDistance = 6.0D;
                    reachDistance = modifiedReachDistance;
                }
                else
                {
                    if(reachDistance > 3.0D)
                    {
                        flag = true;
                    }
                }

                Vec3d playerLook = player.getLook(1.0F);
                Vec3d playerLookReach = playerEyePosition.add(playerLook.x * reachDistance, playerLook.y * reachDistance, playerLook.z * reachDistance);
                Entity pointedEntity = null;
                Vec3d resultHit = null;
                double modifiedReachDistanceTest = modifiedReachDistance;
                EntityRayTraceResult result = null;

                for(Entity entity : world.getEntitiesInAABBexcluding(player, player.getBoundingBox().expand(playerLook.x * reachDistance, playerLook.y * reachDistance, playerLook.z * reachDistance).grow(1.0D, 1.0D, 1.0D), EntityPredicates.NOT_SPECTATING.and(entity -> entity != null && entity.canBeCollidedWith())))
                {
                    AxisAlignedBB entityBoundingBox = entity.getBoundingBox().grow((double) entity.getCollisionBorderSize());
                    Optional<Vec3d> testResult = entityBoundingBox.rayTrace(playerEyePosition, playerLookReach);

                    if(entityBoundingBox.contains(playerEyePosition))
                    {
                        if(modifiedReachDistanceTest >= 0.0D)
                        {
                            pointedEntity = entity;
                            resultHit = testResult.orElse(playerEyePosition);
                            modifiedReachDistanceTest = 0.0D;
                        }
                    }
                    else if(testResult.isPresent())
                    {
                        double distanceToResult = playerEyePosition.distanceTo(testResult.get());

                        if(distanceToResult < modifiedReachDistanceTest || modifiedReachDistanceTest == 0.0D)
                        {
                            if(entity.getLowestRidingEntity() == player.getLowestRidingEntity() && !entity.canRiderInteract())
                            {
                                if(modifiedReachDistanceTest == 0.0D)
                                {
                                    pointedEntity = entity;
                                    resultHit = testResult.get();
                                }
                            }
                            else
                            {
                                pointedEntity = entity;
                                resultHit = testResult.get();
                                modifiedReachDistanceTest = distanceToResult;
                            }
                        }
                    }
                }

                if(pointedEntity != null && flag && playerEyePosition.distanceTo(resultHit) > 3.0D)
                {
                    result = new EntityRayTraceResult(pointedEntity, resultHit);
                }

                if(pointedEntity != null && (modifiedReachDistanceTest < modifiedReachDistance || result == null))
                {
                    result = new EntityRayTraceResult(pointedEntity, resultHit);
                }

                return result;
            }
        }

        return null;
    }
}
