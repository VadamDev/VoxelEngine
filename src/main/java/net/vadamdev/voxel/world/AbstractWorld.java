package net.vadamdev.voxel.world;

import net.vadamdev.voxel.engine.math.MathHelper;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.world.blocks.Block;
import net.vadamdev.voxel.world.blocks.Blocks;
import net.vadamdev.voxel.world.blocks.impl.EdgeBlock;
import net.vadamdev.voxel.world.chunk.Chunk;
import net.vadamdev.voxel.world.raycast.Raycast;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static net.vadamdev.voxel.world.chunk.Chunk.*;

/**
 * @author VadamDev
 * @since 06/06/2025
 */
public abstract class AbstractWorld implements Disposable {
    protected final Map<Vector3i, Chunk> chunks;

    public AbstractWorld() {
        this.chunks = new ConcurrentHashMap<>();
    }

    protected void updateChunks() {
        chunks.values().forEach(Chunk::update);
    }

    @Override
    public void dispose() {
        chunks.values().forEach(Chunk::dispose);
    }

    public List<Chunk> getChunkColumnAt(int chunkX, int chunkZ, boolean ordered) {
        final Stream<Chunk> stream = this.chunks.entrySet().parallelStream()
                .filter(entry -> entry.getKey().x() == chunkX && entry.getKey().z() == chunkZ)
                .map(Map.Entry::getValue);

        if(ordered)
            return stream.sorted(Comparator.comparingInt(chunk -> chunk.position().y())).toList();

        return stream.toList();
    }

    public int findHighestBlockAt(double x, double z) {
        final int chunkX = MathHelper.floorDiv(x, CHUNK_WIDTH);
        final int chunkZ = MathHelper.floorDiv(z, CHUNK_DEPTH);

        final int blockX = (int) Math.floor(x - chunkX * CHUNK_WIDTH);
        final int blockZ = (int) Math.floor(z - chunkZ * CHUNK_DEPTH);

        int highestBlock = 0;
        for(Chunk chunk : getChunkColumnAt(chunkX, chunkZ, true)) {
            final int worldY = chunk.position().y() * CHUNK_HEIGHT;

            for(int localY = 0; localY < CHUNK_HEIGHT; localY++) {
                short blockId = chunk.getBlock(blockX, localY, blockZ);
                if(blockId == 0)
                    continue;

                highestBlock = localY + worldY;
            }
        }

        return highestBlock;
    }

    public Raycast newRay(float stepSize, double maxRange) {
        return new Raycast(this, stepSize, maxRange);
    }

    /*
       Get & Set blocks
     */

    public short getBlockId(double x, double y, double z) {
        final Chunk chunk = getChunk(x, y, z);
        if(chunk == null)
            return EdgeBlock.EDGE_BLOCK_ID; //Not generated chunk --> Likely world edge

        final Vector3f chunkWorldPos = chunk.worldPosition();
        return chunk.getBlock((int) Math.floor(x - chunkWorldPos.x()), (int) Math.floor(y - chunkWorldPos.y()), (int) Math.floor(z - chunkWorldPos.z()));
    }

    @Nullable
    public Block getBlock(double x, double y, double z) {
        final short blockId = getBlockId(x, y, z);
        return blockId != 0 ? Blocks.getBlockById(blockId) : null;
    }

    public void setBlock(short blockId, double x, double y, double z) {
        final Chunk chunk = getChunk(x, y, z);
        if(chunk == null) {
            System.err.println("Failed to set block! Position (x: " + x + ", y: " + y + ", z: " + z + ") is in a non-existant chunk!");
            return;
        }

        final Vector3f chunkWorldPos = chunk.worldPosition();
        chunk.setBlock(blockId, (int) Math.floor(x - chunkWorldPos.x()), (int) Math.floor(y - chunkWorldPos.y()), (int) Math.floor(z - chunkWorldPos.z()));
    }

    public void setBlock(Block block, double x, double y, double z) {
        setBlock(block.blockId(), x, y, z);
    }

    /*
       Get Chunks
     */

    @Nullable
    public Chunk getChunk(Vector3i chunkPos) {
        return chunks.get(chunkPos);
    }

    @Nullable
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return getChunk(new Vector3i(chunkX, chunkY, chunkZ));
    }

    @Nullable
    public Chunk getChunk(double x, double y, double z) {
        return getChunk(toChunkPos(x, y, z));
    }

    public List<Chunk> getNearbyChunks(double x, double y, double z, boolean addSelf) {
        final Vector3i chunkPos = toChunkPos(x, y, z);
        final List<Chunk> result = new ArrayList<>();

        if(addSelf) {
            final Chunk chunk = getChunk(chunkPos);
            if(chunk != null)
                result.add(chunk);
        }

        for(Direction dir : Direction.readValues()) {
            Vector3i newChunkPos = toChunkPos(x + dir.modX(), y + dir.modY(), z + dir.modZ());
            if(newChunkPos.equals(chunkPos))
                continue;

            final Chunk chunk = getChunk(newChunkPos);
            if(chunk == null)
                continue;

            result.add(chunk);
        }

        return result;
    }

    /*
       Remove Chunks
     */

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk.position());
        chunk.dispose();
    }

    public void removeChunk(Vector3i chunkPos) {
        removeChunk(getChunk(chunkPos));
    }

    public static Vector3i toChunkPos(double x, double y, double z) {
        return new Vector3i(
                MathHelper.floorDiv(x, CHUNK_WIDTH),
                MathHelper.floorDiv(y, CHUNK_HEIGHT),
                MathHelper.floorDiv(z, CHUNK_DEPTH)
        );
    }
}
