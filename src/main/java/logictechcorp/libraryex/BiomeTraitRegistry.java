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

package logictechcorp.libraryex;

import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTrait;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTraitBuilder;
import logictechcorp.libraryex.world.generation.trait.iface.IBiomeTraitRegistry;
import logictechcorp.libraryex.world.generation.trait.impl.*;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class BiomeTraitRegistry implements IBiomeTraitRegistry
{
    public static final IBiomeTraitRegistry INSTANCE = new BiomeTraitRegistry();
    private final Map<Class<? extends IBiomeTrait>, ResourceLocation> biomeTraitNames = new HashMap<>();
    private final Map<ResourceLocation, IBiomeTraitBuilder> biomeTraitBuilders = new HashMap<>();

    private BiomeTraitRegistry()
    {
        this.registerBiomeTrait(LibraryEx.getResource("scatter"), new BiomeTraitScatter.Builder(), BiomeTraitScatter.class);
        this.registerBiomeTrait(LibraryEx.getResource("cluster"), new BiomeTraitCluster.Builder(), BiomeTraitCluster.class);
        this.registerBiomeTrait(LibraryEx.getResource("ore"), new BiomeTraitOre.Builder(), BiomeTraitOre.class);
        this.registerBiomeTrait(LibraryEx.getResource("fluid"), new BiomeTraitFluid.Builder(), BiomeTraitFluid.class);
        this.registerBiomeTrait(LibraryEx.getResource("pool"), new BiomeTraitPool.Builder(), BiomeTraitPool.class);
        this.registerBiomeTrait(LibraryEx.getResource("basic_tree"), new BiomeTraitBasicTree.Builder(), BiomeTraitBasicTree.class);
        this.registerBiomeTrait(LibraryEx.getResource("big_mushroom"), new BiomeTraitBigMushroom.Builder(), BiomeTraitBigMushroom.class);
        this.registerBiomeTrait(LibraryEx.getResource("structure"), new BiomeTraitStructure.Builder(), BiomeTraitStructure.class);
    }

    @Override
    public void registerBiomeTrait(ResourceLocation registryName, IBiomeTraitBuilder biomeTraitBuilder, Class<? extends IBiomeTrait> cls)
    {
        if(registryName == null || biomeTraitBuilder == null || cls == null)
        {
            return;
        }

        if(!this.biomeTraitNames.containsKey(cls))
        {
            this.biomeTraitNames.put(cls, registryName);
        }

        if(!this.biomeTraitBuilders.containsKey(registryName))
        {
            this.biomeTraitBuilders.put(registryName, biomeTraitBuilder);
        }
    }

    @Override
    public void unregisterBiomeTrait(ResourceLocation registryName)
    {
        this.biomeTraitBuilders.remove(registryName);
    }

    @Override
    public boolean hasBiomeTrait(ResourceLocation registryName)
    {
        return this.biomeTraitBuilders.containsKey(registryName);
    }

    @Override
    public ResourceLocation getBiomeTraitName(Class<? extends IBiomeTrait> cls)
    {
        return this.biomeTraitNames.get(cls);
    }

    @Override
    public IBiomeTraitBuilder getBiomeTraitBuilder(ResourceLocation registryName)
    {
        return this.biomeTraitBuilders.get(registryName);
    }

    @Override
    public Map<Class<? extends IBiomeTrait>, ResourceLocation> getBiomeTraitNames()
    {
        return Collections.unmodifiableMap(this.biomeTraitNames);
    }

    @Override
    public Map<ResourceLocation, IBiomeTraitBuilder> getBiomeTraitBuilders()
    {
        return Collections.unmodifiableMap(this.biomeTraitBuilders);
    }
}
