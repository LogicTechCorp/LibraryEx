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

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = LibraryEx.MOD_ID, name = LibraryEx.NAME, version = LibraryEx.VERSION, dependencies = LibraryEx.DEPENDENCIES)
public class LibraryEx
{
    static final String NAME = "LibraryEx";
    static final String VERSION = "1.1.3";
    static final String DEPENDENCIES = "required-after:forge@[1.12.2-14.23.4.2768,);";

    public static final String MOD_ID = "libraryex";
    public static final File CONFIG_DIRECTORY = Loader.instance().getConfigDir();
    public static final boolean IS_DEV_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Mod.Instance(MOD_ID)
    public static LibraryEx instance;

    public static final Logger LOGGER = LogManager.getLogger("LibraryEx");

    public static ResourceLocation getResource(String name)
    {
        return new ResourceLocation(LibraryEx.MOD_ID + ":" + name);
    }
}
