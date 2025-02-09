package net.vadamdev.voxel.engine.graphics.rendering;

import net.vadamdev.voxel.engine.graphics.rendering.accessor.IFrustumAccessor;
import net.vadamdev.voxel.engine.graphics.rendering.editor.IProjectionMatrixEditor;
import net.vadamdev.voxel.engine.graphics.rendering.editor.IViewMatrixEditor;
import org.joml.FrustumIntersection;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class MatrixDrawer implements IProjectionMatrixEditor, IViewMatrixEditor, IFrustumAccessor {
    //Projection matrix and precomputed buffer
    private final Matrix4f projectionMatrix;
    private final FloatBuffer projMatrixBuffer;

    //Rendering
    private final Matrix4f viewMatrix, modelViewMatrix;

    //View Frustrum
    private final Matrix4f projectionViewMatrix;
    private final FrustumIntersection frustumIntersection;

    public MatrixDrawer() {
        this.projectionMatrix = new Matrix4f();
        this.projMatrixBuffer = MemoryUtil.memAllocFloat(16);

        this.viewMatrix = new Matrix4f();
        this.modelViewMatrix = new Matrix4f();

        this.projectionViewMatrix = new Matrix4f();
        this.frustumIntersection = new FrustumIntersection();
    }

    /*
       Projection / View Matrix
     */

    @Override
    public void updateProjectionMatrix(float fov, float aspectRatio, float zNear, float zFar) {
        projectionMatrix.identity()
                .perspective(Math.toRadians(fov), aspectRatio, zNear, zFar);

        projectionMatrix.get(projMatrixBuffer.clear());

        projectionViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustumIntersection.set(projectionViewMatrix);
    }

    @Override
    public void updateViewMatrix(Vector3f position, Vector3f rotation) {
        viewMatrix.identity()
                .rotateX(Math.toRadians(rotation.x()))
                .rotateY(Math.toRadians(rotation.y()))
                .rotateZ(Math.toRadians(rotation.z()))

                .translate(-position.x(), -position.y(), -position.z());

        projectionViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustumIntersection.set(projectionViewMatrix);
    }

    /*
       Model View Matrix
     */

    public Matrix4f updateModelViewMatrix(Vector3f position) {
        modelViewMatrix.identity()
                .translate(position);

        return new Matrix4f(viewMatrix).mul(modelViewMatrix);
    }

    public FloatBuffer getProjectionMatrix() {
        return projMatrixBuffer;
    }

    /*
       Frustum
     */

    @Override
    public FrustumIntersection getFrustumIntersection() {
        return frustumIntersection;
    }

    public void destroy() {
        MemoryUtil.memFree(projMatrixBuffer);
    }
}
