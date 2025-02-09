package net.vadamdev.voxel.engine.graphics.shaders;

import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderCompileException;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderLinkException;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderValidateException;
import net.vadamdev.voxel.engine.utils.FileUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class ShaderProgram {
    private final String vertexShaderSource, fragmentShaderSource;

    private int vertexShader, fragmentShader;
    private int programId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) throws IOException {
        this.vertexShaderSource = FileUtils.readFile(vertexShaderPath);
        this.fragmentShaderSource = FileUtils.readFile(fragmentShaderPath);

        this.uniforms = new HashMap<>();
    }

    public void create() throws ShaderException {
        programId = glCreateProgram();

        vertexShader = createShader(vertexShaderSource, GL_VERTEX_SHADER);
        fragmentShader = createShader(fragmentShaderSource, GL_FRAGMENT_SHADER);

        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);

        glLinkProgram(programId);
        if(glGetProgrami(programId, GL_LINK_STATUS) == 0)
            throw new ShaderLinkException(programId);

        glValidateProgram(programId);
        if(glGetProgrami(programId, GL_VALIDATE_STATUS) == 0)
            throw new ShaderValidateException(programId);

        glDetachShader(programId, vertexShader);
        glDetachShader(programId, fragmentShader);
    }

    /*
       Bind / Unbind
     */

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    /*
       Uniforms
     */

    public void createUniform(String name) {
        final int location = glGetUniformLocation(programId, name);
        if(location < 0)
            throw new NullPointerException("Failed to find a uniform with name: " + name);

        uniforms.put(name, location);
    }

    public void setUniform1f(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    public void setUniform1i(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    public void setUniform2f(String name, Vector2f vector) {
        glUniform2f(uniforms.get(name), vector.x(), vector.y());
    }

    public void setUniform3f(String name, Vector3f vector) {
        glUniform3f(uniforms.get(name), vector.x(), vector.y(), vector.z());
    }

    public void setUniform4fv(String name, FloatBuffer matrix) {
        glUniformMatrix4fv(uniforms.get(name), false, matrix);
    }

    public void setUniform4fv(String name, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer buffer = stack.mallocFloat(16);
            glUniformMatrix4fv(uniforms.get(name), false, value.get(buffer));
        }
    }

    /*
       Utility Methods
     */

    public void destroy() {
        unbind();

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glDeleteProgram(programId);
    }

    private int createShader(String source, int type) throws ShaderException {
        final int shaderId = glCreateShader(type);
        if(shaderId == 0)
            throw new ShaderException("An error occurred while creating a shader with type: " + ShaderException.formatShaderType(type) + " (programId=" + programId + ")");

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
            throw new ShaderCompileException(type, shaderId);

        return shaderId;
    }
}
