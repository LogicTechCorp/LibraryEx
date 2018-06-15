/*
 * LibEx
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
 */

package lex.fluid;

import lex.api.IModData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidLibEx extends Fluid
{
    public FluidLibEx(IModData data, String fluidName)
    {
        this(data, fluidName, fluidName);
    }

    public FluidLibEx(IModData data, String fluidName, String textureName)
    {
        super(fluidName, new ResourceLocation(data.getModId() + ":blocks/fluid_" + textureName + "_still"), new ResourceLocation(data.getModId() + ":blocks/fluid_" + textureName + "_flow"));

        FluidRegistry.registerFluid(this);
        FluidRegistry.addBucketForFluid(this);
    }
}
