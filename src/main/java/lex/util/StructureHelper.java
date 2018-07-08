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

package lex.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class StructureHelper
{
    public static BlockPos getGroundedPos(World world, BlockPos pos, BlockPos structureSize, float clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() > 0)
            {
                float sizeX = structureSize.getX();
                float sizeY = structureSize.getY();
                float sizeZ = structureSize.getZ();

                int surfaceBlocks = 0;

                for(int x = 0; x <= MathHelper.abs(sizeX); x++)
                {
                    for(int z = 0; z <= MathHelper.abs(sizeZ); z++)
                    {
                        BlockPos newPos = pos.add(x, 0, z);

                        if(!world.getBlockState(newPos).getMaterial().isReplaceable() && world.getBlockState(newPos.up()).getMaterial().isReplaceable())
                        {
                            surfaceBlocks++;
                        }
                    }
                }

                int replaceableBlocks = 0;

                if(surfaceBlocks >= MathHelper.abs(sizeX * sizeZ) * clearancePercentage)
                {
                    for(int y = 1; y < sizeY; y++)
                    {
                        for(int x = 0; x <= MathHelper.abs(sizeX); x++)
                        {
                            for(int z = 0; z <= MathHelper.abs(sizeZ); z++)
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

                if(replaceableBlocks > MathHelper.abs(sizeX * sizeY * sizeZ) * 0.875F)
                {
                    return pos;
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getFloatingPos(World world, BlockPos pos, BlockPos structureSize, float clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() > 32)
            {
                float sizeX = structureSize.getX();
                float sizeZ = structureSize.getZ();
                float sizeY = structureSize.getY();

                int replaceableBlocks = 0;

                for(int x = 0; x <= MathHelper.abs(sizeX); x++)
                {
                    for(int z = 0; z <= MathHelper.abs(sizeZ); z++)
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

                if(replaceableBlocks >= MathHelper.abs(sizeX * sizeY * sizeZ) * clearancePercentage)
                {
                    return pos;
                }

                pos = pos.down();
            }
        }

        return null;
    }

    public static BlockPos getHangingPos(World world, BlockPos pos, BlockPos structureSize, float clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() < 128)
            {
                float sizeX = structureSize.getX();
                float sizeZ = structureSize.getZ();
                float sizeY = structureSize.getY();

                int ceilingBlocks = 0;
                int replaceableBlocks = 0;

                for(int x = 0; x <= MathHelper.abs(sizeX); x++)
                {
                    for(int z = 0; z <= MathHelper.abs(sizeZ); z++)
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

                if(ceilingBlocks + replaceableBlocks >= MathHelper.abs(sizeX * sizeY * sizeZ) * clearancePercentage)
                {
                    return pos.add(0, -sizeY, 0);
                }

                pos = pos.up();
            }
        }

        return null;
    }

    public static BlockPos getBuriedPos(World world, BlockPos pos, BlockPos structureSize, float clearancePercentage)
    {
        if(world.isAreaLoaded(pos, pos.add(structureSize)))
        {
            while(pos.getY() > 32)
            {
                float sizeX = structureSize.getX();
                float sizeZ = structureSize.getZ();
                float sizeY = structureSize.getY();

                int nonReplaceableBlocks = 0;

                for(int x = 0; x <= MathHelper.abs(sizeX); x++)
                {
                    for(int z = 0; z <= MathHelper.abs(sizeZ); z++)
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

                if(nonReplaceableBlocks >= MathHelper.abs(sizeX * sizeY * sizeZ) * clearancePercentage)
                {
                    return pos;
                }

                pos = pos.down();
            }
        }

        return null;
    }
}
