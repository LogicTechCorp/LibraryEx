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

package logictechcorp.libraryex.world.generation.trait;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.utility.ConfigHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BiomeTraitAbstractTree extends BiomeTrait
{
    protected IBlockState logBlock;
    protected IBlockState leafBlock;
    protected IBlockState blockToTarget;
    protected int minimumGrowthHeight;
    protected int maximumGrowthHeight;

    protected BiomeTraitAbstractTree(Builder builder)
    {
        super(builder);
        this.logBlock = builder.logBlock;
        this.leafBlock = builder.leafBlock;
        this.blockToTarget = builder.blockToTarget;
        this.minimumGrowthHeight = builder.minimumGrowthHeight;
        this.maximumGrowthHeight = builder.maximumGrowthHeight;
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.logBlock = ConfigHelper.getBlockState(config, "logBlock");
        this.leafBlock = ConfigHelper.getBlockState(config, "leafBlock");
        this.blockToTarget = ConfigHelper.getBlockState(config, "blockToTarget");
        this.minimumGrowthHeight = config.getOrElse("minimumGrowthHeight", 2);
        this.maximumGrowthHeight = config.getOrElse("maximumGrowthHeight", 32);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        ConfigHelper.setBlockState(config, "logBlock", this.logBlock);
        ConfigHelper.setBlockState(config, "leafBlock", this.leafBlock);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
        config.add("minimumGrowthHeight", this.minimumGrowthHeight);
        config.add("maximumGrowthHeight", this.maximumGrowthHeight);
    }

    protected void placeLogAt(World world, BlockPos pos)
    {
        if(this.canGrowInto(world.getBlockState(pos).getBlock()))
        {
            world.setBlockState(pos, this.logBlock);
        }
    }

    protected boolean canGrowInto(Block block)
    {
        Material material = block.getDefaultState().getMaterial();
        return material == Material.AIR || material == Material.LEAVES;
    }

    protected boolean isReplaceable(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || this.canGrowInto(state.getBlock());
    }

    public abstract static class Builder extends BiomeTrait.Builder
    {
        protected IBlockState logBlock;
        protected IBlockState leafBlock;
        protected IBlockState blockToTarget;
        protected int minimumGrowthHeight;
        protected int maximumGrowthHeight;

        public Builder()
        {
            this.logBlock = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
            this.leafBlock = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK);
            this.blockToTarget = Blocks.GRASS.getDefaultState();
            this.minimumGrowthHeight = 4;
            this.maximumGrowthHeight = 6;
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

        public Builder blockToTarget(IBlockState blockToTarget)
        {
            this.blockToTarget = blockToTarget;
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
        public abstract BiomeTrait create();
    }
}
