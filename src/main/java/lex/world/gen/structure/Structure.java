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
import lex.config.IConfig;
import lex.pattern.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Structure extends Pattern implements IStructure
{
    private Map<Character, IBlockState> blocks = new HashMap<>();
    private Map<Character, Class<? extends Entity>> entities = new HashMap<>();
    private BlockPos size = BlockPos.ORIGIN;
    private IConfig config;

    public Structure(IConfig configIn)
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

    @Override
    public void addBlock(Character character, IBlockState state)
    {
        if(!blocks.containsKey(character))
        {
            blocks.put(character, state);
        }
    }

    @Override
    public void addEntity(Character character, Class<? extends Entity> cls)
    {
        if(!entities.containsKey(character))
        {
            entities.put(character, cls);
        }
    }


    @Override
    public void generate(World world, BlockPos pos, Mirror mirror, Rotation rotation)
    {
        for(ILayer layer : layers)
        {
            for(IRow row : layer.getRows())
            {
                List<Character> sections = row.getSections();

                for(int characterIndex = 0; characterIndex < sections.size(); characterIndex++)
                {
                    char character = sections.get(characterIndex);
                    BlockPos sectionPos = new BlockPos(layer.getRows().indexOf(row), layers.indexOf(layer), characterIndex);
                    BlockPos placementPos;

                    if(rotation == Rotation.NONE)
                    {
                        placementPos = new BlockPos(pos.getX() + sectionPos.getZ(), pos.getY() + sectionPos.getY(), pos.getZ() + sectionPos.getX());
                    }
                    else if(rotation == Rotation.CLOCKWISE_90)
                    {
                        placementPos = new BlockPos(pos.getX() - sectionPos.getX(), pos.getY() + sectionPos.getY(), pos.getZ() + sectionPos.getZ());
                    }
                    else if(rotation == Rotation.CLOCKWISE_180)
                    {
                        placementPos = new BlockPos(pos.getX() - sectionPos.getZ(), pos.getY() + sectionPos.getY(), pos.getZ() - sectionPos.getX());
                    }
                    else
                    {
                        placementPos = new BlockPos(pos.getX() + sectionPos.getX(), pos.getY() + sectionPos.getY(), pos.getZ() - sectionPos.getZ());
                    }

                    if(blocks.containsKey(character))
                    {
                        world.setBlockState(placementPos, blocks.get(character).withMirror(mirror).withRotation(rotation));
                    }
                    else if(entities.containsKey(character))
                    {
                        Entity entity = EntityList.newEntity(entities.get(character), world);

                        if(entity != null)
                        {
                            entity.setPosition(placementPos.getX() + 0.5F, placementPos.getY(), placementPos.getZ() + 0.5F);
                            world.spawnEntity(entity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockPos getSize()
    {
        return size;
    }

    @Override
    public IConfig getConfig()
    {
        return config;
    }
}
