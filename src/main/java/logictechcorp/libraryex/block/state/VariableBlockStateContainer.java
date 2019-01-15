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

package logictechcorp.libraryex.block.state;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class VariableBlockStateContainer extends BlockStateContainer
{
    private BlockStateContainer container;

    public VariableBlockStateContainer(BlockStateContainer container, Block block, IProperty... properties)
    {
        super(block, properties);
        this.container = container;
    }

    @Override
    public IBlockState getBaseState()
    {
        return this.container == null ? this.getValidStates().get(0) : this.container.getValidStates().get(0);
    }

    public void destroyContainer()
    {
        this.container = null;
    }
}
