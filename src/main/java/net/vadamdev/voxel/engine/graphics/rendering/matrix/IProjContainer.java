package net.vadamdev.voxel.engine.graphics.rendering.matrix;

import org.joml.Matrix4f;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 14/06/2025
 */
public interface IProjContainer {
    void updateProjMatrix(float fov, float aspectRatio, float zNear, float zFar);

    Matrix4f projectionMatrix();
    FloatBuffer projectionMatrixBuffer();
}
