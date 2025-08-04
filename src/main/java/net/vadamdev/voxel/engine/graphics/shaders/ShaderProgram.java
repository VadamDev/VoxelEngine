package net.vadamdev.voxel.engine.graphics.shaders;

import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderCompileException;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderLinkException;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderValidateException;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.engine.utils.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author VadamDev
 * @since 15/07/2025
 */
public abstract class ShaderProgram implements Disposable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

    protected final String vertexShaderPath, fragmentShaderPath;

    protected int vertexShader, fragmentShader;
    protected int programId;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
    }

    public void create() throws IOException, ShaderException, NullPointerException {
        programId = glCreateProgram();

        vertexShader = createShader(FileUtils.readFile(vertexShaderPath), GL_VERTEX_SHADER);
        fragmentShader = createShader(FileUtils.readFile(fragmentShaderPath), GL_FRAGMENT_SHADER);

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

        setupUniforms();
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
       Uniform
     */

    protected abstract void setupUniforms();

    @Nullable
    public UniformAccess accessUniform(String name) {
        final int location = glGetUniformLocation(programId, name);
        if(location < 0)
            return null;

        return new UniformAccess(location);
    }

    @Nullable
    public <T extends GLSLStruct> StructAccess<T> accessStruct(String name, Supplier<T> defaultValue) {
        final T value = defaultValue.get();

        final Map<String, IUniformAccess> uniforms = new HashMap<>();
        for(Field field : value.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(GLSLStruct.Shadow.class))
                continue;

            String fieldName = field.getName();

            final GLSLStruct.Uniform parameters = field.getAnnotation(GLSLStruct.Uniform.class);
            if(parameters != null)
                fieldName = parameters.name();

            final String location = name + "." + fieldName;
            IUniformAccess access = accessUniform(location);
            if(access == null) {
                LOGGER.warn("Failed to find uniform \"" + location + "\" in glsl struct: " + value.getClass().getSimpleName());
                access = IUniformAccess.EMPTY;
            }

            uniforms.put(fieldName, access);
        }

        if(uniforms.isEmpty())
            return null;

        return new StructAccess<>(uniforms, value);
    }

    /*
       Cleanup
     */

    @Override
    public void dispose() {
        unbind();

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glDeleteProgram(programId);
    }
}
