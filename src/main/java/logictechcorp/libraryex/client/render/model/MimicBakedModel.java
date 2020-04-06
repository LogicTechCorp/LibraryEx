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

package logictechcorp.libraryex.client.render.model;

import logictechcorp.libraryex.block.MimicBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MimicBakedModel extends BakedModelWrapper<IBakedModel>
{
    private final MimicBlock.MimicType mimicType;

    public MimicBakedModel(IBakedModel originalModel, MimicBlock.MimicType mimicType)
    {
        super(originalModel);
        this.mimicType = mimicType;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData modelData)
    {
        return super.getParticleTexture(modelData);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random random, IModelData modelData)
    {
        List<BakedQuad> originalQuads = new ArrayList<>(this.originalModel.getQuads(state, side, random, modelData));
        List<BakedQuad> mimickedQuads = new ArrayList<>(this.getMimickedModel(modelData.getData(MimicBlock.MIMIC_PROP)).getQuads(state, side, random, modelData));
        List<BakedQuad> retQuads;

        if(this.mimicType == MimicBlock.MimicType.OVERLAY)
        {
            retQuads = originalQuads;
            retQuads.addAll(mimickedQuads);
        }
        else if(this.mimicType == MimicBlock.MimicType.UNDERLAY)
        {
            retQuads = mimickedQuads;
            retQuads.addAll(originalQuads);
        }
        else
        {
            retQuads = mimickedQuads;
        }

        return retQuads;
    }

    @Override
    public IModelData getModelData(ILightReader reader, BlockPos pos, BlockState state, IModelData modelData)
    {
        return modelData;
    }

    private IBakedModel getMimickedModel(BlockState state)
    {
        if(state != null)
        {
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockModelShapes shapes = dispatcher.getBlockModelShapes();
            return shapes.getModel(state);
        }

        return this.originalModel;
    }
}
