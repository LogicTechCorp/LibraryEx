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
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = LibraryEx.MOD_ID, name = LibraryEx.NAME, version = LibraryEx.VERSION, dependencies = LibraryEx.DEPENDENCIES)
public class LibraryEx implements ILibraryExAPI
{
    static final String NAME = "LibraryEx";
    static final String VERSION = "1.0.9";
    static final String DEPENDENCIES = "required-after:forge@[1.12.2-14.23.4.2768,);";

    public static final String MOD_ID = "libraryex";
    public static final File CONFIG_DIRECTORY = Loader.instance().getConfigDir();
    public static final boolean IS_DEV_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Mod.Instance(MOD_ID)
    public static LibraryEx instance;

    public static final Logger LOGGER = LogManager.getLogger("LibraryEx");

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event)
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
