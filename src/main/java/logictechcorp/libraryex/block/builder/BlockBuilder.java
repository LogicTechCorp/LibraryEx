/*
 * LibraryEx
 * Copyright (c) 2017-2018 by MineEx
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
 */

package logictechcorp.libraryex.block.builder;

import logictechcorp.libraryex.block.HarvestLevel;
import logictechcorp.libraryex.block.HarvestTool;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BlockBuilder
{
    private Material material;
    private MapColor mapColor;
    private SoundType soundType = SoundType.STONE;
    private CreativeTabs creativeTab = CreativeTabs.DECORATIONS;
    private int lightLevel;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float resistance;
    private boolean tickRandomly;
    private Item drop = null;

    public BlockBuilder(Material material, MapColor mapColor)
    {
        this.material = material;
        this.mapColor = mapColor;
    }

    public BlockBuilder sound(SoundType soundType)
    {
        this.soundType = soundType;
        return this;
    }

    public BlockBuilder creativeTab(CreativeTabs creativeTab)
    {
        this.creativeTab = creativeTab;
        return this;
    }

    public BlockBuilder lightLevel(float lightLevel)
    {
        this.lightLevel = (int) (15.0F * lightLevel);
        return this;
    }

    public BlockBuilder harvestLevel(HarvestTool harvestTool, HarvestLevel harvestLevel)
    {
        this.harvestTool = harvestTool.toString().toLowerCase();
        this.harvestLevel = harvestLevel.getLevel();
        return this;
    }

    public BlockBuilder hardness(float hardness)
    {
        this.hardness = hardness;
        return this;
    }

    public BlockBuilder resistance(float resistance)
    {
        this.resistance = Math.max(0.0F, resistance);
        return this;
    }

    public BlockBuilder tickRandomly()
    {
        this.tickRandomly = true;
        return this;
    }

    public BlockBuilder drop(Item drop)
    {
        this.drop = drop;
        return this;
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public MapColor getMapColor()
    {
        return this.mapColor;
    }

    public SoundType getSoundType()
    {
        return this.soundType;
    }

    public CreativeTabs getCreativeTab()
    {
        return this.creativeTab;
    }

    public int getLightLevel()
    {
        return this.lightLevel;
    }

    public String getHarvestTool()
    {
        return this.harvestTool;
    }

    public int getHarvestLevel()
    {
        return this.harvestLevel;
    }

    public float getHardness()
    {
        return this.hardness;
    }

    public float getResistance()
    {
        return this.resistance;
    }

    public boolean needsRandomTick()
    {
        return this.tickRandomly;
    }

    public Item getDrop()
    {
        return this.drop;
    }
}
