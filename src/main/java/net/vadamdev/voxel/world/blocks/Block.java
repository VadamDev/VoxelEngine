package net.vadamdev.voxel.world.blocks;

import net.vadamdev.voxel.rendering.models.BlockModels;
import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.world.AbstractWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VadamDev
 * @since 07/06/2025
 */
public class Block {
    private final short blockId;
    private final String name;
    private final boolean transparent, water;

    protected boolean fragile;

    public Block(short blockId, String name, boolean transparent, boolean water) {
        this.blockId = blockId;
        this.name = name;
        this.transparent = transparent;
        this.water = water;
    }

    public Block(String name, boolean transparent, boolean water) {
        this(Blocks.nextId(), name, transparent, water);
    }

    public Block(String name, boolean transparent) {
        this(name, transparent, false);
    }

    public Block(@NotNull String name) {
        this(name, false);
    }

    /*
       Model
     */

    @NotNull
    public BlockModel retrieveModel(AbstractWorld world, int x, int y, int z) {
        return BlockModels.retrieveBlockModel(name);
    }

    @Nullable
    public String[] associatedModels() {
        return new String[] { name };
    }

    /*
       Block Properties
     */

    public short blockId() {
        return blockId;
    }

    @NotNull
    public String name() {
        return name;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public boolean isWater() {
        return water;
    }

    public boolean isFragile() {
        return fragile;
    }
}
