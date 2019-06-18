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

import logictechcorp.libraryex.api.LibraryExAPI;
import logictechcorp.libraryex.api.internal.iface.ILibraryExAPI;
import logictechcorp.libraryex.api.world.generation.trait.IBiomeTraitRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(LibraryEx.MOD_ID)
public class LibraryEx implements ILibraryExAPI
{
    public static final String MOD_ID = "libraryex";
    public static final File CONFIG_DIRECTORY = FMLPaths.CONFIGDIR.get().toFile();
    public static final Logger LOGGER = LogManager.getLogger("LibraryEx");

    public LibraryEx()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        LibraryExAPI.setInstance(this);
    }

    @Override
    public boolean isStub()
    {
        return false;
    }

    @Override
    public IBiomeTraitRegistry getBiomeTraitRegistry()
    {
        return BiomeTraitRegistry.INSTANCE;
    }

    public static ResourceLocation getResource(String name)
    {
        return new ResourceLocation(LibraryEx.MOD_ID + ":" + name);
    }
}
