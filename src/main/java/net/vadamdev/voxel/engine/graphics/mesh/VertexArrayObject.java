package net.vadamdev.voxel.engine.graphics.mesh;

import it.unimi.dsi.fastutil.ints.AbstractInt2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.utils.Disposable;
import org.lwjgl.opengl.GL40;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class VertexArrayObject implements Renderable, Disposable {
    protected int vao;
    public int verticesCount;

    protected final AbstractInt2IntMap buffers;
    protected int vertexAttribIndex;

    protected boolean ready, destroyed;

    public VertexArrayObject() {
        this.vao = 0;
        this.verticesCount = 0;

        this.buffers = new Int2IntOpenHashMap();
        this.vertexAttribIndex = 0;

        this.ready = false;
        this.destroyed = false;
    }

    /*
       Create
     */

    public VertexArrayObject create(int verticesCount) {
        vao = glGenVertexArrays();
        this.verticesCount = verticesCount;

        return this;
    }

    public VertexArrayObject createAndBind(int verticesCount) {
        create(verticesCount);
        bind();

        return this;
    }

    /*
       Bind / Unbind
     */

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    /*
       Buffers
     */

    public <T extends Buffer> int genBuffer(T buffer, int size) {
        if(destroyed)
            throw new IllegalStateException("VAO was destroyed");

        if(ready)
            throw new IllegalStateException("VAO was set as ready, you can only modify existing buffers");

        final int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);

        glGenericBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(vertexAttribIndex);
        glVertexAttribPointer(vertexAttribIndex, size, glGenericBufferType(buffer), false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        buffers.put(bufferId, vertexAttribIndex);

        vertexAttribIndex++;
        return bufferId;
    }

    public <T extends Buffer> void updateBuffer(int bufferId, T buffer) {
        if(destroyed)
            throw new IllegalStateException("VAO was destroyed");

        if(!buffers.containsKey(bufferId))
            throw new NullPointerException("Buffer id " + bufferId + " not found");

        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glGenericBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /*
       Attrib pointers
     */

    public void enableVertexAttribArrays() {
        buffers.forEach((k, v) -> glEnableVertexAttribArray(v));
    }

    public void disableVertexAttribArrays() {
        buffers.forEach((k, v) -> glDisableVertexAttribArray(v));
    }

    /*
       Lock
     */

    public void ready() {
        ready = true;
    }

    /*
       Rendering
     */

    @Override
    public void render() {
        if(destroyed)
            throw new NullPointerException("Failed to render the VAO. It has been flagged as destroyed");

        if(!ready)
            return;

        bind();
        enableVertexAttribArrays();

        glDrawArrays(GL_TRIANGLES, 0, verticesCount);

        disableVertexAttribArrays();
        unbind();
    }

    @Override
    public void dispose() {
        if(destroyed)
            throw new IllegalStateException("VAO is already disposed");

        glBindVertexArray(vao);
        disableVertexAttribArrays();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        buffers.forEach((k, v) -> glDeleteBuffers(k));

        glBindVertexArray(0);
        glDeleteVertexArrays(vao);

        vao = 0;
        buffers.clear();

        ready = false;
        destroyed = true;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isValid() {
        return ready && !destroyed;
    }

    /*
       Static Utility
     */

    public static <T extends Buffer> void glGenericBufferData(int target, T buffer, int usage) {
        switch(buffer) {
            case IntBuffer intBuffer -> glBufferData(target, intBuffer, usage);
            case ByteBuffer byteBuffer -> glBufferData(target, byteBuffer, usage);
            case FloatBuffer floatBuffer -> glBufferData(target, floatBuffer, usage);
            case ShortBuffer shortBuffer -> glBufferData(target, shortBuffer, usage);
            case DoubleBuffer doubleBuffer -> glBufferData(target, doubleBuffer, usage);
            default -> throw new IllegalArgumentException("Unrecognized buffer type: " + buffer.getClass().getName());
        }
    }

    public static <T extends Buffer> int glGenericBufferType(T buffer) {
        return switch(buffer) {
            case IntBuffer ignored -> GL_FLOAT;
            case ByteBuffer ignored -> GL_BYTE;
            case FloatBuffer ignored -> GL_FLOAT;
            case ShortBuffer ignored -> GL_SHORT;
            case DoubleBuffer ignored -> GL_DOUBLE;
            default -> throw new IllegalArgumentException("Unrecognized buffer type: " + buffer.getClass().getName());
        };
    }
}
