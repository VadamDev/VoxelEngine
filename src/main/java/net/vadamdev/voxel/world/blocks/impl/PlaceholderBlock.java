package net.vadamdev.voxel.world.blocks.impl;

import net.vadamdev.voxel.rendering.models.BlockModels;
import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.world.AbstractWorld;
import net.vadamdev.voxel.world.blocks.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VadamDev
 * @since 14/06/2025
 */
public class PlaceholderBlock extends Block {
    public PlaceholderBlock(short blockId) {
        super(blockId, "unknown", false, false);
    }

    @NotNull
    @Override
    public BlockModel retrieveModel(AbstractWorld world, int x, int y, int z) {
        return BlockModels.placeholderModel();
    }

    @Nullable
    @Override
    public String[] associatedModels() {
        return null;
    }
}
