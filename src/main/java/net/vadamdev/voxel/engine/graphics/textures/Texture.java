package net.vadamdev.voxel.engine.graphics.textures;

import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public class Texture {
    protected final int width, height;
    protected final int[] rgbaPixels;

    protected int textureId;

    public Texture(String path) throws IOException {
        final BufferedImage image = ImageIO.read(Texture.class.getResource(path));

        this.width = image.getWidth();
        this.height = image.getHeight();

        final int[] argbPixels = new int[width * height];
        image.getRGB(0, 0, width, height, argbPixels, 0, width);

        this.rgbaPixels = TextureUtils.argbToRGBA(argbPixels);
    }

    public void create() {
        IntBuffer buffer = null;

        try {
            buffer = MemoryUtil.memAllocInt(rgbaPixels.length);
            buffer.put(rgbaPixels).flip();

            textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);

            initializeTextureParameters();

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            postTextureGeneration();

            glBindTexture(GL_TEXTURE_2D, 0);
        }finally {
            if(buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }

    protected void initializeTextureParameters() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    protected void postTextureGeneration() {}

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getRgbaPixels() {
        return rgbaPixels;
    }
}
