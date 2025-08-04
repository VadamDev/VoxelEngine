package net.vadamdev.voxel.rendering.terrain.mesh;

import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.rendering.terrain.ao.AOBlockGroup;
import net.vadamdev.voxel.world.AbstractWorld;
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
    public static final int
            BITMASK_POS_X = 1,
            BITMASK_NEG_X = 1 << 1,
            BITMASK_POS_Y = 1 << 2,
            BITMASK_NEG_Y = 1 << 3,
            BITMASK_POS_Z = 1 << 4,
            BITMASK_NEG_Z = 1 << 5;

    public static final int BITMASK_ALL = BITMASK_POS_X | BITMASK_NEG_X | BITMASK_POS_Y | BITMASK_NEG_Y | BITMASK_POS_Z | BITMASK_NEG_Z;

    private final AbstractWorld world;

    private final Map<Vector3i, Future<?>> meshingTasks;
    private final ExecutorService executor;

    public ChunkMeshFactory(AbstractWorld world) {
        this.world = world;

        this.meshingTasks = new ConcurrentHashMap<>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<ChunkMeshUnionData> constructMeshAsync(Chunk chunk) {
        final Vector3i chunkPos = chunk.position();
        if(isMeshing(chunkPos))
            throw new IllegalStateException("Chunk meshing is already processing");

        final CompletableFuture<ChunkMeshUnionData> future = new CompletableFuture<>();

        meshingTasks.put(chunkPos, executor.submit(() -> {
            meshingTasks.remove(chunkPos);
            future.complete(constructMeshSync(chunk));
        }));

        return future;
    }

    public ChunkMeshUnionData constructMeshSync(Chunk chunk) {
        final int chunkX = (int) chunk.worldPosition().x();
        final int chunkY = (int) chunk.worldPosition().y();
        final int chunkZ = (int) chunk.worldPosition().z();

        ChunkMesh.Data solidMeshData = null, waterMeshData = null;

        try {
            solidMeshData = new ChunkMesh.Data();
            waterMeshData = new ChunkMesh.Data();

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
                        if(adjacentBlocks == BITMASK_ALL)
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

            return new ChunkMeshUnionData(solidMeshData, waterMeshData);
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

        final Block posX = world.getBlock(x + 1, y, z);
        if(shouldMaskFace(posX, selfBlockId))
            adjacentBlocks |= BITMASK_POS_X;

        final Block negX = world.getBlock(x - 1, y, z);
        if(shouldMaskFace(negX, selfBlockId))
            adjacentBlocks |= BITMASK_NEG_X;

        final Block posY = world.getBlock(x, y + 1, z);
        if(shouldMaskFace(posY, selfBlockId))
            adjacentBlocks |= BITMASK_POS_Y;

        final Block negY = world.getBlock(x, y - 1, z);
        if(shouldMaskFace(negY, selfBlockId))
            adjacentBlocks |= BITMASK_NEG_Y;

        final Block posZ = world.getBlock(x, y, z + 1);
        if(shouldMaskFace(posZ, selfBlockId))
            adjacentBlocks |= BITMASK_POS_Z;

        final Block negZ = world.getBlock(x, y, z - 1);
        if(shouldMaskFace(negZ, selfBlockId))
            adjacentBlocks |= BITMASK_NEG_Z;

        return adjacentBlocks;
    }

    private boolean shouldMaskFace(Block adjacentBlock, short selfBlockId) {
        //A placeholder block replaces blocks outside of chunks, so if the adjacent block is null it can only be air
        if(adjacentBlock == null)
            return false;

        //No meshing chunk borders, avoid world gen chunk borders artifacts
        if(adjacentBlock.blockId() == EdgeBlock.EDGE_BLOCK_ID)
            return true;

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
