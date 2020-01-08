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

package logictechcorp.libraryex.world.generation.trait;

import logictechcorp.libraryex.LibraryEx;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class BiomeTraitRegistry
{
    public static final BiomeTraitRegistry INSTANCE = new BiomeTraitRegistry();

    private final Map<Class<? extends BiomeTrait>, ResourceLocation> biomeTraitNames = new HashMap<>();
    private final Map<ResourceLocation, BiomeTrait.Builder<?>> biomeTraitBuilders = new HashMap<>();

    private BiomeTraitRegistry()
    {
        this.registerBiomeTrait(LibraryEx.getResource("scatter"), new BiomeTraitScatter.Builder(), BiomeTraitScatter.class);
        this.registerBiomeTrait(LibraryEx.getResource("cluster"), new BiomeTraitCluster.Builder(), BiomeTraitCluster.class);
        this.registerBiomeTrait(LibraryEx.getResource("patch"), new BiomeTraitPatch.Builder(), BiomeTraitPatch.class);
        this.registerBiomeTrait(LibraryEx.getResource("boulder"), new BiomeTraitBoulder.Builder(), BiomeTraitBoulder.class);
        this.registerBiomeTrait(LibraryEx.getResource("ore"), new BiomeTraitOre.Builder(), BiomeTraitOre.class);
        this.registerBiomeTrait(LibraryEx.getResource("fluid"), new BiomeTraitFluid.Builder(), BiomeTraitFluid.class);
        this.registerBiomeTrait(LibraryEx.getResource("pool"), new BiomeTraitPool.Builder(), BiomeTraitPool.class);
        this.registerBiomeTrait(LibraryEx.getResource("basic_tree"), new BiomeTraitBasicTree.Builder(), BiomeTraitBasicTree.class);
        this.registerBiomeTrait(LibraryEx.getResource("dense_tree"), new BiomeTraitDenseTree.Builder(), BiomeTraitDenseTree.class);
        this.registerBiomeTrait(LibraryEx.getResource("sparse_tree"), new BiomeTraitSparseTree.Builder(), BiomeTraitSparseTree.class);
        this.registerBiomeTrait(LibraryEx.getResource("big_mushroom"), new BiomeTraitBigMushroom.Builder(), BiomeTraitBigMushroom.class);
        this.registerBiomeTrait(LibraryEx.getResource("structure"), new BiomeTraitStructure.Builder(), BiomeTraitStructure.class);
    }

    public void registerBiomeTrait(ResourceLocation registryName, BiomeTrait.Builder<?> biomeTraitBuilder, Class<? extends BiomeTrait> cls)
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

    public void unregisterBiomeTrait(ResourceLocation registryName)
    {
        this.biomeTraitBuilders.remove(registryName);
    }

    public boolean hasBiomeTrait(ResourceLocation registryName)
    {
        return this.biomeTraitBuilders.containsKey(registryName);
    }

    public ResourceLocation getBiomeTraitName(Class<? extends BiomeTrait> cls)
    {
        return this.biomeTraitNames.get(cls);
    }

    public BiomeTrait.Builder<?> getBiomeTraitBuilder(ResourceLocation registryName)
    {
        return this.biomeTraitBuilders.get(registryName);
    }

    public Map<Class<? extends BiomeTrait>, ResourceLocation> getBiomeTraitNames()
    {
        return Collections.unmodifiableMap(this.biomeTraitNames);
    }

    public Map<ResourceLocation, BiomeTrait.Builder<?>> getBiomeTraitBuilders()
    {
        return Collections.unmodifiableMap(this.biomeTraitBuilders);
    }
}
