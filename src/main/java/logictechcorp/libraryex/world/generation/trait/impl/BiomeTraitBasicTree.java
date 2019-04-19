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

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class BiomeTraitBasicTree extends BiomeTraitConfigurable
{
    private IPlantable sapling;
    private IBlockState logBlock;
    private IBlockState leafBlock;
    private int minimumGrowthHeight;
    private int maximumGrowthHeight;

    public BiomeTraitBasicTree(int generationAttempts, boolean randomizeGenerationAttempts, double generationProbability, int minimumGenerationHeight, int maximumGenerationHeight, IPlantable sapling, IBlockState logBlock, IBlockState leafBlock, int minimumGrowthHeight, int maximumGrowthHeight)
    {
        super(generationAttempts, randomizeGenerationAttempts, generationProbability, minimumGenerationHeight, maximumGenerationHeight);
        this.sapling = sapling;
        this.logBlock = logBlock;
        this.leafBlock = leafBlock;
        this.minimumGrowthHeight = minimumGrowthHeight;
        this.maximumGrowthHeight = maximumGrowthHeight;
    }

    private BiomeTraitBasicTree(Builder builder)
    {
        super(builder);
        this.sapling = builder.sapling;
        this.logBlock = builder.logBlock;
        this.leafBlock = builder.leafBlock;
        this.minimumGrowthHeight = builder.minimumGrowthHeight;
        this.maximumGrowthHeight = builder.maximumGrowthHeight;
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.sapling = (IPlantable) ConfigHelper.getBlockState(config, "sapling");
        this.logBlock = ConfigHelper.getBlockState(config, "logBlock");
        this.leafBlock = ConfigHelper.getBlockState(config, "leafBlock");
        this.minimumGrowthHeight = config.getOrElse("minimumGrowthHeight", 2);
        this.maximumGrowthHeight = config.getOrElse("maximumGrowthHeight", 32);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        ConfigHelper.setBlockState(config, "sapling", (IBlockState) this.sapling);
        ConfigHelper.setBlockState(config, "logBlock", this.logBlock);
        ConfigHelper.setBlockState(config, "leafBlock", this.leafBlock);
        config.add("minimumGrowthHeight", this.minimumGrowthHeight);
        config.add("maximumGrowthHeight", this.maximumGrowthHeight);
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

        if(pos.getY() >= 1 && pos.getY() + height + 1 <= world.getHeight())
        {
            for(int posY = pos.getY(); posY <= pos.getY() + 1 + height; ++posY)
            {
                int k = 1;

                if(posY == pos.getY())
                {
                    k = 0;
                }

                if(posY >= pos.getY() + 1 + height - 2)
                {
                    k = 2;
                }

                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

                for(int posX = pos.getX() - k; posX <= pos.getX() + k && flag; ++posX)
                {
                    for(int posZ = pos.getZ() - k; posZ <= pos.getZ() + k && flag; ++posZ)
                    {
                        if(posY >= 0 && posY < world.getHeight())
                        {
                            if(!this.isReplaceable(world, mutablePos.setPos(posX, posY, posZ)))
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
                IBlockState checkState = world.getBlockState(pos.down());

                if(checkState.getBlock().canSustainPlant(checkState, world, pos.down(), EnumFacing.UP, this.sapling) && pos.getY() < world.getHeight() - height - 1)
                {
                    checkState.getBlock().onPlantGrow(checkState, world, pos.down(), pos);

                    for(int checkPosY = pos.getY() - 3 + height; checkPosY <= pos.getY() + height; checkPosY++)
                    {
                        int i4 = checkPosY - (pos.getY() + height);
                        int j1 = 1 - i4 / 2;

                        for(int checkPosX = pos.getX() - j1; checkPosX <= pos.getX() + j1; checkPosX++)
                        {
                            int l1 = checkPosX - pos.getX();

                            for(int checkPosZ = pos.getZ() - j1; checkPosZ <= pos.getZ() + j1; checkPosZ++)
                            {
                                int j2 = checkPosZ - pos.getZ();

                                if(Math.abs(l1) != j1 || Math.abs(j2) != j1 || random.nextInt(2) != 0 && i4 != 0)
                                {
                                    BlockPos checkPos = new BlockPos(checkPosX, checkPosY, checkPosZ);
                                    checkState = world.getBlockState(checkPos);

                                    if(checkState.getBlock().isAir(checkState, world, checkPos) || checkState.getBlock().isLeaves(checkState, world, checkPos) || checkState.getMaterial() == Material.VINE)
                                    {
                                        world.setBlockState(checkPos, this.leafBlock);
                                    }
                                }
                            }
                        }
                    }

                    for(int heightOffset = 0; heightOffset < height; heightOffset++)
                    {
                        BlockPos offsetPos = pos.up(heightOffset);
                        checkState = world.getBlockState(offsetPos);

                        if(checkState.getBlock().isAir(checkState, world, offsetPos) || checkState.getBlock().isLeaves(checkState, world, offsetPos) || checkState.getMaterial() == Material.VINE)
                        {
                            world.setBlockState(pos.up(heightOffset), this.logBlock);
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

    private boolean canGrowInto(Block block)
    {
        Material material = block.getDefaultState().getMaterial();
        return material == Material.AIR || material == Material.LEAVES;
    }

    private boolean isReplaceable(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || this.canGrowInto(state.getBlock());
    }

    public static class Builder extends BiomeTrait.Builder
    {
        private IPlantable sapling;
        private IBlockState logBlock;
        private IBlockState leafBlock;
        private int minimumGrowthHeight;
        private int maximumGrowthHeight;

        public Builder()
        {
            this.sapling = (IPlantable) Blocks.SAPLING;
            this.logBlock = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
            this.leafBlock = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK);
            this.minimumGrowthHeight = 4;
            this.maximumGrowthHeight = 6;
        }

        public Builder sapling(IPlantable sapling)
        {
            this.sapling = sapling;
            return this;
        }

        public Builder logBlock(IBlockState logBlock)
        {
            this.logBlock = logBlock;
            return this;
        }

        public Builder leafBlock(IBlockState leafBlock)
        {
            this.leafBlock = leafBlock;
            return this;
        }

        public Builder minimumGrowthHeight(int minimumGrowthHeight)
        {
            this.minimumGrowthHeight = minimumGrowthHeight;
            return this;
        }

        public Builder maximumGrowthHeight(int maximumGrowthHeight)
        {
            this.maximumGrowthHeight = maximumGrowthHeight;
            return this;
        }

        @Override
        public BiomeTrait create()
        {
            return new BiomeTraitBasicTree(this);
        }
    }
}
