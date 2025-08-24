package net.vadamdev.voxel.engine.window.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.vadamdev.voxel.engine.window.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VadamDev
 * @since 29/05/2025
 */
public class ImGuiCapableWindow extends Window {
    private static final int MOUSE_DISABLE_MASK = ImGuiConfigFlags.NoMouse | ImGuiConfigFlags.NoMouseCursorChange;

    private final ImGuiImplGlfw imguiGLFW;
    private final ImGuiImplGl3 imguiGL3;

    private final List<DearImGui> windows;

    public ImGuiCapableWindow(String title, int width, int height) {
        super(title, width, height);

        this.imguiGLFW = new ImGuiImplGlfw();
        this.imguiGL3 = new ImGuiImplGl3();

        this.windows = new ArrayList<>();
    }

    @Override
    public void init() {
        super.init();

        ImGui.createContext();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imguiGLFW.init(windowId, true);
        imguiGL3.init("#version 330");
    }

    @Override
    public void swapBuffers() {
        renderImGui();
        super.swapBuffers();
    }

    private void renderImGui() {
        if(windows.isEmpty())
            return;

        imguiGLFW.newFrame();
        imguiGL3.newFrame();
        ImGui.newFrame();

        final ImGuiIO io = ImGui.getIO();
        final boolean isMouseDisabled = io.hasConfigFlags(MOUSE_DISABLE_MASK);

        if(isGrabbed() && !isMouseDisabled)
            io.addConfigFlags(MOUSE_DISABLE_MASK);
        else if(!isGrabbed() && isMouseDisabled)
            io.removeConfigFlags(MOUSE_DISABLE_MASK);

        windows.forEach(imgui -> imgui.render(this));

        ImGui.render();
        imguiGL3.renderDrawData(ImGui.getDrawData());

        if(io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
        }
    }

    @Override
    public void dispose() {
        imguiGL3.destroyDeviceObjects();
        imguiGLFW.shutdown();

        ImGui.destroyContext();

        super.dispose();
    }

    public void registerImGui(DearImGui window) {
        windows.add(window);
    }

    public void removeImGui(DearImGui window) {
        windows.remove(window);
    }

    public boolean wantCapturePeripherals() {
        final ImGuiIO io = ImGui.getIO();
        return io.getWantCaptureMouse() || io.getWantCaptureKeyboard();
    }
}
