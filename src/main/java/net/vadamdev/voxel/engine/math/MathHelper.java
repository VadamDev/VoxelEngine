package net.vadamdev.voxel.engine.math;

/**
 * @author VadamDev
 * @since 24/06/2025
 */
public final class MathHelper {
    private MathHelper() {}

    public static int floorDiv(double a, int b) {
        return (int) Math.floor(a / b);
    }

    public static int floorDiv(int a, double b) {
        return (int) Math.floor(a / b);
    }

    public static int floorDiv(double a, double b) {
        return (int) Math.floor(a / b);
    }
}
