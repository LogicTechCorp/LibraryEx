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

package logictechcorp.libraryex.api.internal;

import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTrait;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTraitBuilder;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTraitRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class BiomeTraitRegistryStub implements IBiomeTraitRegistry
{
    public static final IBiomeTraitRegistry INSTANCE = new BiomeTraitRegistryStub();

    private BiomeTraitRegistryStub()
    {
    }

    @Override
    public void registerBiomeTrait(ResourceLocation registryName, IBiomeTraitBuilder biomeTraitBuilder, Class<? extends IBiomeTrait> cls)
    {

    }

    @Override
    public void unregisterBiomeTrait(ResourceLocation registryName)
    {

    }

    @Override
    public boolean hasBiomeTrait(ResourceLocation registryName)
    {
        return false;
    }

    @Override
    public ResourceLocation getBiomeTraitName(Class<? extends IBiomeTrait> cls)
    {
        return new ResourceLocation("libraryex:missing_no");
    }

    @Override
    public IBiomeTraitBuilder getBiomeTraitBuilder(ResourceLocation registryName)
    {
        return null;
    }

    @Override
    public Map<Class<? extends IBiomeTrait>, ResourceLocation> getBiomeTraitNames()
    {
        return new HashMap<>();
    }

    @Override
    public Map<ResourceLocation, IBiomeTraitBuilder> getBiomeTraitBuilders()
    {
        return new HashMap<>();
    }
}
