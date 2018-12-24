/*
 * LibraryEx
 * Copyright (c) 2017-2018 by MineEx
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

import logictechcorp.libraryex.block.builder.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class BlockMod extends Block
{
    private Item drop;

    public BlockMod(ResourceLocation registryName, BlockBuilder builder)
    {
        super(builder.getMaterial(), builder.getMapColor());
        this.setRegistryName(registryName);
        this.setSoundType(builder.getSoundType());
        this.setCreativeTab(builder.getCreativeTab());
        this.setLightLevel(builder.getLightLevel());
        this.setHarvestLevel(builder.getHarvestTool(), builder.getHarvestLevel());
        this.setHardness(builder.getHardness());
        this.setResistance(builder.getResistance());
        this.setTickRandomly(builder.needsRandomTick());
        this.setTranslationKey(registryName.toString());
        this.drop = builder.getDrop();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return this.drop == null ? super.getItemDropped(state, rand, fortune) : this.drop;
    }
}
