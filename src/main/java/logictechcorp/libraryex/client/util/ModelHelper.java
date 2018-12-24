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
 *
 */

package logictechcorp.libraryex.client.util;

import logictechcorp.libraryex.IModData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHelper
{
    private static StateMapperBase stringMapper = new StateMapperBase()
    {
        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state)
        {
            return new ModelResourceLocation(Blocks.AIR.getRegistryName().toString());
        }
    };

    public static void registerBlockItemModel(IBlockState state)
    {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);

        if(item != Items.AIR)
        {
            registerItemModel(item, new ModelResourceLocation(block.getRegistryName(), stringMapper.getPropertyString(state.getProperties())));
        }
    }

    public static void registerBlockItemModel(IBlockState state, String variant)
    {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);

        if(item != Items.AIR)
        {
            registerItemModel(item, new ModelResourceLocation(block.getRegistryName(), variant));
        }
    }

    public static void registerItemModel(Item item)
    {
        registerItemModel(item, item.getRegistryName().toString());
    }

    public static void registerItemModel(Item item, String modelLocation)
    {
        ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
        registerItemModel(item, fullModelLocation);
    }

    public static void registerItemModel(Item item, ModelResourceLocation fullModelLocation)
    {
        ModelBakery.registerItemVariants(item, fullModelLocation);
        registerItemModel(item, stack -> fullModelLocation);
    }

    public static void registerItemModel(Item item, ItemMeshDefinition meshDefinition)
    {
        ModelLoader.setCustomMeshDefinition(item, meshDefinition);
    }

    public static void registerItemModelForMeta(Item item, int metadata, String variant)
    {
        registerItemModelForMeta(item, metadata, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    public static void registerItemModelForMeta(Item item, int metadata, ModelResourceLocation resourceLocation)
    {
        ModelLoader.setCustomModelResourceLocation(item, metadata, resourceLocation);
    }

    public static void registerFluidModel(IModData data, IFluidBlock block)
    {
        Item item = Item.getItemFromBlock((Block) block);

        if(item != Items.AIR)
        {
            ModelResourceLocation resourceLocation = new ModelResourceLocation(data.getModId() + ":" + block.getFluid().getName());
            ModelBakery.registerItemVariants(item);
            ModelLoader.setCustomMeshDefinition(item, stack -> resourceLocation);
            ModelLoader.setCustomStateMapper((Block) block, new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return resourceLocation;
                }
            });
        }
    }
}
