package net.vadamdev.voxel.rendering.terrain.mesh;

import net.vadamdev.voxel.engine.graphics.mesh.NormalsBakery;
import net.vadamdev.voxel.engine.graphics.mesh.VertexArrayObject;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 07/08/2025
 */
public class ChunkMeshBase<T extends ChunkMeshBase.Data> extends VertexArrayObject {
    private int vbo, tbo, nbo;

    public ChunkMeshBase(T meshData) {
        createAndBind(meshData.verticesCount);

        genBuffers(meshData);

        unbind();
        ready();
    }

    protected void genBuffers(T meshData) {
        vbo = genBuffer(meshData.verticesBuffer, 3);
        tbo = genBuffer(meshData.textureCoordsBuffer, 2);
        nbo = genBuffer(meshData.normalsBuffer, 3);
    }

    public void updateBuffers(T newMeshData) {
        updateBuffer(vbo, newMeshData.verticesBuffer);
        updateBuffer(tbo, newMeshData.textureCoordsBuffer);
        updateBuffer(nbo, newMeshData.normalsBuffer);

        verticesCount = newMeshData.verticesCount;
    }

    @Override
    public void dispose() {
        super.dispose();

        vbo = 0;
        tbo = 0;
        nbo = 0;
    }

    public static class Data {
        public static final int MAX_VERTEX_COUNT = Chunk.NUM_BLOCK_IN_CHUNK * 6 * 6; //6 faces per block * 6 vertices per face TODO: custom model support, this can be WAY higher

        public final FloatBuffer verticesBuffer, textureCoordsBuffer, normalsBuffer;

        public int verticesCount;

        public Data() {
            verticesBuffer = MemoryUtil.memAllocFloat(MAX_VERTEX_COUNT * 3); //3 float per vertex
            textureCoordsBuffer = MemoryUtil.memAllocFloat(MAX_VERTEX_COUNT * 2); //2 floats per tex coord
            normalsBuffer = MemoryUtil.memAllocFloat(verticesBuffer.limit());
        }

        public void flip() {
            this.verticesCount = verticesBuffer.position() / 3;

            verticesBuffer.flip();
            textureCoordsBuffer.flip();
            NormalsBakery.calculateVertexNormals(verticesBuffer, normalsBuffer);
        }

        public void free() {
            if(verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);

            if(textureCoordsBuffer != null)
                MemoryUtil.memFree(textureCoordsBuffer);

            if(normalsBuffer != null)
                MemoryUtil.memFree(normalsBuffer);
        }

        public boolean isEmpty() {
            return verticesCount == 0;
        }
    }
}
