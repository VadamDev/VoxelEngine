package net.vadamdev.voxel.game.world.chunk.data;

import net.vadamdev.voxel.game.world.chunk.Chunk;

import java.util.Arrays;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public class LayeredChunkData implements ILayeredChunkData {
    public static LayeredChunkData of(SingletonLayeredChunkData singletonChunkData) {
        final LayeredChunkData newChunkData = new LayeredChunkData();
        Arrays.fill(newChunkData.blockIds, singletonChunkData.getBlock(0, 0));

        return newChunkData;
    }

    private final short[] blockIds;

    public LayeredChunkData() {
        this.blockIds = new short[Chunk.NUM_BLOCK_IN_LAYER];
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
    public boolean isFullOfSameBlock(short blockId) {
        for(short bId : blockIds) {
            if(bId != blockId)
                return false;
        }

        return true;
    }
}
