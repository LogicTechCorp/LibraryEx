/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.world.biome;

import lex.config.IConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class NetherBiomeWrapper extends AbstractBiomeWrapper
{
    protected IBlockState oceanBlock;
    protected IBlockState wallBlock;
    protected IBlockState ceilingBottomBlock;
    protected IBlockState ceilingFillerBlock;

    NetherBiomeWrapper(Builder builder)
    {
        super(builder);
        oceanBlock = builder.oceanBlock;
        wallBlock = builder.wallBlock;
        ceilingBottomBlock = builder.ceilingBottomBlock;
        ceilingFillerBlock = builder.ceilingFillerBlock;
    }

    public static class Builder extends AbstractBuilder<Builder, NetherBiomeWrapper>
    {
        protected IBlockState oceanBlock;
        protected IBlockState wallBlock;
        protected IBlockState ceilingBottomBlock;
        protected IBlockState ceilingFillerBlock;

        @Override
        public Builder configure(IConfig config)
        {
            biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(config.getString("biome")));

            if(biome == null)
            {
                biome = Biomes.HELL;
            }

            super.configure(config);
            oceanBlock = config.getBlock("oceanBlock", Blocks.LAVA.getDefaultState());
            wallBlock = config.getBlock("wallBlock", biome.fillerBlock);
            ceilingBottomBlock = config.getBlock("ceilingBottomBlock", biome.fillerBlock);
            ceilingFillerBlock = config.getBlock("ceilingFillerBlock", biome.fillerBlock);
            return this;
        }

        @Override
        public NetherBiomeWrapper create()
        {
            return new NetherBiomeWrapper(this);
        }

        public IBlockState getOceanBlock()
        {
            return oceanBlock;
        }

        public IBlockState getWallBlock()
        {
            return wallBlock;
        }

        public IBlockState getCeilingBottomBlock()
        {
            return ceilingBottomBlock;
        }

        public IBlockState getCeilingFillerBlock()
        {
            return ceilingFillerBlock;
        }
    }
}
