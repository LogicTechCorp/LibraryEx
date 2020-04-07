package logictechcorp.libraryex.block.pattern;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.tags.Tag;

public class BlockTagMatcher extends BlockMatcher
{
    private final Tag<Block> blockTag;

    public BlockTagMatcher(Tag<Block> blockTag)
    {
        super(Blocks.AIR);
        this.blockTag = blockTag;
    }

    @Override
    public boolean test(BlockState state)
    {
        return this.blockTag.contains(state.getBlock());
    }
}
