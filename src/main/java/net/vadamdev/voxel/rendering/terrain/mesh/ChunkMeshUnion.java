package net.vadamdev.voxel.rendering.terrain.mesh;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class ChunkMeshUnion implements Disposable {
    private static final ChunkMeshFactory MESH_FACTORY = VoxelGame.get().getChunkMeshFactory();

    private final Vector3i chunkPos, worldPosition;

    private ChunkMesh solidMesh, waterMesh;
    public volatile boolean canBuildMesh = true;

    public ChunkMeshUnion(Vector3i chunkPos) {
        this.chunkPos = chunkPos;
        worldPosition = new Vector3i(chunkPos).mul(Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH);
    }

    public void constructMeshAsync(Chunk chunk) {
        if(!canBuildMesh)
            return;

        canBuildMesh = false;
        MESH_FACTORY.constructMeshAsync(chunk).whenComplete((result, throwable) -> {
            if(result.isEmpty())
                VoxelGame.get().getWorldRenderer().removeChunk(chunkPos);
            else
                VoxelGame.runOnMainThread(() -> {
                    createOrUpdate(result);
                    canBuildMesh = true;
                });
        });
    }

    public void constructMeshSync(Chunk chunk) {
        if(!canBuildMesh)
            return;

        canBuildMesh = false;
        createOrUpdate(MESH_FACTORY.constructMeshSync(chunk));
        canBuildMesh = true;
    }

    private void createOrUpdate(ChunkMeshUnionData meshData) {
        final ChunkMesh.Data solidMeshData = meshData.solidMesh();
        final ChunkMesh.Data waterMeshData = meshData.waterMesh();

        if(solidMeshData != null) {
            try {
                if(solidMesh == null || solidMesh.isDestroyed())
                    solidMesh = new ChunkMesh(solidMeshData);
                else
                    solidMesh.updateBuffers(solidMeshData);
            }finally {
                solidMeshData.free();
            }
        }else if(solidMesh != null) {
            solidMesh.dispose();
            solidMesh = null;
        }

        if(waterMeshData != null) {
            try {
                if(waterMesh == null || waterMesh.isDestroyed())
                    waterMesh = new ChunkMesh(waterMeshData);
                else
                    waterMesh.updateBuffers(waterMeshData);
            }finally {
                waterMeshData.free();
            }
        }else if(waterMesh != null) {
            waterMesh.dispose();
            waterMesh = null;
        }
    }

    @Override
    public void dispose() {
        if(solidMesh != null && !solidMesh.isDestroyed())
            solidMesh.dispose();

        if(waterMesh != null && !waterMesh.isDestroyed())
            waterMesh.dispose();
    }

    public Vector3i position() {
        return chunkPos;
    }

    public Vector3i worldPosition() {
        return worldPosition;
    }

    @Nullable
    public Renderable solidMesh() {
        return solidMesh;
    }

    @Nullable
    public Renderable waterMesh() {
        return waterMesh;
    }

    public boolean isEmpty() {
        return solidMesh == null && waterMesh == null;
    }
}
