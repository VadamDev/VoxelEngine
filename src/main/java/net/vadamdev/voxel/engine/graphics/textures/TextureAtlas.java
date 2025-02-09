package net.vadamdev.voxel.engine.graphics.textures;

import java.io.IOException;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public class TextureAtlas extends Texture {
    private final int subTextureWidth, subTextureHeight;
    private final int subTextureWidthDiv, subTextureHeightDiv;

    public TextureAtlas(String path, int subTextureWidth, int subTextureHeight) throws IOException {
        super(path);

        if(subTextureWidth > width)
            throw new IllegalArgumentException("subTextureWidth cannot be greater than texture width");

        if(subTextureHeight > height)
            throw new IllegalArgumentException("subTextureHeight cannot be greater than texture height");

        this.subTextureWidth = subTextureWidth;
        this.subTextureHeight = subTextureHeight;

        this.subTextureWidthDiv = subTextureWidth / width / 2;
        this.subTextureHeightDiv = subTextureHeight / height / 2;
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

    public int getSubTextureWidth() {
        return subTextureWidth;
    }

    public int getSubTextureHeight() {
        return subTextureHeight;
    }

    public int getSubTextureWidthDiv() {
        return subTextureWidthDiv;
    }

    public int getSubTextureHeightDiv() {
        return subTextureHeightDiv;
    }
}
