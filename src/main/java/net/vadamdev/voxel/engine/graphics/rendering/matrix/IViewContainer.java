package net.vadamdev.voxel.engine.graphics.rendering.matrix;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 14/06/2025
 */
public interface IViewContainer {
    void updateViewMatrix(Vector3f position, Vector2f rotation);

    Matrix4f viewMatrix();
    FloatBuffer viewMatrixBuffer();
}
