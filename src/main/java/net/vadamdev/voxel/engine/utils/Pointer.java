package net.vadamdev.voxel.engine.utils;

import org.jetbrains.annotations.Nullable;

/**
 * @author VadamDev
 * @since 16/08/2025
 */
public class Pointer<T> {
    public static <T> Pointer<T> of(@Nullable T t) {
        return new Pointer<>(t);
    }

    public static <T> Pointer<T> empty(Class<T> type) {
        return new Pointer<>();
    }

    @Nullable
    private T t;

    protected Pointer(@Nullable T t) {
        this.t = t;
    }

    protected Pointer() {
        this(null);
    }

    @Nullable
    public T get() {
        return t;
    }

    public void set(@Nullable T t) {
        this.t = t;
    }

    public void free() {
        set(null);
    }

    public boolean isEmpty() {
        return t == null;
    }
}
