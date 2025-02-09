package net.vadamdev.voxel.game.world.blocks.model;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public interface BlockModel {
    @CheckReturnValue
    @NotNull
    BlockModelFactory factory(float xOffset, float yOffset, float zOffset);
}
