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
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockModFenceGate extends BlockFenceGate
{
    public BlockModFenceGate(ResourceLocation registryName, BlockBuilder builder)
    {
        super(BlockPlanks.EnumType.OAK);
        this.setRegistryName(registryName);
        this.setSoundType(builder.getSoundType());
        this.setCreativeTab(builder.getCreativeTab());
        this.setLightLevel(builder.getLightLevel());
        this.setHarvestLevel(builder.getHarvestTool(), builder.getHarvestLevel());
        this.setHardness(builder.getHardness());
        this.setResistance(builder.getResistance());
        this.setTickRandomly(builder.needsRandomTick());
        this.setTranslationKey(registryName.toString());
        this.useNeighborBrightness = true;
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, builder.getMaterial(), "field_149764_J", "material");
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, builder.getMaterial().getMaterialMapColor(), "field_181083_K", "blockMapColor");
    }
}
