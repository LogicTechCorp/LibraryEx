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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureHelper
{
    public static BlockPos getGroundPos(World world, BlockPos pos, BlockPos structureSize, double clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() > 0)
            {
                float sizeX = structureSize.getX() + 2;
                float sizeY = structureSize.getY() + 1;
                float sizeZ = structureSize.getZ() + 2;

                int groundBlocks = 0;

                for(int x = 0; x <= sizeX; x++)
                {
                    for(int z = 0; z <= sizeZ; z++)
                    {
                        BlockPos newPos = pos.add(x, 0, z);

                        if(!world.getBlockState(newPos).getMaterial().isReplaceable() && world.getBlockState(newPos.up()).getMaterial().isReplaceable())
                        {
                            groundBlocks++;
                        }
                    }
                }

                int replaceableBlocks = 0;

                if(groundBlocks >= (sizeX * sizeZ * clearancePercentage))
                {
                    for(int y = 1; y < sizeY; y++)
                    {
                        for(int x = 0; x <= sizeX; x++)
                        {
                            for(int z = 0; z <= sizeZ; z++)
                            {
                                BlockPos newPos = pos.add(x, y, z);

                                if(world.getBlockState(newPos).getMaterial().isReplaceable())
                                {
                                    replaceableBlocks++;
                                }
                            }
                        }
                    }
                }

                if(replaceableBlocks > (sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos;
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getAirPos(World world, BlockPos pos, BlockPos structureSize, double clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() > 32)
            {
                float sizeX = structureSize.getX() + 2;
                float sizeZ = structureSize.getZ() + 2;
                float sizeY = structureSize.getY() + 2;

                int replaceableBlocks = 0;

                for(int x = 0; x <= sizeX; x++)
                {
                    for(int z = 0; z <= sizeZ; z++)
                    {
                        for(int y = 0; y <= sizeY; y++)
                        {
                            BlockPos newPos = pos.add(x, y, z);

                            if(world.getBlockState(newPos).getMaterial().isReplaceable())
                            {
                                replaceableBlocks++;
                            }
                        }
                    }
                }

                if(replaceableBlocks >= (sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos;
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getBuriedPos(World world, BlockPos pos, BlockPos structureSize, double clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() > 32)
            {
                float sizeX = structureSize.getX();
                float sizeZ = structureSize.getZ();
                float sizeY = structureSize.getY();

                int nonReplaceableBlocks = 0;

                for(int x = 0; x <= sizeX; x++)
                {
                    for(int z = 0; z <= sizeZ; z++)
                    {
                        for(int y = 0; y <= sizeY; y++)
                        {
                            BlockPos newPos = pos.add(x, y, z);

                            if(!world.getBlockState(newPos).getMaterial().isReplaceable())
                            {
                                nonReplaceableBlocks++;
                            }
                        }
                    }
                }

                if(nonReplaceableBlocks >= (sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos;
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getCeilingPos(World world, BlockPos pos, BlockPos structureSize, double clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() < 128)
            {
                float sizeX = structureSize.getX() + 2;
                float sizeY = structureSize.getY() + 1;
                float sizeZ = structureSize.getZ() + 2;

                int ceilingBlocks = 0;
                int replaceableBlocks = 0;

                for(int x = 0; x <= sizeX; x++)
                {
                    for(int z = 0; z <= sizeZ; z++)
                    {
                        for(int y = 0; y <= sizeY; y++)
                        {
                            BlockPos newPos = pos.add(x, -y, z);

                            if(y == 0)
                            {
                                if(world.getBlockState(newPos).isSideSolid(world, newPos, EnumFacing.DOWN))
                                {
                                    ceilingBlocks++;
                                }
                            }
                            else
                            {
                                if(world.getBlockState(newPos).getBlock().isReplaceable(world, newPos))
                                {
                                    replaceableBlocks++;
                                }
                            }
                        }
                    }
                }

                if(ceilingBlocks + replaceableBlocks >= (sizeX * sizeY * sizeZ * clearancePercentage))
                {
                    return pos.add(0, -sizeY, 0);
                }

                pos = pos.up();
            }
        }

        return null;
    }
}
