package net.vadamdev.voxel.controller;

import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.inputs.Keyboard;
import net.vadamdev.voxel.engine.inputs.Mouse;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class PlayerController {
    public static final float DEFAULT_FOV = 90;
    public static final float NEAR_CLIP_PLANE = 0.1f;
    public static final float FAR_CLIP_PLANE = 4096;

    private static final float CAMERA_SPEED = 15;
    private static final float CAMERA_SPRINT_MULTIPLIER = 5;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Camera camera;

    public PlayerController(Camera camera) {
        this.camera = camera;

        camera.fov = DEFAULT_FOV;
        camera.nearClipPlane = NEAR_CLIP_PLANE;
        camera.farClipPlane = FAR_CLIP_PLANE;
    }

    public void processInputs(float deltaTime) {
        final boolean processedMouse = processMouse(), processedKeyboard = processKeyboard(deltaTime);

        if(processedMouse || processedKeyboard)
            camera.updateViewMatrix();
    }

    private boolean processMouse() {
        boolean hasProcessed = false;

        final double deltaX = Mouse.getDX();
        final double deltaY = Mouse.getDY();

        final Vector2f rotation = camera.rotation();

        if(deltaX != 0) {
            rotation.y += (float) deltaX * MOUSE_SENSITIVITY;
            hasProcessed = true;
        }

        if(deltaY != 0) {
            rotation.x += (float) deltaY * MOUSE_SENSITIVITY;
            hasProcessed = true;
        }

        if(hasProcessed) {
            if(rotation.x() > 90)
                rotation.x = 90;
            else if(rotation.x() < -90)
                rotation.x = -90;

            if(rotation.y() > 360)
                rotation.y = 0;
            else if(rotation.y() < 0)
                rotation.y = 360;

            return true;
        }

        return false;
    }

    private boolean processKeyboard(float deltaTime) {
        float xOffset = 0,  yOffset = 0, zOffset = 0;

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
            xOffset *= CAMERA_SPRINT_MULTIPLIER;
            yOffset *= CAMERA_SPRINT_MULTIPLIER;
            zOffset *= CAMERA_SPRINT_MULTIPLIER;
        }

        if(xOffset != 0 || yOffset != 0 || zOffset != 0) {
            move(xOffset * deltaTime, yOffset * deltaTime, zOffset * deltaTime);
            return true;
        }

        return false;
    }

    private void move(float xOffset, float yOffset, float zOffset) {
        final Vector3f position = camera.position();
        final Vector2f rotation = camera.rotation();

        if(zOffset != 0) {
            position.x += -Math.sin(Math.toRadians(rotation.y)) * zOffset;
            position.z += Math.cos(Math.toRadians(rotation.y)) * zOffset;
        }

        if(xOffset != 0) {
            position.x += -Math.sin(Math.toRadians(rotation.y - 90)) * xOffset;
            position.z += Math.cos(Math.toRadians(rotation.y - 90)) * xOffset;
        }

        position.y += yOffset;
    }
}
