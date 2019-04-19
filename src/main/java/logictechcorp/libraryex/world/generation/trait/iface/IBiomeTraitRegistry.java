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

package logictechcorp.libraryex.world.generation.trait.iface;

import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IBiomeTraitRegistry
{
    /**
     * Called to register a biome trait.
     *
     * @param registryName      The name of the biome trait.
     * @param biomeTraitBuilder The builder that is used to create new instance of the biome trait.
     */
    void registerBiomeTrait(ResourceLocation registryName, IBiomeTraitBuilder biomeTraitBuilder, Class<? extends IBiomeTrait> cls);

    /**
     * Called to unregister a biome trait.
     *
     * @param registryName The name of the biome trait.
     */
    void unregisterBiomeTrait(ResourceLocation registryName);

    /**
     * Called to check if a biome trait is registered under a registry name.
     *
     * @param registryName The name of the biome trait.
     * @return Whether a biome trait is registered under
     */
    boolean hasBiomeTrait(ResourceLocation registryName);

    /**
     * Called to get the registry name of a biome trait.
     *
     * @param cls The class that defines the biome trait.
     * @return The registry name of a biome trait.
     */
    ResourceLocation getBiomeTraitName(Class<? extends IBiomeTrait> cls);

    /**
     * Called to get a biome trait builder.
     *
     * @param registryName The name of the biome trait,
     * @return A biome trait builder.
     */
    IBiomeTraitBuilder getBiomeTraitBuilder(ResourceLocation registryName);

    /**
     * Called to get a map containing biome trait classes and their registry names.
     *
     * @return A map containing biome trait classes and their registry names.
     */
    Map<Class<? extends IBiomeTrait>, ResourceLocation> getBiomeTraitNames();

    /**
     * Called to get a map containing biome trait registry names and their builders.
     *
     * @return A map containing biome trait registry names and their builders.
     */
    Map<ResourceLocation, IBiomeTraitBuilder> getBiomeTraitBuilders();
}
