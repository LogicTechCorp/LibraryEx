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

package logictechcorp.libraryex.world.generation.trait.impl;

import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BiomeTraitSparseTree extends BiomeTraitAbstractTree
{
    public BiomeTraitSparseTree(int generationAttempts, boolean randomizeGenerationAttempts, double generationProbability, int minimumGenerationHeight, int maximumGenerationHeight, IBlockState logBlock, IBlockState leafBlock, IBlockState blockToTarget, int minimumGrowthHeight, int maximumGrowthHeight)
    {
        super(generationAttempts, randomizeGenerationAttempts, generationProbability, minimumGenerationHeight, maximumGenerationHeight, logBlock, leafBlock, blockToTarget, minimumGrowthHeight, maximumGrowthHeight);
    }

    private BiomeTraitSparseTree(Builder builder)
    {
        super(builder);
        this.logBlock = builder.logBlock;
        this.leafBlock = builder.leafBlock;
        this.blockToTarget = builder.blockToTarget;
        this.minimumGrowthHeight = builder.minimumGrowthHeight;
        this.maximumGrowthHeight = builder.maximumGrowthHeight;
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.logBlock == null || this.leafBlock == null)
        {
            return false;
        }

        int height = RandomHelper.getNumberInRange(this.minimumGrowthHeight, this.maximumGrowthHeight, random);
        boolean flag = true;

        if(pos.getY() >= 1 && pos.getY() + height + 1 <= 256)
        {
            for(int posY = pos.getY(); posY <= pos.getY() + 1 + height; posY++)
            {
                int adjustedHeight = 1;

                if(posY == pos.getY())
                {
                    adjustedHeight = 0;
                }

                if(posY >= pos.getY() + 1 + height - 2)
                {
                    adjustedHeight = 2;
                }

                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                for(int posX = pos.getX() - adjustedHeight; posX <= pos.getX() + adjustedHeight && flag; posX++)
                {
                    for(int posZ = pos.getZ() - adjustedHeight; posZ <= pos.getZ() + adjustedHeight && flag; posZ++)
                    {
                        if(posY >= 0 && posY < 256)
                        {
                            if(!this.isReplaceable(world, mutableBlockPos.setPos(posX, posY, posZ)))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if(!flag)
            {
                return false;
            }
            else
            {
                BlockPos downPos = pos.down();
                IBlockState state = world.getBlockState(downPos);

                if(this.blockToTarget == state && pos.getY() < world.getHeight() - height - 1)
                {
                    state.getBlock().onPlantGrow(state, world, downPos, pos);
                    EnumFacing facing = EnumFacing.Plane.HORIZONTAL.random(random);

                    int adjustedHeight = height - random.nextInt(4) - 1;
                    int bonusHeight = 3 - random.nextInt(3);
                    int posXOffset = pos.getX();
                    int posZOffset = pos.getZ();
                    int posYOffset = 0;

                    for(int localHeight = 0; localHeight < height; localHeight++)
                    {
                        int adjustedLocalHeight = pos.getY() + localHeight;

                        if(localHeight >= adjustedHeight && bonusHeight > 0)
                        {
                            posXOffset += facing.getXOffset();
                            posZOffset += facing.getZOffset();
                            bonusHeight--;
                        }

                        BlockPos offsetPos = new BlockPos(posXOffset, adjustedLocalHeight, posZOffset);
                        state = world.getBlockState(offsetPos);

                        if(state.getBlock().isAir(state, world, offsetPos) || state.getBlock().isLeaves(state, world, offsetPos))
                        {
                            this.placeLogAt(world, offsetPos);
                            posYOffset = adjustedLocalHeight;
                        }
                    }

                    BlockPos posLeaf = new BlockPos(posXOffset, posYOffset, posZOffset);

                    for(int posXLeaf = -3; posXLeaf <= 3; posXLeaf++)
                    {
                        for(int posZLeaf = -3; posZLeaf <= 3; posZLeaf++)
                        {
                            if(Math.abs(posXLeaf) != 3 || Math.abs(posZLeaf) != 3)
                            {
                                this.placeLeafAt(world, posLeaf.add(posXLeaf, 0, posZLeaf));
                            }
                        }
                    }

                    posLeaf = posLeaf.up();

                    for(int posXLeaf = -1; posXLeaf <= 1; posXLeaf++)
                    {
                        for(int posZLeaf = -1; posZLeaf <= 1; posZLeaf++)
                        {
                            this.placeLeafAt(world, posLeaf.add(posXLeaf, 0, posZLeaf));
                        }
                    }

                    this.placeLeafAt(world, posLeaf.east(2));
                    this.placeLeafAt(world, posLeaf.west(2));
                    this.placeLeafAt(world, posLeaf.south(2));
                    this.placeLeafAt(world, posLeaf.north(2));
                    posXOffset = pos.getX();
                    posZOffset = pos.getZ();
                    EnumFacing horizontalFacing = EnumFacing.Plane.HORIZONTAL.random(random);

                    if(horizontalFacing != facing)
                    {
                        int randomAdjustedHeight = adjustedHeight - random.nextInt(2) - 1;
                        int randomHeight = 1 + random.nextInt(3);
                        posYOffset = 0;

                        for(int localRandomHeight = randomAdjustedHeight; localRandomHeight < height && randomHeight > 0; --randomHeight)
                        {
                            if(localRandomHeight >= 1)
                            {
                                int posYLog = pos.getY() + localRandomHeight;
                                posXOffset += horizontalFacing.getXOffset();
                                posZOffset += horizontalFacing.getZOffset();
                                BlockPos posLog = new BlockPos(posXOffset, posYLog, posZOffset);
                                state = world.getBlockState(posLog);

                                if(state.getBlock().isAir(state, world, posLog) || state.getBlock().isLeaves(state, world, posLog))
                                {
                                    this.placeLogAt(world, posLog);
                                    posYOffset = posYLog;
                                }
                            }

                            localRandomHeight++;
                        }

                        if(posYOffset > 0)
                        {
                            BlockPos posOffset = new BlockPos(posXOffset, posYOffset, posZOffset);

                            for(int posXLeaf = -2; posXLeaf <= 2; posXLeaf++)
                            {
                                for(int posZLeaf = -2; posZLeaf <= 2; posZLeaf++)
                                {
                                    if(Math.abs(posXLeaf) != 2 || Math.abs(posZLeaf) != 2)
                                    {
                                        this.placeLeafAt(world, posOffset.add(posXLeaf, 0, posZLeaf));
                                    }
                                }
                            }

                            posOffset = posOffset.up();

                            for(int posXLeaf = -1; posXLeaf <= 1; posXLeaf++)
                            {
                                for(int posZLeaf = -1; posZLeaf <= 1; posZLeaf++)
                                {
                                    this.placeLeafAt(world, posOffset.add(posXLeaf, 0, posZLeaf));
                                }
                            }
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    private void placeLeafAt(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);

        if(state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos))
        {
            world.setBlockState(pos, this.leafBlock);
        }
    }

    public static class Builder extends BiomeTraitAbstractTree.Builder
    {
        @Override
        public BiomeTrait create()
        {
            return new BiomeTraitSparseTree(this);
        }
    }

}
