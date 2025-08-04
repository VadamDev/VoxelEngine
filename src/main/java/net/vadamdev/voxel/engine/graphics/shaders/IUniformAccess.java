package net.vadamdev.voxel.engine.graphics.shaders;

import org.joml.*;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 29/07/2025
 */
public interface IUniformAccess {
    //float

    void set1f(float value);
    void set2f(float x, float y);
    void set2f(Vector2f vector);
    void set3f(float x, float y, float z);
    void set3f(Vector3f vector);

    void setMatrix4f(Matrix4f matrix);
    void setMatrix4f(FloatBuffer matrixValues);

    //int

    void set1i(int value);
    void set2i(int x, int y);
    void set2i(Vector2i vector);
    void set3i(int x, int y, int z);
    void set3i(Vector3i vector);

    //boolean

    void set(boolean b);

    IUniformAccess EMPTY = new IUniformAccess() {
        @Override
        public void set1f(float value) {}

        @Override
        public void set2f(float x, float y) {}

        @Override
        public void set2f(Vector2f vector) {}

        @Override
        public void set3f(float x, float y, float z) {}

        @Override
        public void set3f(Vector3f vector) {}

        @Override
        public void setMatrix4f(Matrix4f matrix) {}

        @Override
        public void setMatrix4f(FloatBuffer matrixValues) {}

        @Override
        public void set1i(int value) {}

        @Override
        public void set2i(int x, int y) {}

        @Override
        public void set2i(Vector2i vector) {}

        @Override
        public void set3i(int x, int y, int z) {}

        @Override
        public void set3i(Vector3i vector) {}

        @Override
        public void set(boolean b) {}
    };
}
