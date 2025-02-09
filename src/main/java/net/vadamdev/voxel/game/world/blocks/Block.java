package net.vadamdev.voxel.game.world.blocks;

import net.vadamdev.voxel.game.world.blocks.model.BlockModel;
import net.vadamdev.voxel.game.world.blocks.model.BlockModelFactory;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class Block {
    private final BlockModel model;

    public Block(BlockModel model) {
        this.model = model;
    }

    @CheckReturnValue
    @NotNull
    public BlockModelFactory createModelFactory(float xOffset, float yOffset, float zOffset) {
        return model.factory(xOffset, yOffset, zOffset);
    }
}
