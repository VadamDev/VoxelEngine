package net.vadamdev.voxel.engine.window.imgui;

import imgui.ImGui;
import imgui.ImVec2;

/**
 * @author VadamDev
 * @since 29/05/2025
 */
public interface DearImGui {
    void begin();
    void draw();
    void end();

    default void render(ImGuiCapableWindow window) {
        ImGui.setNextWindowViewport(ImGui.getMainViewport().getID());

        begin();
        draw();
        end();
    }
}
