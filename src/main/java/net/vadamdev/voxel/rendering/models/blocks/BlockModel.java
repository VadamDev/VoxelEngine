package net.vadamdev.voxel.rendering.models.blocks;

import net.vadamdev.voxel.rendering.terrain.ao.AOBlockGroup;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshBase;

import java.util.List;

/**
 * @author VadamDev
 * @since 03/06/2025
 */
public class BlockModel {
    private final List<VoxelFactory> voxels;

    public BlockModel(List<VoxelFactory> voxels) {
        if(voxels.isEmpty())
            throw new IllegalArgumentException("Voxel factory list must contains at least one voxel factory");

        this.voxels = voxels;
    }

    public void bake(ChunkMeshBase.Data meshData, int dX, int dY, int dZ, int adjacentBlocks, AOBlockGroup.Face[] aoGroups) {
        for(VoxelFactory voxel : voxels)
            voxel.bakeVerticesToWorld(meshData, dX, dY, dZ, adjacentBlocks, aoGroups);
    }

    public void bake(ChunkMeshBase.Data meshData, int dX, int dY, int dZ, int adjacentBlocks) {
        bake(meshData, dX, dY, dZ, adjacentBlocks, AOBlockGroup.EMPTY);
    }
}
