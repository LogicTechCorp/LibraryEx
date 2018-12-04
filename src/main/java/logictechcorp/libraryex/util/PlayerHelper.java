package logictechcorp.libraryex.util;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerHelper
{
    public static RayTraceResult getRayTracedEntity(EntityPlayer player, World world, float partialTicks)
    {
        if(player != null)
        {
            if(world != null)
            {

                boolean creative = player.isCreative();
                double reachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();

                if(!creative)
                {
                    reachDistance -= 0.5F;
                }

                Vec3d playerEyePosition = player.getPositionEyes(partialTicks);
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
                RayTraceResult result = null;

                for(Entity entity : world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(playerLook.x * reachDistance, playerLook.y * reachDistance, playerLook.z * reachDistance).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, entity -> entity != null && entity.canBeCollidedWith())))
                {
                    AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().grow((double) entity.getCollisionBorderSize());
                    RayTraceResult testResult = entityBoundingBox.calculateIntercept(playerEyePosition, playerLookReach);

                    if(entityBoundingBox.contains(playerEyePosition))
                    {
                        if(modifiedReachDistanceTest >= 0.0D)
                        {
                            pointedEntity = entity;
                            resultHit = testResult == null ? playerEyePosition : testResult.hitVec;
                            modifiedReachDistanceTest = 0.0D;
                        }
                    }
                    else if(testResult != null)
                    {
                        double distanceToResult = playerEyePosition.distanceTo(testResult.hitVec);

                        if(distanceToResult < modifiedReachDistanceTest || modifiedReachDistanceTest == 0.0D)
                        {
                            if(entity.getLowestRidingEntity() == player.getLowestRidingEntity() && !entity.canRiderInteract())
                            {
                                if(modifiedReachDistanceTest == 0.0D)
                                {
                                    pointedEntity = entity;
                                    resultHit = testResult.hitVec;
                                }
                            }
                            else
                            {
                                pointedEntity = entity;
                                resultHit = testResult.hitVec;
                                modifiedReachDistanceTest = distanceToResult;
                            }
                        }
                    }
                }

                if(pointedEntity != null && flag && playerEyePosition.distanceTo(resultHit) > 3.0D)
                {
                    pointedEntity = null;
                    result = new RayTraceResult(RayTraceResult.Type.MISS, resultHit, null, new BlockPos(resultHit));
                }

                if(pointedEntity != null && (modifiedReachDistanceTest < modifiedReachDistance || result == null))
                {
                    result = new RayTraceResult(pointedEntity, resultHit);
                }

                return result;
            }
        }

        return null;
    }
}
