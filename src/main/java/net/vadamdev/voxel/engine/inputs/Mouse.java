package net.vadamdev.voxel.engine.inputs;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

/**
 * @author VadamDev
 * @since 19/12/2024
 */
public enum Mouse {
    BUTTON_1      (GLFW.GLFW_MOUSE_BUTTON_1),
    BUTTON_2      (GLFW.GLFW_MOUSE_BUTTON_2),
    BUTTON_3      (GLFW.GLFW_MOUSE_BUTTON_3),
    BUTTON_4      (GLFW.GLFW_MOUSE_BUTTON_4),
    BUTTON_5      (GLFW.GLFW_MOUSE_BUTTON_5),
    BUTTON_6      (GLFW.GLFW_MOUSE_BUTTON_6),
    BUTTON_7      (GLFW.GLFW_MOUSE_BUTTON_7),
    BUTTON_8      (GLFW.GLFW_MOUSE_BUTTON_8),

    BUTTON_LEFT   (BUTTON_1),
    BUTTON_MIDDLE (BUTTON_3),
    BUTTON_RIGHT  (BUTTON_2);

    private final int keyCode;

    Mouse(int keyCode) {
        this.keyCode = keyCode;
    }

    Mouse(Mouse mouseButton) {
        keyCode = mouseButton.keyCode;
    }

    public int keyCode() {
        return keyCode;
    }

    public static boolean isButtonDown(Mouse button) {
        return isButtonDown(button.keyCode);
    }

    public static boolean isButtonDown(int button) {
        return InputsManager.isMouseButtonDown(button);
    }

    public static Vector2d getMousePos() {
        return InputsManager.getMousePos();
    }

    public static double getDX() {
        return InputsManager.getDeltaX();
    }

    public static double getDY() {
        return InputsManager.getDeltaY();
    }

    public static double getScrollY() {
        return InputsManager.getScrollY();
    }
}
