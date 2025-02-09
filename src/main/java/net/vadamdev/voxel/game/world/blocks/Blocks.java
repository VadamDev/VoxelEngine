package net.vadamdev.voxel.game.world.blocks;

import net.vadamdev.voxel.game.world.blocks.model.BlockModelFactory;
import net.vadamdev.voxel.game.world.blocks.model.FaceUV;
import net.vadamdev.voxel.game.world.blocks.types.SimpleTexturedBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public enum Blocks {
    AIR(null, 0),

    STONE(new SimpleTexturedBlock(FaceUV.FULL_CUBE_UV, 0, 0), 1),
    DIRT(new SimpleTexturedBlock(FaceUV.FULL_CUBE_UV, 0.5f, 0), 2),
    GRASS(new SimpleTexturedBlock(FaceUV.TOP_SIDES_BOTTOM_UV, 1f, 0), 3);

    private final short blockId;

    Blocks(Block block, int blockId) {
        this.blockId = (short) blockId;

        registerBlock(block, this.blockId);
    }

    public short getBlockId() {
        return blockId;
    }

    /*
       Block Registry
     */

    public static void registerBlock(Block block, short blockId) {
        if(block == null)
            return;

        BlockRegistry.blocks.put(blockId, block);
    }

    @Nullable
    public static Block getBlockById(short blockId) {
        if(blockId == 0)
            return null;

        return BlockRegistry.blocks.get(blockId);
    }

    private static final class BlockRegistry {
        private static final Map<Short, Block> blocks = new ConcurrentHashMap<>();
    }
}
