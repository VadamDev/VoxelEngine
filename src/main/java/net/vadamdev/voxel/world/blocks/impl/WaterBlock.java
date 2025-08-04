package net.vadamdev.voxel.world.blocks.impl;

import net.vadamdev.voxel.rendering.models.BlockModels;
import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.world.AbstractWorld;
import net.vadamdev.voxel.world.blocks.Block;
import net.vadamdev.voxel.world.blocks.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VadamDev
 * @since 04/08/2025
 */
public class WaterBlock extends Block {
    public WaterBlock() {
        super(Blocks.nextId(), "water", true, true);
    }

    @NotNull
    @Override
    public BlockModel retrieveModel(AbstractWorld world, int x, int y, int z) {
        String modelName = "water_solid";
        if(world.getBlockId(x, y + 1, z) != blockId())
            modelName = "water_surface";

        return BlockModels.retrieveBlockModel(modelName);
    }

    @Nullable
    @Override
    public String[] associatedModels() {
        return new String[] { "water_surface" , "water_solid" };
    }
}
