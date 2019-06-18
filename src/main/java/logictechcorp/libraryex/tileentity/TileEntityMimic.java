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

package logictechcorp.libraryex.tileentity;

import logictechcorp.libraryex.block.MimicBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

public class TileEntityMimic extends TileEntity
{
    private MimicBlock mimic;

    public TileEntityMimic(MimicBlock mimic, TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
        this.mimic = mimic;
    }

    @Override
    public IModelData getModelData()
    {
        return new ModelDataMap.Builder().withInitial(MimicBlock.MIMIC_PROP, this.mimic.getMimickedState(this.getBlockState(), this.world, this.pos)).build();
    }

    public MimicBlock getMimic()
    {
        return this.mimic;
    }
}
