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

package logictechcorp.libraryex.world.generation.feature;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.utility.BlockHelper;
import logictechcorp.libraryex.utility.ConfigHelper;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FeatureOakTree extends FeatureMod
{
    private IBlockState logBlock;
    private IBlockState leafBlock;
    private int minGrowthHeight;
    private int maxGrowthHeight;

    public FeatureOakTree(Config config)
    {
        super(config);
        this.logBlock = ConfigHelper.getBlockState(config, "logBlock");
        this.leafBlock = ConfigHelper.getBlockState(config, "leafBlock");
        this.minGrowthHeight = config.getOrElse("minGrowthHeight", 2);
        this.maxGrowthHeight = config.getOrElse("maxGrowthHeight", 32);
    }

    public FeatureOakTree(int generationAttempts, float generationProbability, boolean randomizeGenerationAttempts, int minGenerationHeight, int maxGenerationHeight, IBlockState logBlock, IBlockState leafBlock, int minGrowthHeight, int maxGrowthHeight)
    {
        super(generationAttempts, generationProbability, randomizeGenerationAttempts, minGenerationHeight, maxGenerationHeight);
        this.logBlock = logBlock;
        this.leafBlock = leafBlock;
        this.minGrowthHeight = minGrowthHeight;
        this.maxGrowthHeight = maxGrowthHeight;
    }

    @Override
    public Config serialize()
    {
        Config config = super.serialize();
        config.add("maxGrowthHeight", this.maxGrowthHeight);
        config.add("minGrowthHeight", this.minGrowthHeight);
        ConfigHelper.setBlockState(config, "leafBlock", this.leafBlock);
        ConfigHelper.setBlockState(config, "logBlock", this.logBlock);
        return config;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos pos)
    {
        if(this.logBlock == null || this.leafBlock == null)
        {
            return false;
        }

        int height = RandomHelper.getNumberInRange(this.minGrowthHeight, this.maxGrowthHeight, random);
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

                if(BlockHelper.isOreDict("endstone", checkState.getBlock()) && pos.getY() < world.getHeight() - height - 1)
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
                                        this.setBlockAndNotifyAdequately(world, checkPos, this.leafBlock);
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
                            this.setBlockAndNotifyAdequately(world, pos.up(heightOffset), this.logBlock);
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

    protected boolean canGrowInto(Block blockType)
    {
        Material material = blockType.getDefaultState().getMaterial();
        return material == Material.AIR || material == Material.LEAVES;
    }

    public boolean isReplaceable(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || this.canGrowInto(state.getBlock());
    }

}
