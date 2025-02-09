package net.vadamdev.voxel.game.world.chunk.data;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public class SingletonLayeredChunkData implements ILayeredChunkData {
    private short blockId;

    public SingletonLayeredChunkData(short blockId) {
        this.blockId = blockId;
    }

    @Override
    public void setBlock(short blockId, int localX, int localZ) {
        this.blockId = blockId;
    }

    @Override
    public short getBlock(int localX, int localZ) {
        return blockId;
    }

    @Override
    public boolean isFullOfSameBlock(short blockId) {
        return this.blockId == blockId;
    }
}
