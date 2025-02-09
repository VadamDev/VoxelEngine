package net.vadamdev.voxel.engine.graphics.rendering;

import net.vadamdev.voxel.engine.graphics.rendering.editor.IViewMatrixEditor;
import net.vadamdev.voxel.engine.objects.GameObject;
import net.vadamdev.voxel.engine.objects.Rotatable;
import org.joml.Math;
import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class Camera extends GameObject implements Rotatable {
    private final Vector3f position, rotation;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Camera(Vector3f position) {
        this(position, new Vector3f());
    }

    public Camera() {
        this(new Vector3f(), new Vector3f());
    }

    /*
       View matrix
     */

    public void updateViewMatrix(IViewMatrixEditor viewMatrixEditor) {
        viewMatrixEditor.updateViewMatrix(position, rotation);
    }

    /*
       Getters & Setters
     */

    @Override
    public Vector3f position() {
        return position;
    }

    @Override
    public Vector3f rotation() {
        return rotation;
    }

    public float getYaw() {
        return Math.toRadians(rotation.y() + 180);
    }

    public float getPitch() {
        return Math.toRadians(rotation.x());
    }

    public float getRoll() {
        return Math.toRadians(rotation.z());
    }

    public Vector3f getDirection() {
        final float yaw = getYaw();
        final float pitch = getPitch();

        final Vector3f result = new Vector3f();

        result.y = Math.sin(pitch);

        final double xz = Math.cos(pitch);
        result.x = (float) (-xz * Math.sin(yaw));
        result.z = (float) (xz * Math.cos(yaw));

        return result;
    }
}
