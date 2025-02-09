package net.vadamdev.voxel.engine.window;

import net.vadamdev.voxel.engine.inputs.InputsManager;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class Window {
    private String title;
    private int width, height;

    protected long windowId;
    private final int[] xPos, yPos;

    private boolean resized;
    private Consumer<Window> resizeCallback;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        this.xPos = new int[1];
        this.yPos = new int[1];

        this.resized = true;
    }

    public void create() {
        if(!glfwInit())
            throw new IllegalStateException("An error occurred while initializing GLFW!");

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        //Window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        //Creating the window
        if((windowId = glfwCreateWindow(width, height, title, 0, 0)) == 0) {
            glfwTerminate();
            throw new IllegalStateException("Failed to create a window !");
        }

        //Centering the window
        final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        xPos[0] = (vidMode.width() - width) / 2;
        yPos[0] = (vidMode.height() - height) / 2;
        glfwSetWindowPos(windowId, xPos[0], yPos[0]);

        //Setup Callbacks
        setupCallbacks();

        //Set the current OpenGL context, a swap interval is set to 0 (no vsync! (for now))
        glfwMakeContextCurrent(windowId);
        glfwSwapInterval(0);

        //Create OpenGl capabilities
        GL.createCapabilities();

        //Show the window
        glfwShowWindow(windowId);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    protected void setupCallbacks() {
        glfwSetFramebufferSizeCallback(windowId, (windowId, w, h) -> {
            width = w;
            height = h;

            resized = true;
        });

        InputsManager.createAndRegisterCallbacks(windowId);
    }

    public void update() {
        InputsManager.processMouseDeltas();

        if(resized) {
            GL11.glViewport(0, 0, width, height);

            if(resizeCallback != null)
                resizeCallback.accept(this);

            resized = false;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowId);
    }

    public void destroy() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
        glfwTerminate();

        final GLFWErrorCallback errorCallback = glfwSetErrorCallback(null);
        if(errorCallback != null)
            errorCallback.free();
    }

    public void onResize(Consumer<Window> resizeCallback) {
        if(this.resizeCallback == null)
            this.resizeCallback = resizeCallback;
        else
            this.resizeCallback = this.resizeCallback.andThen(resizeCallback);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowId, title);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return (float) width / height;
    }

    public void setGrabbed(boolean grabbed) {
        glfwSetInputMode(windowId, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    public boolean isGrabbed() {
        return glfwGetInputMode(windowId, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowId);
    }
}
