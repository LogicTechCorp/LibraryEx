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

package logictechcorp.libraryex.handler;

import logictechcorp.libraryex.LibraryEx;
import logictechcorp.libraryex.world.generation.feature.BiomeDataFeatureWrapper;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LibraryEx.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeatureRegister
{
    @SubscribeEvent
    public static void onFeatureRegister(RegistryEvent.Register<Feature<?>> event)
    {
        event.getRegistry().registerAll(
                new BiomeDataFeatureWrapper(BiomeDataFeatureWrapper.Config::deserialize).setRegistryName("biome_data_feature_wrapper")
        );
    }
}
