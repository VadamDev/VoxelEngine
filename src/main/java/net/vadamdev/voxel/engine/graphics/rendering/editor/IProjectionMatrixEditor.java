package net.vadamdev.voxel.engine.graphics.rendering.editor;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public interface IProjectionMatrixEditor {
    void updateProjectionMatrix(float fov, float aspectRatio, float zNear, float zFar);
}
