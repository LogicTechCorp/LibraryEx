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

package logictechcorp.libraryex.item.crafting;

import logictechcorp.libraryex.LibraryEx;
import logictechcorp.libraryex.utility.InjectionHelper;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(LibraryEx.MOD_ID)
@Mod.EventBusSubscriber(modid = LibraryEx.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LibraryExRecipeSerializers
{
    public static final IRecipeSerializer<RepairRecipe> CRAFTING_REPAIR = InjectionHelper.nullValue();

    @SubscribeEvent
    public static void onRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        registerRecipeSerializer("crafting_repair", new RepairRecipe.Serializer());
    }

    private static void registerRecipeSerializer(String name, IRecipeSerializer<?> feature)
    {
        ForgeRegistries.RECIPE_SERIALIZERS.register(feature.setRegistryName(GameData.checkPrefix(name, true)));
    }
}
