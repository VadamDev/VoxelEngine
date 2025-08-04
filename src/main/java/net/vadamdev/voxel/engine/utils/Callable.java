package net.vadamdev.voxel.engine.utils;

/**
 * @author VadamDev
 * @since 14/06/2025
 */
@FunctionalInterface
public interface Callable extends Runnable {
    default Callable andThen(Callable after) {
        return () -> { run(); after.run(); };
    }
}
