package net.vadamdev.voxel.world.chunk.data.layers;

/**
 * @author VadamDev
 * @since 01/06/2025
 */
public interface ILayeredChunkData {
    short getBlock(int localX, int localZ);
    void setBlock(short blockId, int localX, int localZ);

    boolean isFullOf(short blockId);

    default boolean isEmpty() {
        return isFullOf((short) 0);
    }
}
