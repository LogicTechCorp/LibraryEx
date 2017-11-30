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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EndBiomeWrapper extends AbstractBiomeWrapper
{
    protected IBlockState wallBlock;
    protected IBlockState islandBottomBlock;
    protected IBlockState islandFillerBlock;

    EndBiomeWrapper(Builder builder)
    {
        super(builder);
        wallBlock = builder.wallBlock;
        islandBottomBlock = builder.islandBottomBlock;
        islandFillerBlock = builder.islandFillerBlock;
    }

    public static class Builder extends AbstractBuilder<Builder, EndBiomeWrapper>
    {
        protected IBlockState wallBlock;
        protected IBlockState islandBottomBlock;
        protected IBlockState islandFillerBlock;

        @Override
        public Builder configure(IConfig config)
        {
            biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(config.getString("biome")));

            if(biome == null)
            {
                biome = Biomes.SKY;
            }

            super.configure(config);
            wallBlock = config.getBlock("wallBlock", biome.fillerBlock);
            islandBottomBlock = config.getBlock("islandBottomBlock", biome.fillerBlock);
            islandFillerBlock = config.getBlock("islandFillerBlock", biome.fillerBlock);
            return this;
        }

        @Override
        public EndBiomeWrapper create()
        {
            return new EndBiomeWrapper(this);
        }

        public IBlockState getWallBlock()
        {
            return wallBlock;
        }

        public IBlockState getIslandBottomBlock()
        {
            return islandBottomBlock;
        }

        public IBlockState getIslandFillerBlock()
        {
            return islandFillerBlock;
        }
    }
}
