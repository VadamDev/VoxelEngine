package net.vadamdev.voxel.game.world;

import net.vadamdev.voxel.engine.fastnoise.FastNoiseLite;
import net.vadamdev.voxel.game.rendering.WorldRenderer;
import net.vadamdev.voxel.game.world.chunk.Chunk;
import net.vadamdev.voxel.game.world.chunk.mesh.ChunkMesh;
import net.vadamdev.voxel.game.world.chunk.mesh.ChunkMeshFactory;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class World {
    public static int RENDER_DISTANCE = 12;

    private final WorldRenderer renderer;
    private final ChunkMeshFactory meshFactory;

    private final Map<Vector2i, Chunk> chunks;

    private final FastNoiseLite noise;

    public World(WorldRenderer renderer) {
        this.renderer = renderer;
        this.meshFactory = new ChunkMeshFactory();

        this.chunks = new ConcurrentHashMap<>();

        this.noise = new FastNoiseLite();
        this.noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        this.noise.SetFrequency(0.002f);

        this.noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.noise.SetFractalOctaves(4);
        this.noise.SetFractalGain(0.4f);
    }

    public void update(LocalPlayer player) {
        final Vector3f position = player.position();

        final int pChunkX = (int) Math.floor(position.x() / Chunk.CHUNK_WIDTH);
        final int pChunkZ = (int) Math.floor(position.z() / Chunk.CHUNK_DEPTH);

        final int rdMinX = pChunkX - RENDER_DISTANCE;
        final int rdMaxX = pChunkX + RENDER_DISTANCE;
        final int rdMinZ = pChunkZ - RENDER_DISTANCE;
        final int rdMaxZ = pChunkZ + RENDER_DISTANCE;

        for(int chunkX = rdMinX; chunkX < rdMaxX; chunkX++) {
            for(int chunkZ = rdMinZ; chunkZ < rdMaxZ; chunkZ++) {
                if(chunkAt(chunkX, chunkZ).isPresent())
                    continue;

                final Vector2i chunkPos = new Vector2i(chunkX, chunkZ);

                final Chunk newChunk = new Chunk(this, chunkPos);
                newChunk.generateChunk(noise);

                chunks.put(chunkPos, newChunk);
            }
        }

        for(Map.Entry<Vector2i, Chunk> entry : chunks.entrySet()) {
            final Vector2i chunkPos = entry.getKey();
            final int chunkX = chunkPos.x();
            final int chunkZ = chunkPos.y();

            final Chunk chunk = entry.getValue();

            if(chunkX >= rdMinX && chunkZ >= rdMinZ && chunkX <= rdMaxX && chunkZ <= rdMaxZ)
                chunk.constructMeshAsync();
            else {
                chunks.remove(chunkPos);
                chunk.destroy();
            }
        }
    }

    /*
       Blocks
     */

    public short getBlockAt(double x, double y, double z) {
        final int chunkX = (int) Math.floor(x / Chunk.CHUNK_WIDTH);
        final int chunkZ = (int) Math.floor(z / Chunk.CHUNK_DEPTH);

        final Chunk chunk = chunkAt(chunkX, chunkZ).orElse(null);
        if(chunk == null)
            return 0;

        return chunk.blockAt((int) (x - (chunkX * Chunk.CHUNK_WIDTH)), (int) y, (int) (z - (chunkZ * Chunk.CHUNK_DEPTH)));
    }

    public boolean setBlockAt(short blockId, double x, double y, double z) {
        final int chunkX = (int) Math.floor(x / Chunk.CHUNK_WIDTH);
        final int chunkZ = (int) Math.floor(z / Chunk.CHUNK_DEPTH);

        final Chunk chunk = chunkAt(chunkX, chunkZ).orElse(null);
        if(chunk == null) {
            System.err.println("Invalid block position (x: " + x + ", y: " + y + ", z: " + z + ")");
            return false;
        }

        return chunk.setBlockAt(blockId, (int) (x - (chunkX * Chunk.CHUNK_WIDTH)), (int) y, (int) (z - (chunkZ * Chunk.CHUNK_DEPTH)));
    }

    public int buildAdjacentBlocksBitmap(int x, int y, int z) {
        int adjacentBlocks = 0;

        if(getBlockAt(x + 1, y, z) != 0)
            adjacentBlocks |= ChunkMesh.BITMASK_POS_X;

        if(getBlockAt(x - 1, y, z) != 0)
            adjacentBlocks |= ChunkMesh.BITMASK_NEG_X;

        if(getBlockAt(x, y + 1, z) != 0)
            adjacentBlocks |= ChunkMesh.BITMASK_POS_Y;

        if(getBlockAt(x, y - 1, z) != 0)
            adjacentBlocks |= ChunkMesh.BITMASK_NEG_Y;

        if(getBlockAt(x, y, z + 1) != 0)
            adjacentBlocks |= ChunkMesh.BITMASK_POS_Z;

        if(getBlockAt(x, y, z - 1) != 0)
            adjacentBlocks |= ChunkMesh.BITMASK_NEG_Z;

        return adjacentBlocks;
    }

    /*
       Chunks
     */

    private Optional<Chunk> chunkAt(double x, double z) {
        final int chunkX = (int) Math.floor(x / Chunk.CHUNK_WIDTH);
        final int chunkZ = (int) Math.floor(z / Chunk.CHUNK_DEPTH);

        return chunkAt(chunkX, chunkZ);
    }

    private Optional<Chunk> chunkAt(int chunkX, int chunkZ) {
        return Optional.ofNullable(chunks.get(new Vector2i(chunkX, chunkZ)));
    }

    /*
       Utils
     */

    public ChunkMeshFactory getChunkMeshFactory() {
        return meshFactory;
    }

    public void render() {
        renderer.render(chunks.values());
    }

    public void destroy() {
        meshFactory.destroy();

        chunks.values().forEach(Chunk::destroy);
    }
}
