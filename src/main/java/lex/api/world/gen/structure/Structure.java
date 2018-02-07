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

package lex.api.world.gen.structure;

import lex.api.pattern.ILayer;
import lex.api.pattern.IRow;
import lex.api.pattern.Pattern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Structure extends Pattern implements IStructure
{
    protected Map<Character, IBlockState> blocks = new HashMap<>();
    protected Map<Character, Class<? extends Entity>> entities = new HashMap<>();
    protected ResourceLocation name;
    protected BlockPos size = BlockPos.ORIGIN;

    @Override
    public void addBlock(char character, IBlockState state)
    {
        if(!blocks.containsKey(character))
        {
            blocks.put(character, state);
        }
    }

    @Override
    public void addEntity(char character, Class<? extends Entity> cls)
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
    public ResourceLocation getName()
    {
        return name;
    }

    @Override
    public BlockPos getSize()
    {
        return size;
    }
}
