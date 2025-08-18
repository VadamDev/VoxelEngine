package net.vadamdev.voxel.rendering.terrain.debug;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.rendering.EngineData;
import net.vadamdev.voxel.engine.graphics.shaders.ShaderProgram;
import net.vadamdev.voxel.engine.graphics.shaders.StructAccess;

/**
 * @author VadamDev
 * @since 17/08/2025
 */
public class ChunkOutlineShader extends ShaderProgram {
    public StructAccess<EngineData> engineData;

    public ChunkOutlineShader() {
        super("/assets/shaders/debug/chunk_outline_vert.glsl", "/assets/shaders/debug/chunk_outline_frag.glsl");
    }

    @Override
    protected void setupUniforms() {
        engineData = accessStruct("engineData", () -> VoxelGame.get().getRenderingEngineData());
    }
}
