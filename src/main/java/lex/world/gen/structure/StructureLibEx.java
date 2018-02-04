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

package lex.world.gen.structure;

import com.google.gson.JsonElement;
import lex.api.config.IConfig;
import lex.api.pattern.ILayer;
import lex.api.pattern.IRow;
import lex.api.pattern.Layer;
import lex.api.pattern.Row;
import lex.api.world.gen.structure.Structure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureLibEx extends Structure
{
    private IConfig config;

    public StructureLibEx(IConfig configIn)
    {
        config = configIn;
        parse();
    }

    private void parse()
    {
        IConfig blockConfig = config.getInnerConfig("blocks");
        IConfig entityConfig = config.getInnerConfig("entities");
        List<IConfig> layerConfigs = config.getInnerConfigs("layers", new ArrayList<>());

        for(Map.Entry<String, JsonElement> entry : blockConfig.getElements().entrySet())
        {
            String character = entry.getKey();
            IBlockState state = blockConfig.getBlock(entry.getKey());

            if(state != null && character.length() == 1)
            {
                addBlock(character.charAt(0), state);
            }
        }

        for(Map.Entry<String, JsonElement> entry : entityConfig.getElements().entrySet())
        {
            String character = entry.getKey();
            Class<? extends Entity> entity = EntityList.getClassFromName(entityConfig.getInnerConfig(entry.getKey()).getString("entity"));

            if(entity != null && character.length() == 1)
            {
                addEntity(character.charAt(0), entity);
            }
        }

        for(IConfig layerConfig : layerConfigs)
        {
            List<IRow> rows = new ArrayList<>();

            for(String section : layerConfig.getStrings("sections", new ArrayList<>()))
            {
                IRow row = new Row();

                for(Character character : section.toCharArray())
                {
                    row.addSection(character);
                }

                rows.add(row);
            }

            addLayer(new Layer(rows));
        }

        name = config.getResource("structure", new ResourceLocation("MissingNo_" + new Timestamp(System.currentTimeMillis()).getTime()));

        int x = 0;
        int y = layers.size();
        int z = 0;

        for(ILayer layer : layers)
        {
            int rowAmount = layer.getRows().size();

            if(rowAmount > x)
            {
                x = rowAmount;
            }

            for(IRow row : layer.getRows())
            {
                int rowSize = row.getSections().size();

                if(rowSize > z)
                {
                    z = rowSize;
                }
            }
        }

        size = new BlockPos(x, y, z);
    }
}
