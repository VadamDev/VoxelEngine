package net.vadamdev.voxel.rendering.mesh;

import net.vadamdev.voxel.engine.graphics.mesh.InstancedVertexArrayObject;
import net.vadamdev.voxel.engine.graphics.mesh.VertexArrayObject;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.utils.Disposable;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VadamDev
 * @since 19/08/2025
 */
public class InstancedMesh implements Renderable, Disposable {
    protected final InstancedVertexArrayObject vao;
    protected final int usage;

    protected int obo;

    private Set<Vector3i> currentOffsets;

    public InstancedMesh(int usage) {
        this.vao = new InstancedVertexArrayObject();
        this.usage = usage;
    }

    public InstancedMesh() {
        this(GL15.GL_DYNAMIC_DRAW);
    }

    public void create(FloatBuffer verticesBuffer, IntBuffer indicesBuffer) {
        IntBuffer offsetsBuffer = null;

        try {
            vao.createAndBind(indicesBuffer.limit(), VertexArrayObject.Type.ELEMENTS);
            vao.genBuffer(verticesBuffer, 3);
            vao.genElementBuffer(indicesBuffer);

            offsetsBuffer = MemoryUtil.memAllocInt(3).put(new int[3]).flip();
            obo = vao.genBufferInstanced(offsetsBuffer, 3, 1, usage);

            vao.unbind();
            vao.ready();
        }finally {
            MemoryUtil.memFree(offsetsBuffer);
        }
    }

    public void updateOffsets(Set<Vector3i> offsets) {
        if(!areNewPositionsOffsets(offsets))
            return;

        currentOffsets = new HashSet<>(offsets);

        IntBuffer positionsBuffer = null;
        try {
            positionsBuffer = MemoryUtil.memAllocInt(offsets.size() * 3);

            for(Vector3i pos : offsets)
                positionsBuffer.put(pos.x()).put(pos.y()).put(pos.z());

            vao.instanceCount = offsets.size();
            vao.updateBuffer(obo, positionsBuffer.flip(), usage);
        }finally {
            MemoryUtil.memFree(positionsBuffer);
        }
    }

    private boolean areNewPositionsOffsets(Set<Vector3i> offsets) {
        if(currentOffsets == null || offsets.size() != currentOffsets.size())
            return true;

        return !offsets.containsAll(currentOffsets);
    }

    @Override
    public void render() {
        vao.render();
    }

    @Override
    public void dispose() {
        vao.dispose();
        obo = 0;
    }

    public Set<Vector3i> getCurrentOffsets() {
        return currentOffsets;
    }
}
