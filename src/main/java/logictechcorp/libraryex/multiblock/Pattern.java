/*
 * Elemental Sciences 2
 * Copyright (c) by Jezza
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
 *
 * Original: https://github.com/Jezza/Elemental-Sciences-2/blob/a2a11fded297b5a27287bf82a672aa05943894da/es_common/me/jezzadabomb/es2/api/multiblock/DimensionalPattern.java
 * (Edited to work with multiple mods)
 */

package logictechcorp.libraryex.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pattern
{
    private static final PatternElement AIR_ELEMENT = new PatternElement(Blocks.AIR.getDefaultState(), ' ');
    private static final PatternElement VOID_ELEMENT = new PatternElement(Blocks.STRUCTURE_VOID.getDefaultState(), '*');

    private List<PatternLayer> layers = new ArrayList<>();
    private Map<Character, PatternElement> elements = new HashMap<>();

    private Pattern(IPatternComponent... components)
    {
        for(IPatternComponent component : components)
        {
            IPatternComponent.Type type = component.getType();

            if(type == IPatternComponent.Type.LAYER)
            {
                this.layers.add((PatternLayer) component);
            }
            else if(type == IPatternComponent.Type.ELEMENT)
            {
                PatternElement element = (PatternElement) component;
                this.elements.put(element.getIdentifier(), element);
            }
        }
    }

    public static Pattern createPattern(IPatternComponent... components)
    {
        return new Pattern(components);
    }

    public static PatternRow createRow(String sections)
    {
        return new PatternRow(sections);
    }

    public static PatternLayer createLayer(PatternRow... rows)
    {
        return new PatternLayer(rows);
    }

    public static PatternElement createElement(IBlockState state, char identifier)
    {
        if(state == AIR_ELEMENT.getBlockState() || identifier == AIR_ELEMENT.getIdentifier())
        {
            throw new InvalidParameterException("Tried to create a PatternElement with invalid parameters");
        }
        else if(state == VOID_ELEMENT.getBlockState() || identifier == VOID_ELEMENT.getIdentifier())
        {
            throw new InvalidParameterException("Tried to create a PatternElement with invalid parameters");
        }

        return new PatternElement(state, identifier);
    }

    public static PatternElementOre createElement(IBlockState state, char identifier, String... ores)
    {
        if(state == AIR_ELEMENT.getBlockState() || identifier == AIR_ELEMENT.getIdentifier())
        {
            throw new InvalidParameterException("Tried to create a PatternElement with invalid parameters");
        }
        else if(state == VOID_ELEMENT.getBlockState() || identifier == VOID_ELEMENT.getIdentifier())
        {
            throw new InvalidParameterException("Tried to create a PatternElement with invalid parameters");
        }

        return new PatternElementOre(state, identifier, ores);
    }

    public boolean placeInWorld(World world, BlockPos pos, boolean replaceExtraneousBlocks)
    {
        if(world == null)
        {
            return false;
        }

        int layerPos = 0;

        for(PatternLayer layer : this.layers)
        {
            int rowPos = 0;

            for(PatternRow row : layer.getRows())
            {
                String sections = row.getSections();

                for(int depth = 0; depth < sections.length(); depth++)
                {
                    char identifier = sections.charAt(depth);
                    BlockPos adjustedPos = pos.add(rowPos, layerPos, depth);

                    if(identifier == VOID_ELEMENT.getIdentifier())
                    {
                        continue;
                    }
                    if(identifier == AIR_ELEMENT.getIdentifier())
                    {
                        if(replaceExtraneousBlocks)
                        {
                            world.setBlockToAir(adjustedPos);
                        }

                        continue;
                    }

                    PatternElement element = this.elements.get(identifier);

                    if(element == null || (!replaceExtraneousBlocks && !element.matches(world.getBlockState(adjustedPos))))
                    {
                        return false;
                    }

                    if(replaceExtraneousBlocks || world.isAirBlock(adjustedPos))
                    {
                        world.setBlockState(adjustedPos, element.getBlockState(), 3);
                    }
                }

                rowPos++;
            }

            layerPos++;
        }

        return true;
    }

    public boolean hasFormed(World world, BlockPos pos)
    {
        if(world == null)
        {
            return false;
        }

        int layerPos = 0;

        for(PatternLayer layer : this.layers)
        {
            int rowPos = 0;

            for(PatternRow row : layer.getRows())
            {
                String sections = row.getSections();

                for(int depth = 0; depth < sections.length(); depth++)
                {
                    char identifier = sections.charAt(depth);
                    BlockPos adjustedPos = pos.add(rowPos, layerPos, depth);

                    if(identifier == VOID_ELEMENT.getIdentifier())
                    {
                        continue;
                    }
                    if(identifier == AIR_ELEMENT.getIdentifier())
                    {
                        if(!world.isAirBlock(adjustedPos))
                        {
                            return false;
                        }

                        continue;
                    }

                    PatternElement element = this.elements.get(identifier);

                    if(element == null || !element.matches(world.getBlockState(adjustedPos)))
                    {
                        return false;
                    }
                }

                rowPos++;
            }

            layerPos++;
        }

        return true;
    }
}
