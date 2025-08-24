package net.vadamdev.voxel.rendering.terrain.shaders;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.rendering.EngineData;
import net.vadamdev.voxel.engine.graphics.shaders.ShaderProgram;
import net.vadamdev.voxel.engine.graphics.shaders.StructAccess;
import net.vadamdev.voxel.engine.graphics.shaders.UniformAccess;
import org.joml.Vector3f;

import java.awt.*;

/**
 * @author VadamDev
 * @since 19/08/2025
 */
public class ColoredWireframeShader extends ShaderProgram {
    public StructAccess<EngineData> engineData;
    private UniformAccess wireframeColor;

    public ColoredWireframeShader(String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
    }

    public ColoredWireframeShader() {
        this("/assets/shaders/wireframe/default_wireframe_vert.glsl", "/assets/shaders/wireframe/default_wireframe_frag.glsl");
    }

    @Override
    protected void setupUniforms() {
        engineData = accessStruct("engineData", () -> VoxelGame.get().getRenderingEngineData());
        wireframeColor = accessUniform("wireframeColor");
    }

    public void setWireframeColor(Vector3f color) {
        wireframeColor.set3f(color);
    }

    public void setWireframeColor(float r, float g, float b) {
        wireframeColor.set3f(r, g, b);
    }

    public void setWireframeColor(int color) {
        setWireframeColor(((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
    }

    public void setWireframeColor(Color color) {
        setWireframeColor(color.getRGB());
    }
}
