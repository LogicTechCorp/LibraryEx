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

package lex;

import lex.config.FileConfig;
import lex.config.IConfig;
import lex.proxy.IProxy;
import lex.util.ConfigHelper;
import lex.world.biome.BiomeWrapper;
import lex.world.biome.IBiomeWrapper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = LibEx.MOD_ID, name = LibEx.NAME, version = LibEx.VERSION, dependencies = LibEx.DEPENDENCIES)
public class LibEx
{
    public static final String MOD_ID = "lex";
    public static final String NAME = "LibEx";
    public static final String VERSION = "@MOD_VERSION@";
    public static final String DEPENDENCIES = "required-after:forge@[1.12.2-14.23.0.2491,);";
    private static final String CLIENT_PROXY = "lex.proxy.ClientProxy";
    private static final String SERVER_PROXY = "lex.proxy.ServerProxy";
    public static final boolean IS_DEV_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    public static final File CONFIG_DIRECTORY = Loader.instance().getConfigDir();

    @Mod.Instance(MOD_ID)
    public static LibEx instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static IProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger("LibEx");

    static
    {
        IConfig test = new FileConfig(new File(CONFIG_DIRECTORY, "Lex/test.json"));
        IBiomeWrapper wrapper = new BiomeWrapper(test);
        ConfigHelper.saveConfig(test, new File(CONFIG_DIRECTORY, "Lex/test.json"));

        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event)
    {
        LOGGER.info("PreInitialization started.");
        proxy.preInit();
        LOGGER.info("PreInitialization completed.");
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event)
    {
        LOGGER.info("Initialization started.");
        proxy.init();
        LOGGER.info("Initialization completed.");
    }

    @Mod.EventHandler
    public void onFMLPostInitialization(FMLPostInitializationEvent event)
    {
        LOGGER.info("PostInitialization started.");
        proxy.postInit();
        LOGGER.info("PostInitialization completed.");
    }
}
