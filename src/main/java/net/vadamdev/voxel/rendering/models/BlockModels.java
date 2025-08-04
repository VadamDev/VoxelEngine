package net.vadamdev.voxel.rendering.models;

import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.world.blocks.Block;
import net.vadamdev.voxel.world.blocks.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VadamDev
 * @since 09/06/2025
 */
public final class BlockModels {
    private BlockModels() {}

    public static final String MODELS_PATH = "/assets/models/";

    private static final Map<String, BlockModel> models = new HashMap<>();

    private static BlockModel unknownModel;
    public static @NotNull BlockModel placeholderModel() { return unknownModel; }

    public static void loadBlockModels() throws IllegalStateException {
        System.out.println("Loading block models...");

        try {
            unknownModel = ModelLoader.loadBlockModel("unknown");
        }catch (Exception e) {
            throw new IllegalStateException("Failed to load the fallback model!", e);
        }

        for(Block block : Blocks.getRegisteredBlocks()) {
            final String[] modelNames = block.associatedModels();
            if(modelNames == null || modelNames.length == 0)
                continue;

            for(String modelName : modelNames) {
                if(models.containsKey(modelName))
                    continue;

                try {
                    registerModel(modelName, ModelLoader.loadBlockModel(modelName));
                }catch (Exception e) {
                    System.err.println("Failed to load model for block: " + block.blockId() + " (" + block.name() + " | " + modelName + ".json)");
                    e.printStackTrace();
                }
            }
        }
    }

    @NotNull
    public static BlockModel retrieveBlockModel(@Nullable String name) {
        if(name == null)
            return unknownModel;

        final BlockModel model = models.get(name);
        return model == null ? unknownModel : model;
    }

    public static void registerModel(String name, @NotNull BlockModel model) {
        models.put(name, model);
    }
}
