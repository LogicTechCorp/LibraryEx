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
import java.util.function.Consumer;

public class BiomeTraitDenseTree extends BiomeTraitAbstractTree
{
    protected BiomeTraitDenseTree(Builder builder)
    {
        super(builder);
        this.logBlock = builder.logBlock;
        this.leafBlock = builder.leafBlock;
        this.blockToTarget = builder.blockToTarget;
        this.minimumGrowthHeight = builder.minimumGrowthHeight;
        this.maximumGrowthHeight = builder.maximumGrowthHeight;
    }

    public static BiomeTraitDenseTree create(Consumer<Builder> consumer)
    {
        Builder builder = new Builder();
        consumer.accept(builder);
        return builder.create();
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.logBlock == null || this.leafBlock == null)
        {
            return false;
        }

        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int height = RandomHelper.getNumberInRange(this.minimumGrowthHeight, this.maximumGrowthHeight, random);

        if(posY >= 1 && posY + height + 1 < 256)
        {
            IBlockState state = world.getBlockState(pos.down());
            BlockPos downPos = pos.down();

            if(!(this.blockToTarget == state && pos.getY() < world.getHeight() - height - 1))
            {
                return false;
            }
            else if(!this.placeTreeOfHeight(world, pos, height))
            {
                return false;
            }
            else
            {
                this.onPlantGrow(world, downPos, pos);
                this.onPlantGrow(world, downPos.east(), pos);
                this.onPlantGrow(world, downPos.south(), pos);
                this.onPlantGrow(world, downPos.south().east(), pos);

                EnumFacing facing = EnumFacing.Plane.HORIZONTAL.random(random);
                int adjustedHeight = height - random.nextInt(4);
                int bonusHeight = 2 - random.nextInt(3);
                int posXOffset = posX;
                int posZOffset = posZ;
                int posYOffset = posY + height - 1;

                for(int localHeight = 0; localHeight < height; localHeight++)
                {
                    if(localHeight >= adjustedHeight && bonusHeight > 0)
                    {
                        posXOffset += facing.getXOffset();
                        posZOffset += facing.getZOffset();
                        bonusHeight--;
                    }

                    int posYLocal = posY + localHeight;
                    BlockPos offsetPos = new BlockPos(posXOffset, posYLocal, posZOffset);
                    state = world.getBlockState(offsetPos);

                    if(state.getBlock().isAir(state, world, offsetPos) || state.getBlock().isLeaves(state, world, offsetPos))
                    {
                        this.placeLogAt(world, offsetPos);
                        this.placeLogAt(world, offsetPos.east());
                        this.placeLogAt(world, offsetPos.south());
                        this.placeLogAt(world, offsetPos.east().south());
                    }
                }

                for(int x = -2; x <= 0; x++)
                {
                    for(int z = -2; z <= 0; z++)
                    {
                        int y = -1;
                        this.placeLeafAt(world, posXOffset + x, posYOffset + y, posZOffset + z);
                        this.placeLeafAt(world, 1 + posXOffset - x, posYOffset + y, posZOffset + z);
                        this.placeLeafAt(world, posXOffset + x, posYOffset + y, 1 + posZOffset - z);
                        this.placeLeafAt(world, 1 + posXOffset - x, posYOffset + y, 1 + posZOffset - z);

                        if((x > -2 || z > -1) && (x != -1 || z != -2))
                        {
                            y = 1;
                            this.placeLeafAt(world, posXOffset + x, posYOffset + y, posZOffset + z);
                            this.placeLeafAt(world, 1 + posXOffset - x, posYOffset + y, posZOffset + z);
                            this.placeLeafAt(world, posXOffset + x, posYOffset + y, 1 + posZOffset - z);
                            this.placeLeafAt(world, 1 + posXOffset - x, posYOffset + y, 1 + posZOffset - z);
                        }
                    }
                }

                if(random.nextBoolean())
                {
                    this.placeLeafAt(world, posXOffset, posYOffset + 2, posZOffset);
                    this.placeLeafAt(world, posXOffset + 1, posYOffset + 2, posZOffset);
                    this.placeLeafAt(world, posXOffset + 1, posYOffset + 2, posZOffset + 1);
                    this.placeLeafAt(world, posXOffset, posYOffset + 2, posZOffset + 1);
                }

                for(int x = -3; x <= 4; x++)
                {
                    for(int z = -3; z <= 4; z++)
                    {
                        if((x != -3 || z != -3) && (x != -3 || z != 4) && (x != 4 || z != -3) && (x != 4 || z != 4) && (Math.abs(x) < 3 || Math.abs(z) < 3))
                        {
                            this.placeLeafAt(world, posXOffset + x, posYOffset, posZOffset + z);
                        }
                    }
                }

                for(int posXLog = -1; posXLog <= 2; posXLog++)
                {
                    for(int posZLog = -1; posZLog <= 2; posZLog++)
                    {
                        if((posXLog < 0 || posXLog > 1 || posZLog < 0 || posZLog > 1) && random.nextInt(3) <= 0)
                        {
                            for(int y = 0; y < random.nextInt(3) + 2; y++)
                            {
                                this.placeLogAt(world, new BlockPos(posX + posXLog, posYOffset - y - 1, posZ + posZLog));
                            }

                            for(int posXLeaf = -1; posXLeaf <= 1; posXLeaf++)
                            {
                                for(int posZLeaf = -1; posZLeaf <= 1; posZLeaf++)
                                {
                                    this.placeLeafAt(world, posXOffset + posXLog + posXLeaf, posYOffset, posZOffset + posZLog + posZLeaf);
                                }
                            }

                            for(int posXLeaf = -2; posXLeaf <= 2; posXLeaf++)
                            {
                                for(int posZLeaf = -2; posZLeaf <= 2; posZLeaf++)
                                {
                                    if(Math.abs(posXLeaf) != 2 || Math.abs(posZLeaf) != 2)
                                    {
                                        this.placeLeafAt(world, posXOffset + posXLog + posXLeaf, posYOffset - 1, posZOffset + posZLog + posZLeaf);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    protected void onPlantGrow(World world, BlockPos pos, BlockPos source)
    {
        IBlockState state = world.getBlockState(pos);
        state.getBlock().onPlantGrow(state, world, pos, source);
    }

    private void placeLeafAt(World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);

        if(state.getBlock().isAir(state, world, pos))
        {
            world.setBlockState(pos, this.leafBlock);
        }
    }

    private boolean placeTreeOfHeight(World world, BlockPos pos, int height)
    {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for(int y = 0; y <= height + 1; y++)
        {
            int localHeight = 1;

            if(y == 0)
            {
                localHeight = 0;
            }

            if(y >= height - 1)
            {
                localHeight = 2;
            }

            for(int x = -localHeight; x <= localHeight; x++)
            {
                for(int z = -localHeight; z <= localHeight; z++)
                {
                    if(!this.isReplaceable(world, mutableBlockPos.setPos(posX + x, posY + y, posZ + z)))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static class Builder extends BiomeTraitAbstractTree.Builder
    {
        @Override
        public BiomeTraitDenseTree create()
        {
            return new BiomeTraitDenseTree(this);
        }
    }
}
