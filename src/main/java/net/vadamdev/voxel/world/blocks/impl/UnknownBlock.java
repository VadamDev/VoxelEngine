package net.vadamdev.voxel.world.blocks.impl;

/**
 * @author VadamDev
 * @since 09/06/2025
 */
public final class UnknownBlock extends PlaceholderBlock {
    public static final short UNKNOWN_BLOCK_ID = Short.MIN_VALUE;

    public UnknownBlock() {
        super(UNKNOWN_BLOCK_ID);
    }
}
