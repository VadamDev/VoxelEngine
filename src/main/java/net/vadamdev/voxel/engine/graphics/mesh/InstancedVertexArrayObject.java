package net.vadamdev.voxel.engine.graphics.mesh;

import java.nio.Buffer;

import static org.lwjgl.opengl.GL33.*;

/**
 * @author VadamDev
 * @since 18/08/2025
 */
public class InstancedVertexArrayObject extends VertexArrayObject {
    public int instanceCount;

    public InstancedVertexArrayObject() {
        this.instanceCount = 0;
    }

    public <T extends Buffer> int genBufferInstanced(T buffer, int size, int divisor, int usage) {
        if(destroyed)
            throw new IllegalStateException("VAO was destroyed");

        if(ready)
            throw new IllegalStateException("VAO was set as ready, you can only modify existing buffers");

        final int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);

        glGenericBufferData(GL_ARRAY_BUFFER, buffer, usage);
        glEnableVertexAttribArray(vertexAttribIndex);
        glVertexAttribPointer(vertexAttribIndex, size, glGenericBufferDataType(false, buffer), false, 0, 0);
        glVertexAttribDivisor(vertexAttribIndex, divisor);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        buffers.put(bufferId, vertexAttribIndex);

        vertexAttribIndex++;
        return bufferId;
    }

    public <T extends Buffer> int genBufferInstanced(T buffer, int size, int divisor) {
        return genBufferInstanced(buffer, size, 1, GL_STATIC_DRAW);
    }

    public <T extends Buffer> int genBufferInstanced(T buffer, int size) {
        return genBufferInstanced(buffer, size, 1);
    }

    @Override
    protected void drawArrays() {
        glDrawArraysInstanced(GL_TRIANGLES, 0, verticesCount, instanceCount);
    }

    @Override
    protected void drawElements() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glDrawElementsInstanced(GL_TRIANGLES, verticesCount, GL_UNSIGNED_INT, 0, instanceCount);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
