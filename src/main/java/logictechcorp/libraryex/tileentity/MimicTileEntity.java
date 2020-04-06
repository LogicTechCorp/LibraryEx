package logictechcorp.libraryex.tileentity;

import logictechcorp.libraryex.block.MimicBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

public class MimicTileEntity extends TileEntity
{
    public MimicTileEntity(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
    }

    @Override
    public IModelData getModelData()
    {
        if(this.world != null)
        {
            BlockState state = this.getBlockState();
            Block block = state.getBlock();

            if(block instanceof MimicBlock)
            {
                return new ModelDataMap.Builder().withInitial(MimicBlock.MIMIC_PROP, ((MimicBlock) block).getMimickedState(state, this.world, this.pos)).build();
            }
        }
        return super.getModelData();
    }
}
