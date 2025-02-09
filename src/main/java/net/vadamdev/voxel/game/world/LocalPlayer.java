package net.vadamdev.voxel.game.world;

import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.graphics.rendering.editor.IViewMatrixEditor;
import net.vadamdev.voxel.engine.inputs.Keyboard;
import net.vadamdev.voxel.engine.inputs.Mouse;
import net.vadamdev.voxel.engine.objects.GameObject;
import net.vadamdev.voxel.engine.objects.Rotatable;
import org.joml.Math;
import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class LocalPlayer extends GameObject implements Rotatable {
    private static final float CAMERA_SPEED = 0.15f;
    private static final float CAMERA_SPEED_MULTIPLIER = 3;
    private static final float MOUSE_SENSITIVITY = 0.25f;

    private final Camera camera;
    private final IViewMatrixEditor viewMatrix;

    public LocalPlayer(Camera camera, IViewMatrixEditor viewMatrix) {
        this.camera = camera;
        this.viewMatrix = viewMatrix;
    }

    public void processInputs(float dt) {
        final boolean hasProcessedMouse = processMouse();
        final boolean hasProcessedKeyboard = processKeyboard(dt);

        if(hasProcessedMouse || hasProcessedKeyboard)
            camera.updateViewMatrix(viewMatrix);
    }

    private boolean processMouse() {
        boolean hasProcessed = false;

        final double deltaX = Mouse.getDX();
        final double deltaY = Mouse.getDY();

        final Vector3f rotation = camera.rotation();

        if(deltaY != 0) {
            rotation.x += (float) deltaY * MOUSE_SENSITIVITY;
            hasProcessed = true;
        }

        if(deltaX != 0) {
            rotation.y += (float) deltaX * MOUSE_SENSITIVITY;
            hasProcessed = true;
        }

        if(rotation.x > 90)
            rotation.x = 90;
        else if(rotation.x < -90)
            rotation.x = -90;

        if(rotation.y > 360 || rotation.y < -360)
            rotation.y = 0;

        return hasProcessed;
    }

    private boolean processKeyboard(float dt) {
        float xOffset = 0, yOffset = 0, zOffset = 0;

        if(Keyboard.isKeyDown(Keyboard.KEY_W))
            zOffset -= CAMERA_SPEED;

        if(Keyboard.isKeyDown(Keyboard.KEY_A))
            xOffset -= CAMERA_SPEED;

        if(Keyboard.isKeyDown(Keyboard.KEY_S))
            zOffset += CAMERA_SPEED;

        if(Keyboard.isKeyDown(Keyboard.KEY_D))
            xOffset += CAMERA_SPEED;

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            yOffset += CAMERA_SPEED;

        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT_CONTROL))
            yOffset -= CAMERA_SPEED;

        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT_SHIFT)) {
            xOffset *= CAMERA_SPEED_MULTIPLIER;
            yOffset *= CAMERA_SPEED_MULTIPLIER;
            zOffset *= CAMERA_SPEED_MULTIPLIER;
        }

        if(xOffset != 0 || yOffset != 0 || zOffset != 0) {
            move(xOffset * dt, yOffset * dt, zOffset * dt);
            return true;
        }

        return false;
    }

    private void move(float xOffset, float yOffset, float zOffset) {
        final Vector3f position = camera.position();
        final Vector3f rotation = camera.rotation();

        if(zOffset != 0) {
            position.x += Math.sin(Math.toRadians(rotation.y)) * -1 * zOffset;
            position.z += Math.cos(Math.toRadians(rotation.y)) * zOffset;
        }

        if(xOffset != 0) {
            position.x += Math.sin(Math.toRadians(rotation.y - 90)) * -1 * xOffset;
            position.z += Math.cos(Math.toRadians(rotation.y - 90)) * xOffset;
        }

        position.y += yOffset;
    }

    @Override
    public Vector3f position() {
        return camera.position();
    }

    @Override
    public Vector3f rotation() {
        return camera.rotation();
    }
}
