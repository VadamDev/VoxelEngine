package net.vadamdev.voxel.rendering.terrain.shaders;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.rendering.EngineData;
import net.vadamdev.voxel.engine.graphics.shaders.*;

import java.util.Map;

/**
 * @author VadamDev
 * @since 12/07/2025
 */
public class WaterTerrainShader extends ShaderProgram {
    public StructAccess<EngineData> engineData;
    public StructAccess<WaveEffect> waveEffect;

    public UniformAccess chunkPos, textureId;

    public WaterTerrainShader() {
        super("/assets/shaders/terrain/water/water_terrain_vert.glsl", "/assets/shaders/terrain/water/water_terrain_frag.glsl");
    }

    @Override
    protected void setupUniforms() {
        engineData = accessStruct("engineData", () -> VoxelGame.get().getRenderingEngineData());
        waveEffect = accessStruct("waveEffect", WaveEffect::new);

        chunkPos = accessUniform("chunkPos");
        textureId = accessUniform("textureId");
    }

    public static final class WaveEffect implements GLSLStruct {
        public boolean enabled = true;
        public float maxDistance = 64, strength = 0.15f, falloff = 1.5f;

        private WaveEffect() {}

        @Override
        public void sendToShader(Map<String, IUniformAccess> uniforms) {
            uniforms.get("enabled").set(enabled);
            uniforms.get("maxDistance").set1f(maxDistance);
            uniforms.get("strength").set1f(strength);
            uniforms.get("falloff").set1f(falloff);
        }
    }
}
