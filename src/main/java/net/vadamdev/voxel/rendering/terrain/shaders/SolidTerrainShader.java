package net.vadamdev.voxel.rendering.terrain.shaders;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.rendering.EngineData;
import net.vadamdev.voxel.engine.graphics.shaders.*;

/**
 * @author VadamDev
 * @since 02/06/2025
 */
public class SolidTerrainShader extends ShaderProgram {
    public StructAccess<EngineData> engineData;

    public UniformAccess aoIntensity;
    public UniformAccess chunkPos, textureId;

    public SolidTerrainShader() {
        super("/assets/shaders/terrain/solid/solid_terrain_vert.glsl", "/assets/shaders/terrain/solid/solid_terrain_frag.glsl");
    }

    @Override
    protected void setupUniforms() {
        engineData = accessStruct("engineData", () -> VoxelGame.get().getRenderingEngineData());

        aoIntensity = accessUniform("aoIntensity");

        chunkPos = accessUniform("chunkPos");
        textureId = accessUniform("textureId");
    }
}
