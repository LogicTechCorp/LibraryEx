package logictechcorp.libraryex.event.world;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * ChunkGenerateEvent is fired when a chunk is generated.<br>
 * This event is fired during chunk loading in <br>
 * {@link ChunkProviderServer#provideChunk(int, int)}, <br>
 * Chunk.provideChunk(). <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkGenerateEvent extends ChunkEvent
{
    public ChunkGenerateEvent(Chunk chunk)
    {
        super(chunk);
    }
}
