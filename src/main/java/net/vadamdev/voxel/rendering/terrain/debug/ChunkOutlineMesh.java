package net.vadamdev.voxel.rendering.terrain.debug;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.mesh.InstancedVertexArrayObject;
import net.vadamdev.voxel.rendering.mesh.Wireframes;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author VadamDev
 * @since 15/06/2025
 */
public class ChunkOutlineMesh extends InstancedVertexArrayObject {
    private Set<Vector3i> previousPostions;
    private int pbo;

    public ChunkOutlineMesh() {
        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null, positionsBuffer = null;

        try {
            verticesBuffer = MemoryUtil.memAllocFloat(288);
            indicesBuffer = MemoryUtil.memAllocInt(432);

            positionsBuffer = MemoryUtil.memAllocInt(3).put(new int[3]);
            instanceCount = 1;

            Wireframes.createWireframeBox(0.001f, new Vector3f(), new Vector3f(Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH), verticesBuffer, indicesBuffer);

            createAndBind(indicesBuffer.limit(), Type.ELEMENTS);
            genBuffer(verticesBuffer, 3);
            pbo = genBufferInstanced(positionsBuffer, 3);
            genElementBuffer(indicesBuffer);

            unbind();
            ready();
        }catch(Exception e) {
            VoxelGame.get().getLogger().error("An error occurred while generating chunk outline mesh:", e);
        }finally {
            if(verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);

            if(indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);

            if(positionsBuffer != null)
                MemoryUtil.memFree(positionsBuffer);
        }
    }

    public void updatePostions(Set<Vector3i> positions) {
        if(!areNewPositionsProvided(positions))
            return;

        previousPostions = positions;

        IntBuffer positionsBuffer = null;
        try {
            positionsBuffer = MemoryUtil.memAllocInt(positions.size() * 3);

            for(Vector3i pos : positions)
                positionsBuffer.put(pos.x()).put(pos.y()).put(pos.z());

            instanceCount = positions.size();
            updateBuffer(pbo, positionsBuffer.flip(), GL15.GL_STREAM_DRAW);
        }finally {
            if(positionsBuffer != null)
                MemoryUtil.memFree(positionsBuffer);
        }
    }

    private boolean areNewPositionsProvided(Set<Vector3i> positions) {
        if(previousPostions == null || positions.size() != previousPostions.size())
            return true;

        return !positions.containsAll(previousPostions);
    }
}
