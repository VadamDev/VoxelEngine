package net.vadamdev.voxel.engine.inputs;

import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public final class InputsManager {
    private InputsManager() {}

    public static boolean REGISTERED = false;

    private static final boolean[] keyboardKeys;
    private static final boolean[] mouseButtons;

    private static final Vector2d mousePos, oldMousePos;
    private static double deltaX, deltaY;

    private static double scrollY;

    static {
        keyboardKeys = new boolean[GLFW_KEY_LAST];
        mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];

        mousePos = new Vector2d();
        oldMousePos = new Vector2d();
    }

    /*
       Keyboard
     */

    static boolean isKeyDown(int key) {
        return keyboardKeys[key];
    }

    /*
       Mouse
     */

    static boolean isMouseButtonDown(int button) {
        return mouseButtons[button];
    }

    static Vector2d getMousePos() {
        return mousePos;
    }

    static double getDeltaX() {
        return deltaX;
    }

    static double getDeltaY() {
        return deltaY;
    }

    static double getScrollY() {
        return scrollY;
    }

    /*
       Callbacks Registration
     */

    public static void createAndRegisterCallbacks(long windowId) {
        if(REGISTERED)
            return;

        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
            if(key < 0 || key >= GLFW_KEY_LAST)
                return;

            keyboardKeys[key] = action != GLFW_RELEASE;
        });

        glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> {
            if(button < 0 || button >= GLFW_MOUSE_BUTTON_LAST)
                return;

            mouseButtons[button] = action != GLFW_RELEASE;
        });

        glfwSetScrollCallback(windowId, (window, xOffset, yOffset) -> {
            //Ignore xOffset for now
            scrollY += yOffset;
        });

        glfwSetCursorPosCallback(windowId, (window, xPos, yPos) -> {
            mousePos.x = xPos;
            mousePos.y = yPos;
        });

        REGISTERED = true;
    }

    public static void processMouseDeltas() {
        if(!REGISTERED)
            return;

        deltaX = mousePos.x - oldMousePos.x;
        deltaY = mousePos.y - oldMousePos.y;

        oldMousePos.x = mousePos.x;
        oldMousePos.y = mousePos.y;
    }
}
