package net.vadamdev.voxel.world.chunk;

import net.vadamdev.voxel.world.chunk.data.IChunkStorage;
import net.vadamdev.voxel.world.chunk.data.LayeredChunkStorage;
import net.vadamdev.voxel.world.chunk.data.SingletonChunkStorage;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 06/06/2025
 */
public class Chunk {
    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 32;
    public static final int CHUNK_DEPTH = 32;

    public static final int NUM_BLOCKS_IN_LAYER = CHUNK_WIDTH * CHUNK_DEPTH;
    public static final int NUM_BLOCK_IN_CHUNK = NUM_BLOCKS_IN_LAYER * CHUNK_HEIGHT;

    public static void checkRange(int localX, int localY, int localZ) {
        if(localX < 0 || localY < 0 || localZ < 0)
            throw new IndexOutOfBoundsException("Local coordinates must be non-negative: (" + localX + ", " + localY + ", " + localZ + ")");

        if(localX >= CHUNK_WIDTH || localY >= CHUNK_HEIGHT || localZ >= CHUNK_DEPTH)
            throw new IndexOutOfBoundsException("Local coordinates out of bounds: (" + localX + ", " + localY + ", " + localZ + ")");
    }

    public static boolean inRange(int localX, int localY, int localZ) {
        try {
            checkRange(localX, localY, localZ);
            return true;
        }catch (IndexOutOfBoundsException ignored) {
            return false;
        }
    }

    protected final Vector3i chunkPos;
    protected final Vector3f worldPos;

    protected IChunkStorage storage;

    public Chunk(Vector3i position) {
        this.chunkPos = position;
        this.worldPos = new Vector3f(chunkPos).mul(CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_DEPTH);

        this.storage = new SingletonChunkStorage();
    }

    /*
       Get & Set blocks
     */

    public short getBlock(int localX, int localY, int localZ) {
        checkRange(localX, localY, localZ);

        return storage.getBlock(localX, localY, localZ);
    }

    public void setBlock(short blockId, int localX, int localY, int localZ) {
        checkRange(localX, localY, localZ);

        if(storage instanceof SingletonChunkStorage singletonStorage && singletonStorage.getStored() != blockId)
            storage = LayeredChunkStorage.of(singletonStorage);

        storage.setBlock(blockId, localX, localY, localZ);
    }

    public void tryCompress() {
        final short blockId = storage.getBlock(0, 0, 0);
        if(storage instanceof LayeredChunkStorage && storage.isFullOf(blockId))
            storage = new SingletonChunkStorage(blockId);
        else
            storage.tryCompress();
    }

    public Vector3i position() {
        return chunkPos;
    }

    public Vector3f worldPosition() {
        return worldPos;
    }
}
