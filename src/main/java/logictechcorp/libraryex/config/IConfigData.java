package logictechcorp.libraryex.config;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.File;

/**
 * An interface designed to unify various things
 * that can be serialized to and from a config file.
 */
public interface IConfigData
{
    FileConfig serialize(File configFile);

    void deserialize(FileConfig config);
}
