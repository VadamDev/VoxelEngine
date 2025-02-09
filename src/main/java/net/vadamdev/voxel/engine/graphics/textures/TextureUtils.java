package net.vadamdev.voxel.engine.graphics.textures;

/**
 * @author VadamDev
 * @since 09/01/2025
 */
public final class TextureUtils {
    private TextureUtils() {}

    public static int[] argbToRGBA(int[] argb) {
        final int[] rgba = new int[argb.length];

        for(int i = 0; i < argb.length; i++) {
            final int pixelColor = argb[i];

            final int a = (pixelColor & 0xff000000) >> 24;
            final int r = (pixelColor & 0xff0000) >> 16;
            final int g = (pixelColor & 0xff00) >> 8;
            final int b = (pixelColor & 0xff);

            rgba[i] = a << 24 | b << 16 | g << 8 | r;
        }

        return rgba;
    }
}
