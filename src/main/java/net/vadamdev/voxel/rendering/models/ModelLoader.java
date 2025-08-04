package net.vadamdev.voxel.rendering.models;

import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import net.vadamdev.voxel.rendering.models.blocks.json.JsonBlockModel;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 02/08/2025
 */
public final class ModelLoader {
    private ModelLoader() {}

    public static BlockModel loadBlockModel(String name) throws IOException, ParseException {
        return new JsonBlockModel(name).toBlockModel();
    }
}
