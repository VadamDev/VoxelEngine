package net.vadamdev.voxel.engine.graphics.shaders.exceptions;

import org.lwjgl.opengl.GL20;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class ShaderValidateException extends ShaderException {
    public ShaderValidateException(int programId) {
        super("An error occured while validating program with id " + programId + ": " + GL20.glGetProgramInfoLog(programId));
    }
}
