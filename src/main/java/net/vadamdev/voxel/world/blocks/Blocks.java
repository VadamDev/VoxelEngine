package net.vadamdev.voxel.world.blocks;

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.vadamdev.voxel.world.blocks.impl.BillboardBlock;
import net.vadamdev.voxel.world.blocks.impl.EdgeBlock;
import net.vadamdev.voxel.world.blocks.impl.UnknownBlock;
import net.vadamdev.voxel.world.blocks.impl.WaterBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author VadamDev
 * @since 20/06/2025
 */
public final class Blocks {
    private Blocks() {}
    public static void registerAll() {}

    private static final Short2ObjectOpenHashMap<Block> blocks = new Short2ObjectOpenHashMap<>();

    public static final Block UNKNOWN = registerBlock(new UnknownBlock());
    private static final Block EDGE   = registerBlock(new EdgeBlock());

    public static final Block STONE     = registerBlock(new Block("stone"));
    public static final Block DIRT      = registerBlock(new Block("dirt"));
    public static final Block GRASS     = registerBlock(new Block("grass"));
    public static final Block BILLBOARD = registerBlock(new BillboardBlock());
    public static final Block BEDROCK   = registerBlock(new Block("bedrock"));
    public static final Block GLASS     = registerBlock(new Block("glass", true));
    public static final Block WATER     = registerBlock(new WaterBlock());
    public static final Block SAND      = registerBlock(new Block("sand"));

    /*
       Registry
     */

    public static Block registerBlock(Block block) {
        final short blockId = block.blockId();
        if(blocks.containsKey(blockId) || blockId == 0)
            throw new IllegalArgumentException("Block id " + blockId + " is already used");

        blocks.put(blockId, block);
        return block;
    }

    private static short id = 0;
    public static short nextId() {
        return ++id;
    }

    @Nullable
    public static Block getBlockById(short blockId) {
        if(blockId == 0 || !blocks.containsKey(blockId))
            return null;

        return blocks.get(blockId);
    }

    @NotNull
    public static Block getBlockNotNull(short blockId) {
        final Block block = getBlockById(blockId);
        return block != null ? block : Blocks.UNKNOWN;
    }

    public static Collection<Block> getRegisteredBlocks() {
        return Collections.unmodifiableCollection(blocks.values());
    }
}
