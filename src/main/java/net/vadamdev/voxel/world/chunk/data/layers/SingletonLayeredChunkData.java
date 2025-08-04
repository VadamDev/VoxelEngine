package net.vadamdev.voxel.world.chunk.data.layers;

/**
 * @author VadamDev
 * @since 01/06/2025
 */
public class SingletonLayeredChunkData implements ILayeredChunkData {
    private short blockId;

    public SingletonLayeredChunkData(short blockId) {
        this.blockId = blockId;
    }

    @Override
    public short getBlock(int localX, int localZ) {
        return blockId;
    }

    @Override
    public void setBlock(short blockId, int localX, int localZ) {
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
