package net.vadamdev.voxel.rendering.terrain;

import net.vadamdev.voxel.engine.graphics.mesh.VertexArrayObject;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.rendering.models.blocks.VoxelFactory;
import net.vadamdev.voxel.rendering.terrain.ao.AOBlockGroup;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshes;
import net.vadamdev.voxel.rendering.terrain.texture.FaceUVs;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 15/06/2025
 */
public class ChunkDebugBorder implements Renderable, Disposable {
    private ChunkMeshes.Solid.Data meshData;
    private VertexArrayObject vao;

    public ChunkDebugBorder() {
        final VoxelFactory outlineMesh = new VoxelFactory(
                new Vector3i(0, 0, 0),
                new Vector3i(8, 8, 8),
                null,
                0,
                FaceUVs.newVoxelUVs()
        );

        meshData = new ChunkMeshes.Solid.Data();

        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
            outlineMesh.bakeVerticesToWorld(meshData, x, 0,              0,             0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, x, Chunk.CHUNK_HEIGHT, 0,             0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, x, 0,              Chunk.CHUNK_DEPTH, 0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, x, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH, 0, AOBlockGroup.EMPTY);
        }

        for(int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
            outlineMesh.bakeVerticesToWorld(meshData, 0,             y, 0,             0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, Chunk.CHUNK_WIDTH, y, 0,             0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, 0,             y, Chunk.CHUNK_DEPTH, 0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, Chunk.CHUNK_WIDTH, y, Chunk.CHUNK_DEPTH, 0, AOBlockGroup.EMPTY);
        }

        for(int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
            outlineMesh.bakeVerticesToWorld(meshData, 0,             0,              z, 0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, Chunk.CHUNK_WIDTH, 0,              z, 0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, 0,             Chunk.CHUNK_HEIGHT, z, 0, AOBlockGroup.EMPTY);
            outlineMesh.bakeVerticesToWorld(meshData, Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, z, 0, AOBlockGroup.EMPTY);
        }

        meshData.flip();
    }

    public void create() {
        try {
            vao = new VertexArrayObject().createAndBind(meshData.verticesCount);
            vao.genBuffer(meshData.verticesBuffer, 3);
            vao.genBuffer(meshData.textureCoordsBuffer, 2);

            vao.ready();
            vao.unbind();
        }finally {
            meshData.free();
            meshData = null;
        }
    }

    @Override
    public void render() {
        vao.render();
    }

    @Override
    public void dispose() {
        vao.dispose();
    }
}
