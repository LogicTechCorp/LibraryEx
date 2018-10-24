package lex.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;

import java.util.List;

interface IBiomeRegistry<T extends IBiomeWrapper>
{
    void addBiome(T wrapper);

    void removeBiome(Biome biome);

    List<BiomeManager.BiomeEntry> getBiomeEntries();

    IBiomeWrapper getBiomeWrapper(Biome biome);

    IBiomeWrapper getModdedBiomeWrapper(Biome biome);

    IBiomeWrapper getPlayerBiomeWrapper(Biome biome);
}
