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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

public class ExperienceDroppingBlock extends Block
{
    private final int minimumExperience;
    private final int maximumExperience;

    public ExperienceDroppingBlock(int minimumExperience, int maximumExperience, Properties properties)
    {
        super(properties);
        this.minimumExperience = minimumExperience;
        this.maximumExperience = maximumExperience;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortuneLevel, int silkTouchLevel)
    {
        return silkTouchLevel == 0 ? MathHelper.nextInt(this.RANDOM, this.minimumExperience, this.maximumExperience) : 0;
    }
}
