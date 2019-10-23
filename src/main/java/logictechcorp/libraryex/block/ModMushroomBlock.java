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

package logictechcorp.libraryex.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.BigMushroomFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.PlantType;

import java.util.Random;
import java.util.function.Supplier;

public class ModMushroomBlock extends MushroomBlock
{
    private final Supplier<Feature<BigMushroomFeatureConfig>> featureSupplier;

    public ModMushroomBlock(Supplier<Feature<BigMushroomFeatureConfig>> featureSupplier, Properties properties)
    {
        super(properties);
        this.featureSupplier = featureSupplier;
    }

    @Override
    public boolean generateBigMushroom(IWorld world, BlockPos pos, BlockState state, Random rand)
    {
        world.removeBlock(pos, false);

        if(this.featureSupplier.get().place(world, world.getChunkProvider().getChunkGenerator(), rand, pos, new BigMushroomFeatureConfig(true)))
        {
            return true;
        }
        else
        {
            world.setBlockState(pos, state, 3);
            return false;
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        BlockPos posDown = pos.down();
        BlockState stateDown = world.getBlockState(posDown);
        Block blockDown = stateDown.getBlock();

        if(blockDown != Blocks.MYCELIUM && blockDown != Blocks.PODZOL)
        {
            return stateDown.canSustainPlant(world, posDown, Direction.UP, this);
        }
        else
        {
            return true;
        }
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos)
    {
        return PlantType.Cave;
    }
}
