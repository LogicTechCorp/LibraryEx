package lex.world.biome;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.File;

public interface ISerializableBiomeWrapper
{
    FileConfig serialize();

    void deserialize(FileConfig config);

    void reset();

    File getSaveFile();
}
