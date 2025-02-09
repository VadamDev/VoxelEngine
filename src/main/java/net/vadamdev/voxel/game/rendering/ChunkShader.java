package net.vadamdev.voxel.game.rendering;

import net.vadamdev.voxel.engine.graphics.shaders.ShaderProgram;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class ChunkShader extends ShaderProgram {
    public ChunkShader() throws IOException {
        super("/assets/shaders/chunk_vert.glsl", "/assets/shaders/chunk_frag.glsl");
    }

    @Override
    public void create() throws ShaderException {
        super.create();

        createUniform("projectionMatrix");
        createUniform("modelViewMatrix");

        createUniform("texture_sampler");
    }
}
