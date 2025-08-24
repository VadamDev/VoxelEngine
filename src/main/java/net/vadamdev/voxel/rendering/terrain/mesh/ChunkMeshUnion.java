package net.vadamdev.voxel.rendering.terrain.mesh;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.engine.utils.Pointer;
import net.vadamdev.voxel.rendering.terrain.ChunkMeshFactory;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.function.Function;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class ChunkMeshUnion implements Disposable {
    private static final ChunkMeshFactory MESH_FACTORY = VoxelGame.get().getChunkMeshFactory();

    //Position
    private final Vector3i chunkPos, worldPosition;

    //Meshes
    private final Pointer<ChunkMeshes.Solid> solidMeshPtr;
    private final Pointer<ChunkMeshes.Water> waterMeshPtr;

    private volatile boolean canBuildMesh;

    public ChunkMeshUnion(Vector3i chunkPos) {
        this.chunkPos = chunkPos;
        this.worldPosition = new Vector3i(chunkPos).mul(Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH);

        this.solidMeshPtr = Pointer.empty(ChunkMeshes.Solid.class);
        this.waterMeshPtr = Pointer.empty(ChunkMeshes.Water.class);

        this.canBuildMesh = true;
    }

    public void constructMeshAsync(Chunk chunk) {
        if(!canBuildMesh)
            return;

        canBuildMesh = false;
        MESH_FACTORY.constructMeshAsync(chunk).whenComplete((result, throwable) -> VoxelGame.runOnMainThread(() -> {
            if(result.isEmpty())
                VoxelGame.get().getWorldRenderer().removeChunk(chunkPos);
            else
                createOrUpdateMeshes(result);

            canBuildMesh = true;
        }));
    }

    public void constructMeshSync(Chunk chunk) {
        if(!canBuildMesh)
            return;

        canBuildMesh = false;
        createOrUpdateMeshes(MESH_FACTORY.constructMeshSync(chunk));
        canBuildMesh = true;
    }

    private void createOrUpdateMeshes(Data unionData) {
        createOrUpdateMesh(unionData.solidMeshData(), solidMeshPtr, ChunkMeshes.Solid::new);
        createOrUpdateMesh(unionData.waterMeshData(), waterMeshPtr, ChunkMeshes.Water::new);
    }

    @Override
    public void dispose() {
        disposeMesh(solidMeshPtr);
        disposeMesh(waterMeshPtr);
    }

    //Position
    public Vector3i position() {
        return chunkPos;
    }

    public Vector3i worldPosition() {
        return worldPosition;
    }

    //Meshes
    @Nullable
    public Renderable solidMesh() {
        return solidMeshPtr.get();
    }

    @Nullable
    public Renderable waterMesh() {
        return waterMeshPtr.get();
    }

    //Utils
    public boolean isEmpty() {
        return solidMeshPtr.isEmpty() && waterMeshPtr.isEmpty();
    }

    /*
       Data
     */

    public record Data(@Nullable ChunkMeshes.Solid.SolidData solidMeshData, @Nullable ChunkMeshBase.Data waterMeshData) {
        public boolean isEmpty() {
            return (solidMeshData == null || solidMeshData.isEmpty()) && (waterMeshData == null || waterMeshData.isEmpty());
        }
    }

    /*
       Utils
     */

    private static <T extends ChunkMeshBase<U>, U extends ChunkMeshBase.Data> void createOrUpdateMesh(@Nullable U meshData, Pointer<T> meshPtr, Function<U, T> newMeshSupplier) {
        if(meshData != null) {
            try {
                final ChunkMeshBase<U> mesh = meshPtr.get();

                if(mesh == null || mesh.isDestroyed())
                    meshPtr.set(newMeshSupplier.apply(meshData));
                else
                    mesh.updateBuffers(meshData);
            }finally {
                meshData.free();
            }
        }else if(!meshPtr.isEmpty()) {
            meshPtr.get().dispose();
            meshPtr.free();
        }
    }

    private static <T extends ChunkMeshBase<?>> void disposeMesh(Pointer<T> meshPtr) {
        final T mesh = meshPtr.get();
        if(mesh == null || mesh.isDestroyed())
            return;

        mesh.dispose();
    }
}
