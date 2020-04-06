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

import logictechcorp.libraryex.tileentity.MimicTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelProperty;

public abstract class MimicBlock<T extends MimicTileEntity> extends TileEntityBlock<T>
{
    public static final ModelProperty<BlockState> MIMIC_PROP = new ModelProperty<>();
    private final MimicType mimicType;

    public MimicBlock(Block.Properties properties, MimicType mimicType, Class<T> cls)
    {
        super(properties, cls);
        this.mimicType = mimicType;
    }

    public abstract BlockState getMimickedState(BlockState state, IWorldReader world, BlockPos pos);

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof MimicTileEntity)
        {
            tileEntity.requestModelDataUpdate();
        }
    }

    public ModelResourceLocation getModelLocation()
    {
        return new ModelResourceLocation(this.getRegistryName().toString(), "");
    }

    public MimicType getMimicType()
    {
        return this.mimicType;
    }

    public enum MimicType
    {
        OVERLAY,
        UNDERLAY,
        FULL
    }
}