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

package logictechcorp.libraryex.event;

import logictechcorp.libraryex.event.world.ChunkGenerateEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Random;

public class LibExEventFactory
{
    public static void onChunkGenerate(Chunk chunk)
    {
        ChunkGenerateEvent event = new ChunkGenerateEvent(chunk);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onPreDecorateBiome(World world, Random rand, ChunkPos chunkPos)
    {
        DecorateBiomeEvent.Pre event = new DecorateBiomeEvent.Pre(world, rand, chunkPos);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
    }

    public static boolean onDecorateBiome(World world, Random rand, ChunkPos chunkPos, BlockPos blockPos, DecorateBiomeEvent.Decorate.EventType type)
    {
        DecorateBiomeEvent.Decorate event = new DecorateBiomeEvent.Decorate(world, rand, chunkPos, blockPos, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }

    public static void onPostDecorateBiome(World world, Random rand, ChunkPos chunkPos)
    {
        DecorateBiomeEvent.Post event = new DecorateBiomeEvent.Post(world, rand, chunkPos);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
    }

    public static void onPreOreGen(World world, Random rand, BlockPos blockPos)
    {
        OreGenEvent.Pre event = new OreGenEvent.Pre(world, rand, blockPos);
        MinecraftForge.ORE_GEN_BUS.post(event);
    }

    public static boolean onOreGen(World world, Random rand, WorldGenerator generator, BlockPos blockPos, OreGenEvent.GenerateMinable.EventType type)
    {
        OreGenEvent.GenerateMinable event = new OreGenEvent.GenerateMinable(world, rand, generator, blockPos, type);
        MinecraftForge.ORE_GEN_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }

    public static void onPostOreGen(World world, Random rand, BlockPos blockPos)
    {
        OreGenEvent.Post event = new OreGenEvent.Post(world, rand, blockPos);
        MinecraftForge.ORE_GEN_BUS.post(event);
    }
}
