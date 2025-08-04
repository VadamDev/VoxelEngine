package net.vadamdev.voxel.engine.graphics.rendering.matrix;

import org.joml.Matrix4f;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 14/06/2025
 */
public interface IProjViewContainer extends IProjContainer, IViewContainer {
    Matrix4f projViewMatrix();
    FloatBuffer projViewMatrixBuffer();
}
