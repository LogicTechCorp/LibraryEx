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

package lex.handler;

import lex.LibEx;
import lex.world.biome.BiomeWrapperBuilder;
import lex.world.biome.BiomeWrapperEnd;
import lex.world.biome.BiomeWrapperNether;
import lex.world.biome.BiomeWrapperOverworld;
import lex.world.gen.feature.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = LibEx.MOD_ID)
public class RegistryHandler
{
    @SubscribeEvent
    public static void onCreateNewRegistry(RegistryEvent.NewRegistry event)
    {
        new RegistryBuilder<BiomeWrapperBuilder>().setName(new ResourceLocation(LibEx.MOD_ID + ":biome_wrapper_builders")).setType(BiomeWrapperBuilder.class).disableOverrides().disableSaving().create();
        new RegistryBuilder<FeatureBuilder>().setName(new ResourceLocation(LibEx.MOD_ID + ":feature_builders")).setType(FeatureBuilder.class).disableOverrides().disableSaving().create();
    }

    @SubscribeEvent
    public static void onRegisterBiomeWrapperBuilders(RegistryEvent.Register<BiomeWrapperBuilder> event)
    {
        event.getRegistry().registerAll(
                new BiomeWrapperOverworld.Builder(),
                new BiomeWrapperNether.Builder(),
                new BiomeWrapperEnd.Builder()
        );
    }

    @SubscribeEvent
    public static void onRegisterFeatureBuilders(RegistryEvent.Register<FeatureBuilder> event)
    {
        event.getRegistry().registerAll(
                new FeatureScatter.Builder(),
                new FeatureCluster.Builder(),
                new FeatureOre.Builder(),
                new FeatureFluid.Builder(),
                new FeaturePool.Builder()
        );
    }
}
