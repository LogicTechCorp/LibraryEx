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

package logictechcorp.libraryex.client.render.block.model;

import logictechcorp.libraryex.block.MimicBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MimicBakedModel implements IDynamicBakedModel
{
    private final IBakedModel originalModel;
    private final MimicBlock.MimicType mimicType;

    public MimicBakedModel(IBakedModel originalModel, MimicBlock.MimicType mimicType)
    {
        this.originalModel = originalModel;
        this.mimicType = mimicType;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return this.originalModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return this.originalModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return this.originalModel.isBuiltInRenderer();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random random, IModelData data)
    {
        List<BakedQuad> originalQuads = new ArrayList<>(this.originalModel.getQuads(state, side, random));
        List<BakedQuad> mimickedQuads = new ArrayList<>(this.getMimickedModel(data.getData(MimicBlock.MIMIC_PROP)).getQuads(state, side, random));
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
    public IModelData getModelData(IEnviromentBlockReader world, BlockPos pos, BlockState state, IModelData modelData)
    {
        Block block = state.getBlock();

        if(block instanceof MimicBlock)
        {
            return new ModelDataMap.Builder().withInitial(MimicBlock.MIMIC_PROP, ((MimicBlock) block).getMimickedState(state, world, pos)).build();
        }

        return modelData;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return this.originalModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return this.originalModel.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type)
    {
        return Pair.of(this, this.originalModel.handlePerspective(type).getRight());
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
