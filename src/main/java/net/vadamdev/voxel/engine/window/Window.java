package net.vadamdev.voxel.engine.window;

import net.vadamdev.voxel.engine.inputs.InputsManager;
import net.vadamdev.voxel.engine.utils.Callable;
import net.vadamdev.voxel.engine.utils.Disposable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author VadamDev
 * @since 29/05/2025
 */
public class Window implements Disposable {
    private String title;
    private int width, height;

    protected long windowId;

    private boolean resized, grabbed;
    private Callable resizeCallback;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        this.resized = true;
    }

    public void init() {
        //Initializing GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!");

        //Creating the window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        boolean maximized = false;
        if(width == 0 || height == 0) {
            width = 100;
            height = 100;

            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximized = true;
        }

        if((windowId = glfwCreateWindow(width, height, title, 0, 0)) == 0)
            throw new IllegalStateException("Failed to create GLFW window!");

        //Setup callbacks
        setupCallbacks();

        //Centering the window
        if(maximized)
            glfwMaximizeWindow(windowId);
        else {
            final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(windowId, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        //Set the current OpenGL context
        glfwMakeContextCurrent(windowId);
        glfwSwapInterval(0); //TODO: vsync support

        //Show the window
        glfwShowWindow(windowId);

        //Create OpenGL capabilities
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void setupCallbacks() {
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
                resizeCallback.run();

            resized = false;
        }

        glfwPollEvents();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowId);
    }

    @Override
    public void dispose() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
        glfwTerminate();

        final GLFWErrorCallback errorCallback = glfwSetErrorCallback(null);
        if(errorCallback != null)
            errorCallback.free();
    }

    public void onResize(Callable callback) {
        if(resizeCallback == null)
            resizeCallback = callback;
        else
            resizeCallback = resizeCallback.andThen(callback);
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
        this.grabbed = grabbed;
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowId);
    }
}
