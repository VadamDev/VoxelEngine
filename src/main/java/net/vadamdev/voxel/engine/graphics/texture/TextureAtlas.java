package net.vadamdev.voxel.engine.graphics.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author VadamDev
 * @since 07/06/2025
 */
public class TextureAtlas extends Texture {
    public TextureAtlas(String path) throws IOException {
        super(path);
    }

    public TextureAtlas(BufferedImage image) {
        super(image);
    }

    @Override
    protected void initializeTextureParameters() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 4);
    }

    @Override
    protected void postTextureGeneration() {
        glGenerateMipmap(GL_TEXTURE_2D);
    }
}
