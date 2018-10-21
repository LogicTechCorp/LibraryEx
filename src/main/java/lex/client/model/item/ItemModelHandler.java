package lex.client.model.item;

import lex.IModData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemModelHandler
{
    public static void registerBlockModel(Block block, int metadata, String location, String variant)
    {
        registerItemModel(Item.getItemFromBlock(block), metadata, location, variant);
    }

    public static void registerBlockModel(Block block, String variant)
    {
        registerItemModel(Item.getItemFromBlock(block), variant);
    }

    public static void registerItemModel(Item item, String variant)
    {
        registerItemModel(item, 0, item.getRegistryName().toString(), variant);
    }

    public static void registerItemModel(Item item, int metadata, String location, String variant)
    {
        if(item != Items.AIR)
        {
            ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(location, variant));
        }
    }

    public static void registerModel(IModData data, IFluidBlock block)
    {
        Item item = Item.getItemFromBlock((Block) block);
        ModelBakery.registerItemVariants(item);
        ModelResourceLocation modelLocation = new ModelResourceLocation(data.getModId() + ":fluid", block.getFluid().getName());
        ModelLoader.setCustomMeshDefinition(item, stack -> modelLocation);
        ModelLoader.setCustomStateMapper((Block) block, new StateMapperBase()
        {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return modelLocation;
            }
        });
    }
}
