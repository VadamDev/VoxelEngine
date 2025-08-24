package net.vadamdev.voxel.rendering.terrain;

import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.rendering.terrain.ao.AOBlockGroup;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshBase;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshUnion;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshes;
import net.vadamdev.voxel.world.AbstractWorld;
import net.vadamdev.voxel.world.Direction;
import net.vadamdev.voxel.world.blocks.Block;
import net.vadamdev.voxel.world.blocks.Blocks;
import net.vadamdev.voxel.world.blocks.impl.EdgeBlock;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.Map;
import java.util.concurrent.*;

import static net.vadamdev.voxel.world.chunk.Chunk.*;

/**
 * @author VadamDev
 * @since 02/06/2025
 */
public class ChunkMeshFactory {
    private final AbstractWorld world;

    private final Map<Vector3i, Future<?>> meshingTasks;
    private final ThreadPoolExecutor executor;

    public ChunkMeshFactory(AbstractWorld world) {
        this.world = world;

        this.meshingTasks = new ConcurrentHashMap<>();
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    }

    public CompletableFuture<ChunkMeshUnion.Data> constructMeshAsync(Chunk chunk) {
        final Vector3i chunkPos = chunk.position();
        if(isMeshing(chunkPos))
            throw new IllegalStateException("Chunk meshing is already processing");

        final CompletableFuture<ChunkMeshUnion.Data> future = new CompletableFuture<>();

        meshingTasks.put(chunkPos, executor.submit(() -> {
            meshingTasks.remove(chunkPos);
            future.complete(constructMeshSync(chunk));
        }));

        return future;
    }

    public ChunkMeshUnion.Data constructMeshSync(Chunk chunk) {
        final int chunkX = (int) chunk.worldPosition().x();
        final int chunkY = (int) chunk.worldPosition().y();
        final int chunkZ = (int) chunk.worldPosition().z();

        ChunkMeshes.Solid.SolidData solidMeshData = null;
        ChunkMeshBase.Data waterMeshData = null;

        try {
            solidMeshData = new ChunkMeshes.Solid.SolidData();
            waterMeshData = new ChunkMeshBase.Data();

            for(int localX = 0; localX < CHUNK_WIDTH; localX++) {
                for(int localY = 0; localY < CHUNK_HEIGHT; localY++) {
                    for(int localZ = 0; localZ < CHUNK_DEPTH; localZ++) {
                        //discard if air block
                        final short blockId = chunk.getBlock(localX, localY, localZ);
                        if(blockId == 0)
                            continue;

                        final int worldX = chunkX + localX;
                        final int worldY = chunkY + localY;
                        final int worldZ = chunkZ + localZ;

                        //Discard every block surrounded by other blocks
                        final int adjacentBlocks = buildAdjacentBlocksBitmap(blockId, worldX, worldY, worldZ);
                        if(adjacentBlocks == Direction.ALL_DIRECTIONS_MASK)
                            continue;

                        final Block block = Blocks.getBlockNotNull(blockId);
                        final BlockModel model = block.retrieveModel(world, worldX, worldY, worldZ);

                        if(!block.isWater())
                            model.bake(solidMeshData, localX, localY, localZ, adjacentBlocks, AOBlockGroup.collectFaces(world, worldX, worldY, worldZ));
                        else
                            model.bake(waterMeshData,  localX, localY, localZ, adjacentBlocks);
                    }
                }
            }

            //Solid Mesh
            solidMeshData.flip();
            if(solidMeshData.isEmpty()) {
                solidMeshData.free();
                solidMeshData = null;
            }

            //Water mesh
            waterMeshData.flip();
            if(waterMeshData.isEmpty()) {
                waterMeshData.free();
                waterMeshData = null;
            }

            return new ChunkMeshUnion.Data(solidMeshData, waterMeshData);
        }catch(Exception e) {
            if(solidMeshData != null)
                solidMeshData.free();

            if(waterMeshData != null)
                waterMeshData.free();

            throw e;
        }
    }

    private int buildAdjacentBlocksBitmap(short selfBlockId, int x, int y, int z) {
        int adjacentBlocks = 0;

        for(Direction dir : Direction.readValues()) {
            if(!shouldMaskFace(dir, world.getBlock(x + dir.modX(), y + dir.modY(), z + dir.modZ()), selfBlockId))
                continue;

            adjacentBlocks |= dir.bitMask();
        }

        return adjacentBlocks;
    }

    private boolean shouldMaskFace(Direction dir, Block adjacentBlock, short selfBlockId) {
        //A placeholder block replaces blocks outside of chunks, so if the adjacent block is null it can only be air
        if(adjacentBlock == null)
            return false;

        //No meshing chunk borders, avoid world gen chunk borders artifacts
        if(adjacentBlock.blockId() == EdgeBlock.EDGE_BLOCK_ID)
            return !(dir.equals(Direction.UP) || dir.equals(Direction.DOWN));

        //self-explanatory
        if(adjacentBlock.blockId() != selfBlockId && !adjacentBlock.isTransparent())
            return true;

        return adjacentBlock.blockId() == selfBlockId;
    }

    public boolean cancelMeshingTask(Vector3i chunkPos) {
        final Future<?> meshingTask = meshingTasks.get(chunkPos);
        if(meshingTask == null)
            return false;

        if(meshingTask.isDone() || meshingTask.isCancelled())
            return false;

        meshingTask.cancel(false);
        meshingTasks.remove(chunkPos);

        return true;
    }

    public boolean isMeshing(Vector3i chunkPos) {
        return meshingTasks.containsKey(chunkPos);
    }

    public int getActiveMeshingTasks() {
        return meshingTasks.size();
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
