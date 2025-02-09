package net.vadamdev.voxel.engine.objects;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public interface Scalable {
    default float scale() {
        return 1f;
    }
}
