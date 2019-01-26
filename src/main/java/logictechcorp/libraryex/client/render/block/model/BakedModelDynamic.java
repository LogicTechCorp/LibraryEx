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

import logictechcorp.libraryex.block.BlockDynamic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class BakedModelDynamic implements IBakedModel
{
    private final IBakedModel originalModel;
    private final BlockDynamic.TexturePlacement texturePlacement;

    public BakedModelDynamic(IBakedModel originalModel, BlockDynamic.TexturePlacement texturePlacement)
    {
        this.originalModel = originalModel;
        this.texturePlacement = texturePlacement;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long random)
    {
        List<BakedQuad> originalQuads = new ArrayList<>(this.originalModel.getQuads(state, side, random));
        List<BakedQuad> dynamicQuads = new ArrayList<>(this.getDynamicModel(state).getQuads(state, side, random));
        List<BakedQuad> quads;

        if(this.texturePlacement == BlockDynamic.TexturePlacement.OVER)
        {
            quads = originalQuads;
            quads.addAll(dynamicQuads);
        }
        else
        {
            quads = dynamicQuads;
            quads.addAll(originalQuads);
        }

        return quads;
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

    private IBakedModel getDynamicModel(IBlockState state)
    {
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState stateMask = extendedState.getValue(BlockDynamic.DYNAMIC);

            if(stateMask != null)
            {
                Minecraft mc = Minecraft.getMinecraft();
                BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
                BlockModelShapes shapes = dispatcher.getBlockModelShapes();
                return shapes.getModelForState(stateMask);
            }
        }

        return this.originalModel;
    }
}
