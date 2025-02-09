package net.vadamdev.voxel.game.world.blocks.model;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public abstract class BlockModelFactory {
    protected final float xOffset, yOffset, zOffset;

    public BlockModelFactory(float xOffset, float yOffset, float zOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    @CheckReturnValue
    @NotNull
    public abstract BlockModelFactory work(int adjacentBlocks);

    public abstract float[] getVertices();
    public abstract int getVerticesCount();

    public abstract float[] getTextureCoords();
}
