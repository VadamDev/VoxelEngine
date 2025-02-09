package net.vadamdev.voxel.engine.graphics.shaders.exceptions;

import org.lwjgl.opengl.GL20;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class ShaderLinkException extends ShaderException {
    public ShaderLinkException(int programId) {
        super("An error occurred while linking program with id " + programId + ": " + GL20.glGetProgramInfoLog(programId));
    }
}
