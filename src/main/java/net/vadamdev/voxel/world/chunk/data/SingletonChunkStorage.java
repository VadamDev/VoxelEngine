package net.vadamdev.voxel.world.chunk.data;

/**
 * @author VadamDev
 * @since 02/06/2025
 */
public class SingletonChunkStorage implements IChunkStorage {
    private short blockId;

    public SingletonChunkStorage(short blockId) {
        this.blockId = blockId;
    }

    public SingletonChunkStorage() {
        this((short) 0);
    }

    @Override
    public short getBlock(int localX, int localY, int localZ) {
        return blockId;
    }

    @Override
    public void setBlock(short blockId, int localX, int localY, int localZ) {
        this.blockId = blockId;
    }

    public short getStored() {
        return blockId;
    }

    @Override
    public boolean isFullOf(short blockId) {
        return this.blockId == blockId;
    }
}
