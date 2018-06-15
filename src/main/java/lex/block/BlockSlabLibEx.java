/*
 * LibEx
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

package lex.block;

import com.google.common.base.CaseFormat;
import lex.api.IModData;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockSlabLibEx extends BlockSlab
{
    public BlockSlabLibEx(IModData data, String name, Material material)
    {
        super(material);
        setRegistryName(!isDouble() ? data.getModId() + ":" + name : data.getModId() + ":" + name + "_double");
        setUnlocalizedName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, getRegistryName().toString()));
        setSoundType(SoundType.STONE);

        if(!isDouble())
        {
            useNeighborBrightness = true;
            setCreativeTab(data.getCreativeTab());
        }
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = getStateFromMeta(meta);
        return isDouble() ? state : (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM) : state.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return !isDouble() ? new BlockStateContainer(this, getVariantProperty(), HALF) : new BlockStateContainer(this, getVariantProperty());
    }
}
