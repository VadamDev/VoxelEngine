package net.vadamdev.voxel.engine.graphics.rendering;

import net.vadamdev.voxel.engine.graphics.rendering.matrix.IFrustumAccessor;
import net.vadamdev.voxel.engine.graphics.rendering.matrix.IProjViewContainer;
import net.vadamdev.voxel.engine.utils.Disposable;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 03/06/2025
 */
public class MatrixDrawer implements IProjViewContainer, IFrustumAccessor, Disposable {
    //Projection matrix
    private final Matrix4f projMatrix;
    private final FloatBuffer projMatrixBuffer;

    //Rendering
    private final Matrix4f viewMatrix;
    private final FloatBuffer viewMatrixBuffer;

    private final Matrix4f projViewMatrix;
    private final FloatBuffer projViewMatrixBuffer;

    //View Frustum
    private final FrustumIntersection frustumIntersection;

    public MatrixDrawer() {
        this.projMatrix = new Matrix4f();
        this.projMatrixBuffer = MemoryUtil.memAllocFloat(16);

        this.viewMatrix = new Matrix4f();
        this.viewMatrixBuffer = MemoryUtil.memAllocFloat(16);

        this.projViewMatrix = new Matrix4f();
        this.projViewMatrixBuffer = MemoryUtil.memAllocFloat(16);

        this.frustumIntersection = new FrustumIntersection();
    }

    /*
       Proj Matrix
     */

    @Override
    public void updateProjMatrix(float fov, float aspectRatio, float zNear, float zFar) {
        projMatrix.identity().perspective(Math.toRadians(fov), aspectRatio, zNear, zFar);
        projMatrix.get(projMatrixBuffer.clear());

        updateViewFrustum();
    }

    @Override
    public Matrix4f projectionMatrix() {
        return projMatrix;
    }

    @Override
    public FloatBuffer projectionMatrixBuffer() {
        return projMatrixBuffer;
    }

    /*
       View Matrix
     */

    @Override
    public void updateViewMatrix(Vector3f position, Vector2f rotation) {
        viewMatrix.identity()
                .rotateX(Math.toRadians(rotation.x()))
                .rotateY(Math.toRadians(rotation.y()))
                .translate(-position.x(), -position.y(), -position.z());

        viewMatrix.get(viewMatrixBuffer.clear());
        updateViewFrustum();
    }

    @Override
    public Matrix4f viewMatrix() {
        return viewMatrix;
    }

    @Override
    public FloatBuffer viewMatrixBuffer() {
        return viewMatrixBuffer;
    }

    /*
       Proj View Matrix
     */

    @Override
    public Matrix4f projViewMatrix() {
        return projViewMatrix;
    }

    @Override
    public FloatBuffer projViewMatrixBuffer() {
        return projViewMatrixBuffer;
    }

    /*
       View Frustum
     */

    private void updateViewFrustum() {
        projViewMatrix.set(projMatrix).mul(viewMatrix);
        frustumIntersection.set(projViewMatrix);

        projViewMatrix.get(projViewMatrixBuffer.clear());
    }

    @Override
    public FrustumIntersection frustumIntersection() {
        return frustumIntersection;
    }

    /*
       Dispose
     */

    @Override
    public void dispose() {
        MemoryUtil.memFree(projMatrixBuffer);
        MemoryUtil.memFree(viewMatrixBuffer);
        MemoryUtil.memFree(projViewMatrixBuffer);
    }
}
