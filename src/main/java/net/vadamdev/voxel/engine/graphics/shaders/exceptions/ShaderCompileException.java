package net.vadamdev.voxel.engine.graphics.shaders.exceptions;

import org.lwjgl.opengl.GL20;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class ShaderCompileException extends ShaderException {
    public ShaderCompileException(int type, int shaderId) {
        super("An error occurred while compiling " + formatShaderType(type) + " with id " + shaderId + ": " + GL20.glGetShaderInfoLog(shaderId));
    }
}
