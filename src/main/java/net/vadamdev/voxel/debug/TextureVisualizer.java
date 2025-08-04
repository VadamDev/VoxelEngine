package net.vadamdev.voxel.debug;

import imgui.ImGui;
import net.vadamdev.voxel.engine.graphics.texture.Texture;
import net.vadamdev.voxel.engine.window.imgui.DearImGui;

import static imgui.ImGui.*;

/**
 * @author VadamDev
 * @since 08/06/2025
 */
public class TextureVisualizer implements DearImGui {
    private final Texture texture;

    public TextureVisualizer(Texture texture) {
        this.texture = texture;

        initParameters();
    }

    private static final float MIN_MULTIPLIER = 1, MAX_MULTIPLIER = 5;
    private float[] sizeMultiplier;

    private void initParameters() {
        sizeMultiplier = new float[] { 3.75f };
    }

    private void validateParameters() {
        if(sizeMultiplier[0] < MIN_MULTIPLIER)
            sizeMultiplier[0] = MIN_MULTIPLIER;
        else if(sizeMultiplier[0] > MAX_MULTIPLIER)
            sizeMultiplier[0] = MAX_MULTIPLIER;
    }

    @Override
    public void begin() {
        ImGui.begin("Texture Visualizer");
    }

    @Override
    public void draw() {
        dragFloat("Size Multiplier", sizeMultiplier, 0.05f, MIN_MULTIPLIER, MAX_MULTIPLIER);

        validateParameters();

        newLine();
        image(texture.getTextureId(), texture.getWidth() * sizeMultiplier[0], texture.getHeight() * sizeMultiplier[0]);
    }

    @Override
    public void end() {
        ImGui.end();
    }

    @Override
    public void render() {
        if(texture == null)
            return;

        DearImGui.super.render();
    }
}
