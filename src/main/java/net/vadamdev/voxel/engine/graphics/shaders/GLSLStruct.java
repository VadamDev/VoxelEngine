package net.vadamdev.voxel.engine.graphics.shaders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * @author VadamDev
 * @since 17/07/2025
 */
public interface GLSLStruct {
    void sendToShader(Map<String, IUniformAccess> uniforms);

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @interface Uniform {
        String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @interface Shadow {}
}
