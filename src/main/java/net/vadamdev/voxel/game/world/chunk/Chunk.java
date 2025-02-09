package net.vadamdev.voxel.game.world.chunk;

import net.vadamdev.voxel.engine.fastnoise.FastNoiseLite;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.objects.GameObject;
import net.vadamdev.voxel.game.world.World;
import net.vadamdev.voxel.game.world.blocks.Blocks;
import net.vadamdev.voxel.game.world.chunk.data.ILayeredChunkData;
import net.vadamdev.voxel.game.world.chunk.data.LayeredChunkData;
import net.vadamdev.voxel.game.world.chunk.data.SingletonLayeredChunkData;
import net.vadamdev.voxel.game.world.chunk.mesh.ChunkMesh;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class Chunk extends GameObject implements Renderable {
    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 128;
    public static final int CHUNK_DEPTH = 32;

    public static final int NUM_BLOCK_IN_CHUNK = CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH;
    public static final int NUM_BLOCK_IN_LAYER = CHUNK_WIDTH * CHUNK_DEPTH;

    private final World world;
    private final Vector3f position;

    private final ChunkMesh mesh;

    private final ILayeredChunkData[] layers;

    public Chunk(World world, Vector2i position) {
        this.world = world;
        this.position = new Vector3f(position.x() * CHUNK_WIDTH, 0, position.y() * CHUNK_DEPTH);

        this.mesh = new ChunkMesh();

        this.layers = new ILayeredChunkData[CHUNK_HEIGHT];
    }

    public void generateChunk(FastNoiseLite noise) {
        Arrays.fill(layers, null);

        for(int x = 0; x < CHUNK_WIDTH; x++) {
            for(int z = 0; z < CHUNK_DEPTH; z++) {
                final float noisyHeight = Math.abs(noise.GetNoise(x + position.x(), z + position.z()));

                int yPos = (int) Math.clamp((noisyHeight * CHUNK_HEIGHT) + 32, 0, CHUNK_HEIGHT - 1);

                retrieveLayer(yPos--, LayeredChunkData::new).setBlock(Blocks.GRASS.getBlockId(), x, z);

                for(int i = 0; i < 3; i++) {
                    if(yPos > 1)
                        retrieveLayer(yPos--, LayeredChunkData::new).setBlock(Blocks.DIRT.getBlockId(), x, z);
                    else
                        break;
                }

                if(yPos >= 0) {
                    for(int y = 0; y <= yPos; y++) {
                        retrieveLayer(y, LayeredChunkData::new).setBlock(Blocks.STONE.getBlockId(), x, z);
                    }
                }
            }
        }

        for(int localY = 0; localY < CHUNK_HEIGHT; localY++)
            compressLayer(localY);
    }

    public void constructMeshAsync() {
        mesh.constructMeshAsync(this);
    }

    public void constructMeshSync() {
        mesh.constructMeshSync(this);
    }

    /*
       Layer Stuff
     */

    private void compressLayer(int localY) {
        final ILayeredChunkData layer = layers[localY];
        if(layer == null)
            return;

        if(layer.isEmpty())
            layers[localY] = null;
        else {
            final short blockId = layer.getBlock(0, 0);

            if(layer.isFullOfSameBlock(blockId) && !(layer instanceof SingletonLayeredChunkData))
                layers[localY] = new SingletonLayeredChunkData(blockId);
        }
    }

    @NotNull
    public ILayeredChunkData retrieveLayer(int localY, Supplier<ILayeredChunkData> fallback) {
        final ILayeredChunkData storedLayer = layers[localY];
        if(storedLayer != null)
            return storedLayer;

        final ILayeredChunkData fallbackLayer = fallback.get();
        layers[localY] = fallbackLayer;

        return fallbackLayer;
    }

    /*
       Get and Set blocks
     */

    public short blockAt(int localX, int localY, int localZ) {
        if(!areCoordinatesValid(localX, localY, localZ))
            return 0;

        final ILayeredChunkData layer = layers[localY];
        if(layer == null)
            return 0;

        return layer.getBlock(localX, localZ);
    }

    public boolean setBlockAt(short blockId, int localX, int localY, int localZ) {
        if(!areCoordinatesValid(localX, localY, localZ))
            return false;

        ILayeredChunkData layer = layers[localY];
        if(layer == null)
            return false;

        if(layer instanceof SingletonLayeredChunkData singletonLayer) {
            final short blockIdInSingleton = singletonLayer.getBlock(0, 0);

            if(blockId != blockIdInSingleton) {
                final ILayeredChunkData newLayer = LayeredChunkData.of(singletonLayer);

                layers[localY] = newLayer;
                layer = newLayer;
            }
        }

        layer.setBlock(blockId, localX, localZ);

        if(layer instanceof LayeredChunkData)
            compressLayer(localY);

        return true;
    }

    private boolean areCoordinatesValid(int localX, int localY, int localZ) {
        if(localX < 0 || localY < 0 || localZ < 0)
            return false;

        return localX < CHUNK_WIDTH && localY < CHUNK_HEIGHT && localZ < CHUNK_DEPTH;
    }

    /*
       Utils
     */

    @Override
    public void render() {
        mesh.render();
    }

    public World getWorld() {
        return world;
    }

    @Override
    public Vector3f position() {
        return position;
    }

    public void destroy() {
        mesh.destroy();
    }

    public boolean isDestroyed() {
        return mesh.isDestroyed();
    }
}
