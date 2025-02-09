package net.vadamdev.voxel.game.rendering;

import net.vadamdev.voxel.engine.graphics.textures.TextureAtlas;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public class BlockTextureAtlas extends TextureAtlas {
    private static BlockTextureAtlas INSTANCE;
    public static BlockTextureAtlas get() {
        return INSTANCE;
    }

    static {
        try {
            INSTANCE = new BlockTextureAtlas();
            INSTANCE.create();
        }catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public BlockTextureAtlas() throws IOException {
        super("/assets/textures/blocks.png", 16, 16);
    }
}
