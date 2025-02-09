package net.vadamdev.voxel.engine.imgui;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public interface ImGuiWindow {
    void begin();
    void draw();
    void end();

    default void render() {
        begin();
        draw();
        end();
    }
}
