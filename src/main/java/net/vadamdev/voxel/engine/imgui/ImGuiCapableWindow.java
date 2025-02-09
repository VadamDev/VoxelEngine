package net.vadamdev.voxel.engine.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.vadamdev.voxel.engine.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class ImGuiCapableWindow extends Window {
    private final ImGuiImplGlfw imguiGLFW;
    private final ImGuiImplGl3 imguiGL3;

    private final List<ImGuiWindow> imGuiWindows;

    public ImGuiCapableWindow(String title, int width, int height) {
        super(title, width, height);

        this.imguiGLFW = new ImGuiImplGlfw();
        this.imguiGL3 = new ImGuiImplGl3();

        this.imGuiWindows = new ArrayList<>();
    }

    @Override
    public void create() {
        super.create();

        ImGui.createContext();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imguiGLFW.init(windowId, true);
        imguiGL3.init("#version 330");
    }

    @Override
    public void swapBuffers() {
        if(!imGuiWindows.isEmpty()) {
            imguiGLFW.newFrame();
            imguiGL3.newFrame();
            ImGui.newFrame();

            imGuiWindows.forEach(ImGuiWindow::render);

            ImGui.render();
            imguiGL3.renderDrawData(ImGui.getDrawData());

            if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowId = GLFW.glfwGetCurrentContext();

                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();

                GLFW.glfwMakeContextCurrent(backupWindowId);
            }
        }

        super.swapBuffers();
    }

    @Override
    public void destroy() {
        imguiGL3.destroyDeviceObjects();
        imguiGLFW.shutdown();

        ImGui.destroyContext();

        super.destroy();
    }

    public void registerImGuiWindow(ImGuiWindow window) {
        imGuiWindows.add(window);
    }

    public boolean removeImGuiWindow(ImGuiWindow window) {
        return imGuiWindows.remove(window);
    }

    public boolean wantCapturePeripherals() {
        final ImGuiIO io = ImGui.getIO();
        return io.getWantCaptureMouse() || io.getWantCaptureKeyboard();
    }
}
