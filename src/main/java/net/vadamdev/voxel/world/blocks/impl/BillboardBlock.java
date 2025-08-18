package net.vadamdev.voxel.world.blocks.impl;

import net.vadamdev.voxel.world.blocks.Block;

/**
 * @author VadamDev
 * @since 16/08/2025
 */
public class BillboardBlock extends Block {
    public BillboardBlock() {
        super("billboard", true);
        fragile = true;
    }
}
