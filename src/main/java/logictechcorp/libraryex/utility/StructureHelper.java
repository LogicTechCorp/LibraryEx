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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.PlacementSettings;

public class StructureHelper
{
    public static BlockPos getGroundPos(World world, BlockPos pos, PlacementSettings placementSettings, BlockPos structureSize, double clearancePercentage)
    {
        int sizeX = structureSize.getX() + 2;
        int sizeY = structureSize.getY() + 1;
        int sizeZ = structureSize.getZ() + 2;

        int startX = (structureSize.getX() / 2);
        int startZ = (structureSize.getZ() / 2);
        int endX = (sizeX - startX);
        int endZ = (sizeZ - startZ);

        if(world.isAreaLoaded(pos.add(orientBlockPos(new BlockPos(startX, 0, startZ), placementSettings)), pos.add(orientBlockPos(new BlockPos(endX, 0, endZ), placementSettings))))
        {
            while(pos.getY() > 0)
            {
                int groundBlocks = 0;
                int replaceableBlocks = 0;

                for(int x = -startX; x < endX; x++)
                {
                    for(int z = -startZ; z < endZ; z++)
                    {
                        BlockPos newPos = pos.add(orientBlockPos(new BlockPos(x, 0, z), placementSettings));

                        if(!world.getBlockState(newPos).getMaterial().isReplaceable() && world.getBlockState(newPos.up()).getMaterial().isReplaceable())
                        {
                            groundBlocks++;
                        }
                    }
                }

                if(groundBlocks >= (int) Math.floor(sizeX * sizeZ * clearancePercentage))
                {
                    for(int y = 0; y < sizeY; y++)
                    {
                        for(int x = -startX; x < endX; x++)
                        {
                            for(int z = -startZ; z < endZ; z++)
                            {
                                BlockPos newPos = pos.add(orientBlockPos(new BlockPos(x, y, z), placementSettings));

                                if(world.getBlockState(newPos).getMaterial().isReplaceable())
                                {
                                    replaceableBlocks++;
                                }
                            }
                        }
                    }
                }

                if((groundBlocks + replaceableBlocks) >= (int) Math.floor(sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos.add((-startX + 1), 1, (-startZ + 1));
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getAirPos(World world, BlockPos pos, PlacementSettings placementSettings, BlockPos structureSize, double clearancePercentage)
    {
        int sizeX = structureSize.getX() + 2;
        int sizeY = structureSize.getY() + 1;
        int sizeZ = structureSize.getZ() + 2;

        int startX = (structureSize.getX() / 2);
        int startZ = (structureSize.getZ() / 2);
        int endX = (sizeX - startX);
        int endZ = (sizeZ - startZ);

        if(world.isAreaLoaded(pos.add(orientBlockPos(new BlockPos(startX, 0, startZ), placementSettings)), pos.add(orientBlockPos(new BlockPos(endX, 0, endZ), placementSettings))))
        {
            while(pos.getY() > world.getSeaLevel() + 1)
            {
                int replaceableBlocks = 0;

                for(int y = 0; y < sizeY; y++)
                {
                    for(int x = -startX; x < endX; x++)
                    {
                        for(int z = -startZ; z < endZ; z++)
                        {
                            BlockPos newPos = pos.add(orientBlockPos(new BlockPos(x, y, z), placementSettings));

                            if(world.getBlockState(newPos).getMaterial().isReplaceable())
                            {
                                replaceableBlocks++;
                            }
                        }
                    }
                }

                if(replaceableBlocks >= (int) Math.floor(sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos.add((-startX + 1), 0, (-startZ + 1));
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getBuriedPos(World world, BlockPos pos, PlacementSettings placementSettings, BlockPos structureSize, double clearancePercentage)
    {
        int sizeX = structureSize.getX() + 2;
        int sizeY = structureSize.getY() + 1;
        int sizeZ = structureSize.getZ() + 2;

        int startX = (structureSize.getX() / 2);
        int startZ = (structureSize.getZ() / 2);
        int endX = (sizeX - startX);
        int endZ = (sizeZ - startZ);

        if(world.isAreaLoaded(pos.add(orientBlockPos(new BlockPos(startX, 0, startZ), placementSettings)), pos.add(orientBlockPos(new BlockPos(endX, 0, endZ), placementSettings))))
        {
            while(pos.getY() > 32)
            {
                int nonReplaceableBlocks = 0;

                for(int y = 0; y < sizeY; y++)
                {
                    for(int x = -startX; x < endX; x++)
                    {
                        for(int z = -startZ; z < endZ; z++)
                        {
                            BlockPos newPos = pos.add(orientBlockPos(new BlockPos(x, y, z), placementSettings));

                            if(!world.getBlockState(newPos).getMaterial().isReplaceable())
                            {
                                nonReplaceableBlocks++;
                            }
                        }
                    }
                }

                if(nonReplaceableBlocks >= (int) Math.floor(sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos.add((-startX + 1), 0, (-startZ + 1));
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getCeilingPos(World world, BlockPos pos, PlacementSettings placementSettings, BlockPos structureSize, double clearancePercentage)
    {
        int sizeX = structureSize.getX() + 2;
        int sizeY = structureSize.getY() + 1;
        int sizeZ = structureSize.getZ() + 2;

        int startX = (structureSize.getX() / 2);
        int startZ = (structureSize.getZ() / 2);
        int endX = (sizeX - startX);
        int endZ = (sizeZ - startZ);

        if(world.isAreaLoaded(pos.add(orientBlockPos(new BlockPos(startX, 0, startZ), placementSettings)), pos.add(orientBlockPos(new BlockPos(endX, 0, endZ), placementSettings))))
        {
            while(pos.getY() < world.getActualHeight())
            {
                int ceilingBlocks = 0;
                int replaceableBlocks = 0;

                for(int x = -startX; x < endX; x++)
                {
                    for(int z = -startZ; z < endZ; z++)
                    {
                        BlockPos newPos = pos.add(orientBlockPos(new BlockPos(x, 0, z), placementSettings));
                        BlockState state = world.getBlockState(newPos);

                        if(Block.doesSideFillSquare(state.getCollisionShape(world, newPos, ISelectionContext.dummy()), Direction.DOWN))
                        {
                            ceilingBlocks++;
                        }
                    }
                }

                if(ceilingBlocks >= (int) Math.floor(sizeX * sizeZ * clearancePercentage))
                {
                    for(int y = 0; y < sizeY; y++)
                    {
                        for(int x = -startX; x < endX; x++)
                        {
                            for(int z = -startZ; z < endZ; z++)
                            {
                                BlockPos newPos = pos.add(orientBlockPos(new BlockPos(x, -y, z), placementSettings));

                                if(world.isAirBlock(newPos))
                                {
                                    replaceableBlocks++;
                                }
                            }
                        }
                    }
                }

                if((ceilingBlocks + replaceableBlocks) >= (int) Math.floor(sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos.add((-startX + 1), -sizeY + 1, (-startZ + 1));
                }

                pos = pos.up();
            }
        }

        return null;
    }

    public static BlockPos orientBlockPos(BlockPos pos, PlacementSettings placementSettings)
    {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        boolean mirrored = true;

        switch(placementSettings.getMirror())
        {
            case LEFT_RIGHT:
                z = -z;
                break;
            case FRONT_BACK:
                x = -x;
                break;
            default:
                mirrored = false;
        }

        switch(placementSettings.getRotation())
        {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(z, y, -x);
            case CLOCKWISE_90:
                return new BlockPos(-z, y, x);
            case CLOCKWISE_180:
                return new BlockPos(-x, y, -z);
            default:
                return mirrored ? new BlockPos(x, y, z) : pos;
        }
    }
}
