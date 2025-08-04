package net.vadamdev.voxel.rendering.terrain.mesh;

import org.jetbrains.annotations.Nullable;

/**
 * @author VadamDev
 * @since 02/06/2025
 */
public record ChunkMeshUnionData(@Nullable ChunkMesh.Data solidMesh, @Nullable ChunkMesh.Data waterMesh) {
    public boolean isEmpty() {
        return solidMesh == null && waterMesh == null;
    }
}
