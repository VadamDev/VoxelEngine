package net.vadamdev.voxel.engine.graphics.texture;

import java.awt.image.BufferedImage;

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

    public static void copyPixelsToImage(BufferedImage from, BufferedImage to, int xOffset, int yOffset) {
        for(int x = 0; x < from.getWidth(); x++) {
            for(int y = 0; y < from.getHeight(); y++) {
                to.setRGB(x + xOffset, y + yOffset, from.getRGB(x, y));
            }
        }
    }

    public static void fill(BufferedImage image, int color) {
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, color);
            }
        }
    }

    public static void writeGrid(BufferedImage image, int gridSize, int gridColor) {
        //Rows
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                if(y % gridSize != 0)
                    continue;

                image.setRGB(x, y, gridColor);
            }
        }

        //Columns
        for(int x = 0; x < image.getWidth(); x++) {
            if(x % gridSize != 0)
                continue;

            for(int y = 0; y < image.getHeight(); y++)
                image.setRGB(x, y, gridColor);
        }
    }
}
