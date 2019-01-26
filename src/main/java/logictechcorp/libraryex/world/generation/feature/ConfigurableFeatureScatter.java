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
import logictechcorp.libraryex.utility.ConfigHelper;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ConfigurableFeatureScatter extends ConfigurableFeature
{
    private IBlockState blockToSpawn;
    private IBlockState blockToTarget;
    private Placement placement;

    public ConfigurableFeatureScatter(Config config)
    {
        super(config);
        this.blockToSpawn = ConfigHelper.getBlockState(config, "blockToSpawn");
        this.blockToTarget = ConfigHelper.getBlockState(config, "blockToTarget");
        this.placement = ConfigHelper.getEnum(config, "placement", Placement.class);
    }

    public ConfigurableFeatureScatter(int generationAttempts, double generationProbability, boolean randomizeGenerationAttempts, int minGenerationHeight, int maxGenerationHeight, IBlockState blockToSpawn, IBlockState blockToTarget, Placement placement)
    {
        super(generationAttempts, generationProbability, randomizeGenerationAttempts, minGenerationHeight, maxGenerationHeight);
        this.blockToSpawn = blockToSpawn;
        this.blockToTarget = blockToTarget;
        this.placement = placement;
    }

    @Override
    public Config serialize()
    {
        Config config = super.serialize();
        config.add("placement", this.placement == null ? null : this.placement.toString().toLowerCase());
        ConfigHelper.setBlockState(config, "blockToSpawn", this.blockToSpawn);
        ConfigHelper.setBlockState(config, "blockToTarget", this.blockToTarget);
        return config;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos pos)
    {
        if(this.blockToSpawn == null || this.blockToTarget == null || this.placement == null)
        {
            return false;
        }

        for(int i = 0; i < 64; i++)
        {
            BlockPos newPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if(world.isAirBlock(newPos) && world.getBlockState(newPos.down()) == this.blockToTarget)
            {
                if(this.blockToSpawn instanceof BlockBush)
                {
                    if(((BlockBush) this.blockToSpawn).canBlockStay(world, this.placement.offsetPos(pos), this.blockToSpawn))
                    {
                        world.setBlockState(this.placement.offsetPos(newPos), this.blockToSpawn, 2);
                    }
                }
                else
                {
                    world.setBlockState(this.placement.offsetPos(newPos), this.blockToSpawn, 2);
                }
            }
        }

        return true;
    }

    public enum Placement
    {
        ON_GROUND(null),
        IN_GROUND(EnumFacing.DOWN);

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
}
