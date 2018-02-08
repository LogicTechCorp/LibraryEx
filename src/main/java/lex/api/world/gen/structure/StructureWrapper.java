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

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;

import java.util.ArrayList;
import java.util.List;

public abstract class StructureWrapper implements IStructureWrapper
{
    protected ResourceLocation structure;
    protected Mirror mirror = Mirror.NONE;
    protected Rotation rotation = Rotation.NONE;
    protected IBlockState ignoredBlock = Blocks.STRUCTURE_VOID.getDefaultState();
    protected List<ResourceLocation> lootTables = new ArrayList<>();
    protected List<ResourceLocation> spawnerMobs = new ArrayList<>();

    public StructureWrapper(ResourceLocation structureIn)
    {
        structure = structureIn;
    }

    @Override
    public ResourceLocation getStructure()
    {
        return structure;
    }

    @Override
    public Mirror getMirror()
    {
        return mirror;
    }

    @Override
    public Rotation getRotation()
    {
        return rotation;
    }

    @Override
    public IBlockState getIgnoredBlock()
    {
        return ignoredBlock;
    }

    @Override
    public List<ResourceLocation> getLootTables()
    {
        return ImmutableList.copyOf(lootTables);
    }

    @Override
    public List<ResourceLocation> getSpawnerMobs()
    {
        return ImmutableList.copyOf(spawnerMobs);
    }
}
