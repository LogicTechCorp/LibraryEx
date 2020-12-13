/*
 * LibraryEx
 * Copyright (c) 2017-2019 by LogicTechCorp
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

package logictechcorp.libraryex.block.property;

import logictechcorp.libraryex.block.HarvestLevel;
import logictechcorp.libraryex.block.HarvestTool;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockProperties
{
    private final Material material;
    private final MapColor mapColor;
    private SoundType soundType = SoundType.STONE;
    private float lightLevel;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float resistance;
    private boolean tickRandomly;

    public BlockProperties(Material material, MapColor mapColor)
    {
        this.material = material;
        this.mapColor = mapColor;
    }

    public BlockProperties sound(SoundType soundType)
    {
        this.soundType = soundType;
        return this;
    }

    public BlockProperties lightLevel(float lightLevel)
    {
        this.lightLevel = lightLevel;
        return this;
    }

    public BlockProperties harvestLevel(HarvestTool harvestTool, HarvestLevel harvestLevel)
    {
        this.harvestTool = harvestTool.toString().toLowerCase();
        this.harvestLevel = harvestLevel.getLevel();
        return this;
    }

    public BlockProperties hardness(float hardness)
    {
        this.hardness = hardness;
        return this;
    }

    public BlockProperties resistance(float resistance)
    {
        this.resistance = Math.max(0.0F, resistance);
        return this;
    }

    public BlockProperties tickRandomly()
    {
        this.tickRandomly = true;
        return this;
    }

    public BlockProperties copy()
    {
        BlockProperties properties = new BlockProperties(this.material, this.mapColor);
        properties.soundType = this.soundType;
        properties.lightLevel = this.lightLevel;
        properties.harvestTool = this.harvestTool;
        properties.harvestLevel = this.harvestLevel;
        properties.hardness = this.hardness;
        properties.resistance = this.resistance;
        properties.tickRandomly = this.tickRandomly;
        return properties;
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

    public float getLightLevel()
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
}
