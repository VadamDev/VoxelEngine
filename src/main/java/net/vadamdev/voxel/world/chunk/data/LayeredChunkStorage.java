package net.vadamdev.voxel.world.chunk.data;

import net.vadamdev.voxel.world.chunk.Chunk;
import net.vadamdev.voxel.world.chunk.data.layers.ILayeredChunkData;
import net.vadamdev.voxel.world.chunk.data.layers.LayeredChunkData;
import net.vadamdev.voxel.world.chunk.data.layers.SingletonLayeredChunkData;

import java.util.Arrays;

/**
 * @author VadamDev
 * @since 01/06/2025
 */
public class LayeredChunkStorage implements IChunkStorage {
    public static LayeredChunkStorage of(SingletonChunkStorage singletonChunkStorage) {
        final LayeredChunkStorage newStorage = new LayeredChunkStorage();
        Arrays.fill(newStorage.layers, new SingletonLayeredChunkData(singletonChunkStorage.getStored()));

        return newStorage;
    }

    private final ILayeredChunkData[] layers;

    public LayeredChunkStorage() {
        this.layers = new ILayeredChunkData[Chunk.CHUNK_HEIGHT];
    }

    @Override
    public short getBlock(int localX, int localY, int localZ) {
        final ILayeredChunkData layer = layers[localY];
        if(layer == null)
            return 0;

        return layer.getBlock(localX, localZ);
    }

    @Override
    public void setBlock(short blockId, int localX, int localY, int localZ) {
        ILayeredChunkData layer = layers[localY];

        if(layer == null) {
            layer = new LayeredChunkData();
            layers[localY] = layer;
        }else if(layer instanceof SingletonLayeredChunkData singletonLayer && singletonLayer.getStored() != blockId) {
            final LayeredChunkData newLayer = LayeredChunkData.of(singletonLayer);

            layers[localY] = newLayer;
            layer = newLayer;
        }

        layer.setBlock(blockId, localX, localZ);

        //You might want to call tryCompress after big operations on a specific layer
    }

    @Override
    public boolean isFullOf(short blockId) {
        for(ILayeredChunkData layer : layers) {
            if(layer == null) {
                if(blockId == 0)
                    continue;
                else
                    return false;
            }

            if(!layer.isFullOf(blockId))
                return false;
        }

        return true;
    }

    @Override
    public void tryCompress() {
        for(int i = 0; i < layers.length; i++)
            tryCompressLayer(i);
    }

    public void tryCompressLayer(int localY) {
        final ILayeredChunkData layer = layers[localY];
        if(layer == null)
            return;

        if(layer.isEmpty())
            layers[localY] = null;
        else if(!(layer instanceof SingletonLayeredChunkData)) {
            final short blockId = layer.getBlock(0, 0);
            if(!layer.isFullOf(blockId))
                return;

            layers[localY] = new SingletonLayeredChunkData(blockId);
        }
    }
}
