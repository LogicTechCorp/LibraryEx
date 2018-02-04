/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.world.gen.feature;

import lex.api.config.IConfig;
import lex.api.world.gen.feature.Feature;
import lex.world.gen.Placement;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FeatureScatter extends Feature
{
    private IBlockState blockToSpawn;
    private IBlockState blockToTarget;
    private Placement placement;

    public FeatureScatter(IConfig config)
    {
        super(config);
        blockToSpawn = config.getBlock("blockToSpawn", Blocks.BARRIER.getDefaultState());
        blockToTarget = config.getBlock("blockToTarget", Blocks.BARRIER.getDefaultState());
        placement = config.getEnum("placement", Placement.class, Placement.ON_GROUND);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        if(blockToSpawn.getBlock() == Blocks.BARRIER || blockToTarget.getBlock() == Blocks.BARRIER)
        {
            return false;
        }

        for(int i = 0; i < 64; ++i)
        {
            BlockPos newPos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if(world.isAirBlock(newPos) && world.getBlockState(newPos.down()) == blockToTarget)
            {
                if(blockToSpawn instanceof BlockBush)
                {
                    if(((BlockBush) blockToSpawn).canBlockStay(world, placement.offsetPos(pos), blockToSpawn))
                    {
                        world.setBlockState(placement.offsetPos(newPos), blockToSpawn, 2);
                    }
                }
                else
                {
                    world.setBlockState(placement.offsetPos(newPos), blockToSpawn, 2);
                }
            }
        }

        return true;
    }
}
