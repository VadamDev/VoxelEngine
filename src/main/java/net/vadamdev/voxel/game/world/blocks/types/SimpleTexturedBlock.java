package net.vadamdev.voxel.game.world.blocks.types;

import net.vadamdev.voxel.game.world.blocks.Block;
import net.vadamdev.voxel.game.world.blocks.model.BlockModelCube;
import net.vadamdev.voxel.game.world.blocks.model.FaceUV;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public class SimpleTexturedBlock extends Block {
    public SimpleTexturedBlock(FaceUV[] uvs, float uShift, float vShift) {
        super(new BlockModelCube(uvs, uShift, vShift));
    }
}
