package net.vadamdev.voxel.game.world.chunk.data;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public interface ILayeredChunkData {
    void setBlock(short blockId, int localX, int localZ);
    short getBlock(int localX, int localZ);

    boolean isFullOfSameBlock(short blockId);

    default boolean isEmpty() {
        return isFullOfSameBlock((short) 0);
    }
}
