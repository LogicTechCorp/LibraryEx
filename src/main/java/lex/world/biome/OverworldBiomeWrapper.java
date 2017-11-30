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

public class OverworldBiomeWrapper extends AbstractBiomeWrapper
{
    protected IBlockState oceanBlock;

    OverworldBiomeWrapper(Builder builder)
    {
        super(builder);
        oceanBlock = builder.oceanBlock;
    }

    public static class Builder extends AbstractBuilder<Builder, OverworldBiomeWrapper>
    {
        protected IBlockState oceanBlock;

        @Override
        public Builder configure(IConfig config)
        {
            biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(config.getString("biome")));

            if(biome == null)
            {
                biome = Biomes.PLAINS;
            }

            super.configure(config);
            oceanBlock = config.getBlock("oceanBlock", Blocks.WATER.getDefaultState());
            return this;
        }

        @Override
        public OverworldBiomeWrapper create()
        {
            return new OverworldBiomeWrapper(this);
        }

        public IBlockState getOceanBlock()
        {
            return oceanBlock;
        }
    }
}
