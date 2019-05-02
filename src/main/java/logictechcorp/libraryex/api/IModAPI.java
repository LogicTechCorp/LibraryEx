package logictechcorp.libraryex.api;

public interface IModAPI
{
    /**
     * Returns false if the actual mod is loaded.
     *
     * @return Whether this API instance is created by the mod.
     */
    boolean isStub();
}
