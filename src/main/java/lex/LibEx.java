package lex;

import lex.proxy.IProxy;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LibEx.MOD_ID, name = LibEx.NAME, version = LibEx.VERSION)
public class LibEx
{
    public static final String MOD_ID = "lex";
    public static final String NAME = "LibEx";
    public static final String VERSION = "@MOD_VERSION@";
    private static final String CLIENT_PROXY = "lex.proxy.ClientProxy";
    private static final String SERVER_PROXY = "lex.proxy.ServerProxy";
    public static final boolean IS_DEV_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Mod.Instance(MOD_ID)
    public static LibEx instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static IProxy proxy;

    private static final Logger LOGGER = LogManager.getLogger("LibEx|Main");

    static
    {
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
