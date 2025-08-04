package net.vadamdev.voxel.world.chunk.data;

/**
 * @author VadamDev
 * @since 01/06/2025
 */
public interface IChunkStorage {
    short getBlock(int localX, int localY, int localZ);
    void setBlock(short blockId, int localX, int localY, int localZ);

    boolean isFullOf(short blockId);

    default boolean isEmpty() {
        return isFullOf((short) 0);
    }

    default void tryCompress() {}
}
