package net.vadamdev.voxel.engine.graphics.shaders.exceptions;

import org.lwjgl.opengl.GL20;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class ShaderException extends Exception {
    public ShaderException() {}

    public ShaderException(int programId) {
        this("An unexpected error occurred with a shader (programId=" + programId + ")");
    }

    public ShaderException(String message) {
        super(message);
    }

    public static String formatShaderType(int type) {
        return switch(type) {
            case GL20.GL_VERTEX_SHADER -> "GL_VERTEX_SHADER";
            case GL20.GL_FRAGMENT_SHADER -> "GL_FRAGMENT_SHADER";
            default -> "Unknown";
        };
    }
}
