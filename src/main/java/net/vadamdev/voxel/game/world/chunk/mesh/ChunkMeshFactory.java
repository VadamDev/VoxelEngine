package net.vadamdev.voxel.game.world.chunk.mesh;

import net.vadamdev.voxel.game.world.World;
import net.vadamdev.voxel.game.world.blocks.Blocks;
import net.vadamdev.voxel.game.world.blocks.model.BlockModelFactory;
import net.vadamdev.voxel.game.world.chunk.Chunk;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author VadamDev
 * @since 09/02/2025
 */
public class ChunkMeshFactory {
    private final ThreadPoolExecutor executor;

    public ChunkMeshFactory() {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    }

    public CompletableFuture<ChunkMeshData> constructMeshAsync(Chunk chunk) {
        final CompletableFuture<ChunkMeshData> future = new CompletableFuture<>();
        executor.submit(() -> future.complete(constructMeshSync(chunk)));

        return future;
    }

    public ChunkMeshData constructMeshSync(Chunk chunk) throws Exception {
        final World world = chunk.getWorld();

        final int chunkX = (int) chunk.position().x();
        final int chunkZ = (int) chunk.position().z();

        FloatBuffer verticesBuffer = null, textureCoordsBuffer = null;

        try {
            verticesBuffer = MemoryUtil.memAllocFloat(Chunk.NUM_BLOCK_IN_CHUNK * 3 * 6 * 6);
            int verticesCount = 0;

            textureCoordsBuffer = MemoryUtil.memAllocFloat(Chunk.NUM_BLOCK_IN_CHUNK * 2 * 6 * 6);

            for(int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
                for(int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                    for(int z = 0; z < Chunk.CHUNK_DEPTH; z++) {
                        final short blockId = chunk.blockAt(x, y, z);
                        if(blockId == 0)
                            continue;

                        final int adjacentBlocks = world.buildAdjacentBlocksBitmap(chunkX + x, y, chunkZ + z);
                        if(adjacentBlocks == ChunkMesh.BITMASK_ALL)
                            continue;

                        final BlockModelFactory factory = Blocks.getBlockById(blockId).createModelFactory(x, y ,z).work(adjacentBlocks);

                        verticesBuffer.put(factory.getVertices());
                        verticesCount += factory.getVerticesCount();

                        textureCoordsBuffer.put(factory.getTextureCoords());
                    }
                }
            }

            verticesBuffer.flip();
            textureCoordsBuffer.flip();

            return new ChunkMeshData(verticesBuffer, textureCoordsBuffer, verticesCount);
        }catch(Exception e) {
            if(verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);

            if(textureCoordsBuffer != null)
                MemoryUtil.memFree(textureCoordsBuffer);

            throw e;
        }
    }

    public boolean isIdling() {
        return executor.getActiveCount() == 0;
    }

    public void destroy() {
        executor.shutdown();
    }
}
