package net.vadamdev.voxel.rendering.terrain.mesh;

import net.vadamdev.voxel.engine.graphics.mesh.NormalsBakery;
import net.vadamdev.voxel.engine.graphics.mesh.VertexArrayObject;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author VadamDev
 * @since 03/07/2025
 */
public class ChunkMesh extends VertexArrayObject {
    private int vbo, tbo, nbo, aobo;

    public ChunkMesh(@NotNull Data meshData) {
        createAndBind(meshData.verticesCount);

        vbo = genBuffer(meshData.verticesBuffer, 3);
        tbo = genBuffer(meshData.textureCoordsBuffer, 2);
        nbo = genBuffer(meshData.normalsBuffer, 3);
        aobo = genBuffer(meshData.aoBuffer, 1);

        unbind();
        ready();
    }

    public void updateBuffers(Data newMeshData) {
        updateBuffer(vbo, newMeshData.verticesBuffer);
        updateBuffer(tbo, newMeshData.textureCoordsBuffer);
        updateBuffer(nbo, newMeshData.normalsBuffer);
        updateBuffer(aobo, newMeshData.aoBuffer);

        verticesCount = newMeshData.verticesCount;
    }

    @Override
    public void dispose() {
        super.dispose();

        vbo = 0;
        tbo = 0;
        nbo = 0;
        aobo = 0;
    }

    public static class Data {
        public final FloatBuffer verticesBuffer, textureCoordsBuffer, normalsBuffer;
        public final IntBuffer aoBuffer;

        public int verticesCount;

        public Data() {
            final int vertexCount = Chunk.NUM_BLOCK_IN_CHUNK * 6 * 6; //6 faces per block * 6 vertices per face TODO: custom model support, this can be WAY higher

            verticesBuffer = MemoryUtil.memAllocFloat(vertexCount * 3); //3 float per vertex
            textureCoordsBuffer = MemoryUtil.memAllocFloat(vertexCount * 2); //2 floats per tex coord
            normalsBuffer = MemoryUtil.memAllocFloat(verticesBuffer.limit());
            aoBuffer = MemoryUtil.memAllocInt(vertexCount); //1 ambiant occlusion value per vertex
        }

        public void flip() {
            this.verticesCount = verticesBuffer.position() / 3;

            verticesBuffer.flip();
            textureCoordsBuffer.flip();
            NormalsBakery.calculateVertexNormals(verticesBuffer, normalsBuffer);
            aoBuffer.flip();
        }

        public void free() {
            if(verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);

            if(textureCoordsBuffer != null)
                MemoryUtil.memFree(textureCoordsBuffer);

            if(normalsBuffer != null)
                MemoryUtil.memFree(normalsBuffer);

            if(aoBuffer != null)
                MemoryUtil.memFree(aoBuffer);
        }

        public boolean isEmpty() {
            return verticesCount == 0;
        }
    }
}
