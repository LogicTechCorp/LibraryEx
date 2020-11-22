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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Consumer;

public class BiomeTraitScatter extends BiomeTrait
{
    protected IBlockState blockToSpawn;
    protected IBlockState blockToTarget;
    protected Placement placement;

    protected BiomeTraitScatter(Builder builder)
    {
        super(builder);
        this.blockToSpawn = builder.blockToSpawn;
        this.blockToTarget = builder.blockToTarget;
        this.placement = builder.placement;
    }

    public static BiomeTraitScatter create(Consumer<Builder> consumer)
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
        this.placement = config.getEnumOrElse("placement", Placement.ON_GROUND);
    }

    @Override
    public void writeToConfig(Config config)
    {
        super.writeToConfig(config);
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
        config.add("placement", this.placement == null ? null : this.placement.toString().toLowerCase());
    }

    @Override
    public boolean generate(World world, BlockPos pos, Random random)
    {
        if(this.blockToSpawn == null || this.blockToTarget == null || this.placement == null)
        {
            return false;
        }

        for(int i = 0; i < 64; i++)
        {
            BlockPos newPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if(world.isAirBlock(newPos))
            {
                if((this.placement == Placement.ON_GROUND || this.placement == Placement.IN_GROUND) && world.getBlockState(newPos.down()) == this.blockToTarget)
                {
                    BlockPos offsetPos = this.placement.offsetPos(newPos);

                    if(this.blockToSpawn.getBlock().canPlaceBlockAt(world, offsetPos))
                    {
                        world.setBlockState(offsetPos, this.blockToSpawn, 3);
                    }
                }
                else if((this.placement == Placement.ON_ROOF || this.placement == Placement.IN_ROOF) && world.getBlockState(newPos.up()) == this.blockToTarget)
                {
                    BlockPos offsetPos = this.placement.offsetPos(newPos);

                    if(this.blockToSpawn.getBlock().canPlaceBlockAt(world, offsetPos))
                    {
                        world.setBlockState(offsetPos, this.blockToSpawn, 3);
                    }
                }
            }
        }

        return true;
    }

    public enum Placement
    {
        ON_GROUND(null),
        IN_GROUND(EnumFacing.DOWN),
        ON_ROOF(null),
        IN_ROOF(EnumFacing.UP);

        EnumFacing offset;

        Placement(EnumFacing offsetIn)
        {
            this.offset = offsetIn;
        }

        public BlockPos offsetPos(BlockPos pos)
        {
            if(this.offset != null)
            {
                return pos.offset(this.offset);
            }
            else
            {
                return pos;
            }
        }
    }

    public static class Builder extends BiomeTrait.Builder<BiomeTraitScatter>
    {
        private IBlockState blockToSpawn;
        private IBlockState blockToTarget;
        private Placement placement;

        public Builder()
        {
            this.blockToSpawn = Blocks.TALLGRASS.getDefaultState();
            this.blockToTarget = Blocks.GRASS.getDefaultState();
            this.placement = Placement.ON_GROUND;
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

        public Builder placement(Placement placement)
        {
            this.placement = placement;
            return this;
        }

        @Override
        public BiomeTraitScatter create()
        {
            return new BiomeTraitScatter(this);
        }
    }
}
