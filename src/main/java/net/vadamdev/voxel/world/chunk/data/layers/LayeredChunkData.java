package net.vadamdev.voxel.world.chunk.data.layers;

import net.vadamdev.voxel.world.chunk.Chunk;

import java.util.Arrays;

/**
 * @author VadamDev
 * @since 01/06/2025
 */
public class LayeredChunkData implements ILayeredChunkData {
    public static LayeredChunkData of(SingletonLayeredChunkData singletonChunkData) {
        final LayeredChunkData newChunkData = new LayeredChunkData();
        Arrays.fill(newChunkData.blockIds, singletonChunkData.getStored());

        return newChunkData;
    }

    private final short[] blockIds;

    public LayeredChunkData() {
        this.blockIds = new short[Chunk.NUM_BLOCKS_IN_LAYER];
    }

    @Override
    public void setBlock(short blockId, int localX, int localZ) {
        blockIds[getBlockPosition(localX, localZ)] = blockId;
    }

    @Override
    public short getBlock(int localX, int localZ) {
        return blockIds[getBlockPosition(localX, localZ)];
    }

    private int getBlockPosition(int localX, int localZ) {
        return localX + (localZ * Chunk.CHUNK_DEPTH);
    }

    @Override
    public boolean isFullOf(short blockId) {
        for(short bId : blockIds) {
            if(bId != blockId)
                return false;
        }

        return true;
    }
}
