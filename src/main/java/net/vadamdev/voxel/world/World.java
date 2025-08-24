package net.vadamdev.voxel.world;

import net.vadamdev.voxel.engine.fastnoise.FastNoiseLite;
import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.rendering.terrain.WorldRenderer;
import net.vadamdev.voxel.world.chunk.Chunk;
import net.vadamdev.voxel.world.chunk.ChunkColumn;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;

import static net.vadamdev.voxel.world.chunk.Chunk.CHUNK_DEPTH;
import static net.vadamdev.voxel.world.chunk.Chunk.CHUNK_WIDTH;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class World extends AbstractWorld {
    public static int RENDER_DISTANCE = 1;
    public static final int WORLD_HEIGHT = 4;

    public final FastNoiseLite noise;

    private Camera camera;
    private WorldRenderer worldRenderer;

    private int previousChunkX = 0, previousChunkZ = 0;
    private boolean initGen = true;

    public World() {
        this.noise = new FastNoiseLite();
        this.noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        this.noise.SetFrequency(0.008f);

        this.noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.noise.SetFractalOctaves(2);
        this.noise.SetFractalLacunarity(1.1f);
        this.noise.SetFractalGain(0.07f);
    }

    public void init(Camera camera, WorldRenderer worldRenderer) {
        this.camera = camera;
        this.worldRenderer = worldRenderer;
    }

    public void update() {
        generateChunksAroundCamera();
    }

    private void generateChunksAroundCamera() {
        final Vector3f cameraPos = camera.position();
        final int pChunkX = (int) Math.floor(cameraPos.x() / CHUNK_WIDTH);
        final int pChunkZ = (int) Math.floor(cameraPos.z() / CHUNK_DEPTH);

        final int moveDeltaX = pChunkX - previousChunkX;
        final int moveDeltaZ = pChunkZ - previousChunkZ;
        if(moveDeltaX == 0 && moveDeltaZ == 0 && !initGen)
            return;

        //Adding one to center the world around the player
        final int rdMinX = pChunkX - RENDER_DISTANCE + 1;
        final int rdMaxX = pChunkX + RENDER_DISTANCE;

        final int rdMinZ = pChunkZ - RENDER_DISTANCE + 1;
        final int rdMaxZ = pChunkZ + RENDER_DISTANCE;

        for(int chunkX = rdMinX; chunkX < rdMaxX; chunkX++) {
            for(int chunkZ = rdMinZ; chunkZ < rdMaxZ; chunkZ++) {
                if(getChunk(chunkX, 0, chunkZ) != null)
                    continue;

                final ChunkColumn column = new ChunkColumn(chunkX, chunkZ, WORLD_HEIGHT);
                column.generate(noise);

                for(Chunk generatedChunk : column.getChunks()) {
                    chunks.put(generatedChunk.position(), generatedChunk);
                    worldRenderer.addChunk(generatedChunk);
                }

                if(moveDeltaX != 0)
                    getChunkColumnAt(chunkX - moveDeltaX, chunkZ, false)
                            .forEach(chunk -> worldRenderer.updateChunk(chunk));

                if(moveDeltaZ != 0)
                    getChunkColumnAt(chunkX, chunkZ - moveDeltaZ, false)
                            .forEach(chunk -> worldRenderer.updateChunk(chunk));

                if(moveDeltaX != 0 && moveDeltaZ != 0)
                    getChunkColumnAt(chunkX - moveDeltaX, chunkZ - moveDeltaZ, false)
                            .forEach(chunk -> worldRenderer.updateChunk(chunk));
            }
        }

        for(Map.Entry<Vector3i, Chunk> entry : chunks.entrySet()) {
            final Vector3i chunkPos = entry.getKey();
            final int chunkX = chunkPos.x();
            final int chunkZ = chunkPos.z();

            if(chunkX >= rdMaxX || chunkX < rdMinX || chunkZ >= rdMaxZ || chunkZ < rdMinZ) {
                final Chunk removedChunk = entry.getValue();
                removeChunk(removedChunk);

                worldRenderer.removeChunk(chunkPos);
            }
        }

        previousChunkX = pChunkX;
        previousChunkZ = pChunkZ;

        if(initGen) {
            initGen = false;

            final Vector3f position = camera.position();
            position.y = findHighestBlockAt(position.x(), position.z()) + 2.5f;

            camera.updateViewMatrix();
        }
    }
}
