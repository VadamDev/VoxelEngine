package net.vadamdev.voxel.game.world.chunk.mesh;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public record ChunkMeshData(FloatBuffer verticesBuffer, FloatBuffer textureCoordsBuffer, int verticesCount) {
    public void free() {
        if(verticesBuffer != null)
            MemoryUtil.memFree(verticesBuffer);

        if(textureCoordsBuffer != null)
            MemoryUtil.memFree(textureCoordsBuffer);
    }
}
