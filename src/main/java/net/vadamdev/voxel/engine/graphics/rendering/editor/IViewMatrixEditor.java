package net.vadamdev.voxel.engine.graphics.rendering.editor;

import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public interface IViewMatrixEditor {
    void updateViewMatrix(Vector3f position, Vector3f rotation);
}
