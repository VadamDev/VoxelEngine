package net.vadamdev.voxel.engine.graphics.shaders;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 17/07/2025
 */
public class StructAccess<T extends GLSLStruct> {
    private final Map<String, IUniformAccess> uniforms;
    private T struct;

    public StructAccess(Map<String, IUniformAccess> uniforms, @NotNull T defaultValue) {
        this.uniforms = uniforms;
        this.struct = defaultValue;
    }

    public void set(@NotNull T struct) {
        this.struct = struct;
        set();
    }

    public void editAndSet(Consumer<T> editor) {
        editor.accept(struct);
        set();
    }

    public void set() {
        struct.sendToShader(uniforms);
    }

    public T get() {
        return struct;
    }
}
