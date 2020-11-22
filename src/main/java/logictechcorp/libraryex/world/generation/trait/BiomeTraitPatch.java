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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Consumer;

public class BiomeTraitPatch extends BiomeTrait
{
    protected IBlockState blockToSpawn;
    protected IBlockState blockToTarget;
    protected int patchWidth;

    protected BiomeTraitPatch(Builder builder)
    {
        super(builder);
        this.blockToSpawn = builder.blockToSpawn;
        this.blockToTarget = builder.blockToTarget;
        this.patchWidth = builder.patchWidth;
    }

    public static BiomeTraitPatch create(Consumer<Builder> consumer)
    {
        Builder builder = new Builder();
        consumer.accept(builder);
        return builder.create();
    }

    @Override
    public void readFromConfig(Config config)
    {
        super.readFromConfig(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToTarget = ConfigHelper.getBlockState(config, "blockToTarget");
        this.patchWidth = config.get("patchWidth");
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
        config.add("patchWidth", this.patchWidth);
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.blockToSpawn == null || this.blockToTarget == null)
        {
            return false;
        }

        while(world.isAirBlock(pos) && pos.getY() > 2)
        {
            pos = pos.down();
        }

        if(world.getBlockState(pos) != this.blockToTarget)
        {
            return false;
        }
        else
        {
            int width = random.nextInt(this.patchWidth - 2) + 2;

            for(int posX = pos.getX() - width; posX <= pos.getX() + width; posX++)
            {
                for(int posZ = pos.getZ() - width; posZ <= pos.getZ() + width; posZ++)
                {
                    int areaX = posX - pos.getX();
                    int areaZ = posZ - pos.getZ();

                    if(areaX * areaX + areaZ * areaZ <= width * width)
                    {
                        for(int posY = pos.getY() - 1; posY <= pos.getY() + 1; posY++)
                        {
                            BlockPos blockPos = new BlockPos(posX, posY, posZ);

                            if(world.getBlockState(blockPos) == this.blockToTarget)
                            {
                                world.setBlockState(blockPos, this.blockToSpawn, 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    public static class Builder extends BiomeTrait.Builder<BiomeTraitPatch>
    {
        private IBlockState blockToSpawn;
        private IBlockState blockToTarget;
        private int patchWidth;

        public Builder()
        {
            this.blockToSpawn = Blocks.PACKED_ICE.getDefaultState();
            this.blockToTarget = Blocks.ICE.getDefaultState();
            this.patchWidth = 4;
        }

        public Builder blockToSpawn(IBlockState blockToSpawn)
        {
            this.blockToSpawn = blockToSpawn;
            return this;
        }

        public Builder blockToTarget(IBlockState blockToTarget)
        {
            this.blockToTarget = blockToTarget;
            return this;
        }

        public Builder patchWidth(int patchWidth)
        {
            this.patchWidth = patchWidth;
            return this;
        }

        @Override
        public BiomeTraitPatch create()
        {
            return new BiomeTraitPatch(this);
        }
    }
}
