package logictechcorp.libraryex.api;

import logictechcorp.libraryex.world.biome.data.iface.IBiomeDataManager;
import logictechcorp.libraryex.world.biome.data.iface.IBiomeDataRegistry;

public interface IBiomeDataAPI
{
    /**
     * Returns the biome data registry.
     *
     * @return The biome data registry.
     */
    IBiomeDataRegistry getBiomeDataRegistry();

    /**
     * Returns The biome data manager.
     *
     * @return The biome data manager
     */
    IBiomeDataManager getBiomeDataManager();
}
