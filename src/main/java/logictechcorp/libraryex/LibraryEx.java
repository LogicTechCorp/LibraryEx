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

import logictechcorp.libraryex.item.crafting.LibraryExRecipeSerializers;
import logictechcorp.libraryex.world.generation.feature.LibraryExFeatures;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LibraryEx.MOD_ID)
public class LibraryEx
{
    public static final String MOD_ID = "libraryex";
    public static final Logger LOGGER = LogManager.getLogger("LibraryEx");

    public LibraryEx()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LibraryExRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        LibraryExFeatures.FEATURES.register(modEventBus);
    }
}
