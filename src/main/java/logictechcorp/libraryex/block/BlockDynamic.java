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

import logictechcorp.libraryex.block.property.BlockProperties;
import logictechcorp.libraryex.block.property.PropertyDynamicTexture;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class BlockDynamic extends BlockMod
{
    private final TexturePlacement texturePlacement;
    public static final PropertyDynamicTexture DYNAMIC = new PropertyDynamicTexture();

    public BlockDynamic(ResourceLocation registryName, TexturePlacement texturePlacement, BlockProperties properties)
    {
        super(registryName, properties);
        this.texturePlacement = texturePlacement;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{DYNAMIC});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState maskedState = this.getDynamicState(state, world, pos);
            extendedState = extendedState.withProperty(DYNAMIC, maskedState);
            return extendedState;
        }

        return state;
    }

    public abstract IBlockState getDynamicState(IBlockState state, IBlockAccess world, BlockPos pos);

    public TexturePlacement getTexturePlacement()
    {
        return this.texturePlacement;
    }

    public ModelResourceLocation getModelLocation()
    {
        return new ModelResourceLocation(this.getRegistryName().toString(), "normal");
    }

    public enum TexturePlacement
    {
        OVER,
        UNDER
    }
}
