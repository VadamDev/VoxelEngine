package net.vadamdev.voxel.engine.graphics.rendering;

import net.vadamdev.voxel.engine.graphics.rendering.matrix.IProjViewContainer;
import net.vadamdev.voxel.engine.window.Window;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class Camera {
    private final Vector3f position;
    private final Vector2f rotation;

    private final Window window;
    private final IProjViewContainer projViewContainer;

    public float fov = 90;
    public float nearClipPlane = 0.1f, farClipPlane = 8192f;

    public Camera(Vector3f position, Vector2f rotation, Window window, IProjViewContainer projViewContainer) {
        this.position = position;
        this.rotation = rotation;

        this.window = window;
        this.projViewContainer = projViewContainer;

        window.onResize(() -> projViewContainer.updateProjMatrix(fov, window.getAspectRatio(), nearClipPlane, farClipPlane));

        updateProjectionMatrix();
        updateViewMatrix();
    }

    public Camera(Vector3f position, Window window, IProjViewContainer projViewContainer) {
        this(position, new Vector2f(), window, projViewContainer);
    }

    public Camera(Window window, IProjViewContainer projViewContainer) {
        this(new Vector3f(), window, projViewContainer);
    }

    /*
       Matrices
     */

    public void updateViewMatrix() {
        projViewContainer.updateViewMatrix(position, rotation);
    }

    public void updateProjectionMatrix() {
        projViewContainer.updateProjMatrix(fov, window.getAspectRatio(), nearClipPlane, farClipPlane);
    }

    /*
       Getters & Setters
     */

    public Vector3f position() {
        return position;
    }

    public Vector2f rotation() {
        return rotation;
    }

    public float yaw() {
        return Math.toRadians(rotation.y() + 180);
    }

    public float pitch() {
        return Math.toRadians(rotation.x());
    }

    public Vector3f getDirection() {
        final float yaw = yaw();
        final float pitch = pitch();

        final Vector3f result = new Vector3f();

        result.y = Math.sin(pitch);

        final double xz = Math.cos(pitch);
        result.x = (float) (-xz * Math.sin(yaw));
        result.z = (float) (xz * Math.cos(yaw));

        return result;
    }
}
