package net.vadamdev.voxel.engine.graphics.shaders;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author VadamDev
 * @since 15/07/2025
 */
public class UniformAccess implements IUniformAccess {
    protected final int location;

    public UniformAccess(int location) {
        this.location = location;
    }

    //float

    @Override
    public void set1f(float value) {
        glUniform1f(location, value);
    }

    @Override
    public void set2f(float x, float y) {
        glUniform2f(location, x, y);
    }

    @Override
    public void set2f(Vector2f vector) {
        glUniform2f(location, vector.x(), vector.y());
    }

    @Override
    public void set3f(float x, float y, float z) {
        glUniform3f(location, x, y, z);
    }

    @Override
    public void set3f(Vector3f vector) {
        glUniform3f(location, vector.x(), vector.y(), vector.z());
    }

    @Override
    public void setMatrix4f(Matrix4f matrix) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer buffer = stack.mallocFloat(16);
            glUniformMatrix4fv(location, false, matrix.get(buffer));
        }
    }

    @Override
    public void setMatrix4f(FloatBuffer matrixValues) {
        glUniformMatrix4fv(location, false, matrixValues);
    }

    //int

    @Override
    public void set1i(int value) {
        glUniform1i(location, value);
    }

    @Override
    public void set2i(int x, int y) {
        glUniform2i(location, x, y);
    }

    @Override
    public void set2i(Vector2i vector) {
        glUniform2i(location, vector.x(), vector.y());
    }

    @Override
    public void set3i(int x, int y, int z) {
        glUniform3i(location, x, y, z);
    }

    @Override
    public void set3i(Vector3i vector) {
        glUniform3i(location, vector.x(), vector.y(), vector.z());
    }

    //boolean

    @Override
    public void set(boolean b) {
        glUniform1i(location, b ? 1 : 0);
    }
}
