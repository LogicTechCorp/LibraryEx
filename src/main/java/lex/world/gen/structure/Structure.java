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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Structure implements IStructure
{
    private Map<String, IBlockState> blocks = new HashMap<>();
    private Map<String, Class<? extends Entity>> entities = new HashMap<>();
    private Map<BlockPos, String> layers = new HashMap<>();
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
            IBlockState state = blockConfig.getBlock(entry.getKey());

            if(state != null)
            {
                addBlock(entry.getKey(), state);
            }
        }

        for(Map.Entry<String, JsonElement> entry : entityConfig.getElements().entrySet())
        {
            Class<? extends Entity> entity = EntityList.getClassFromName(entityConfig.getInnerConfig(entry.getKey()).getString("entity"));

            if(entity != null)
            {
                addEntity(entry.getKey(), entity);
            }
        }

        for(IConfig layerConfig : layerConfigs)
        {
            int level = layerConfig.getInt("level");
            List<String> segments = layerConfig.getStrings("segments");

            if(segments != null)
            {
                addLayer(segments.toArray(new String[segments.size()]), level);
            }
        }

        for(BlockPos pos : layers.keySet())
        {
            if(pos.getX() > size.getX() || pos.getY() > size.getY() || pos.getZ() > size.getZ())
            {
                size = pos;
            }
        }

        size = size.add(1, 1, 1);
    }

    @Override
    public void addBlock(String character, IBlockState state)
    {
        if(!blocks.containsKey(character))
        {
            blocks.put(character, state);
        }
    }

    @Override
    public void addEntity(String character, Class<? extends Entity> cls)
    {
        if(!entities.containsKey(character))
        {
            entities.put(character, cls);
        }
    }

    @Override
    public void addLayer(String[] layer, int level)
    {
        for(int x = 0; x < layer.length; x++)
        {
            for(int z = 0; z < layer[x].length(); z++)
            {
                layers.put(new BlockPos(x, level, z), layer[x].substring(z, z + 1));
            }
        }
    }

    @Override
    public void generate(World world, BlockPos pos, Mirror mirror, Rotation rotation)
    {
        for(Map.Entry<BlockPos, String> entry : layers.entrySet())
        {
            String character = entry.getValue();
            BlockPos placementPos = Template.getZeroPositionWithTransform(pos.add(entry.getKey()), mirror, rotation, size.getX(), size.getZ());

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
