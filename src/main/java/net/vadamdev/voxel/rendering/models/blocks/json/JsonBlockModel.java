package net.vadamdev.voxel.rendering.models.blocks.json;

import net.vadamdev.voxel.engine.json.PreemJsonArray;
import net.vadamdev.voxel.engine.json.PreemJsonObj;
import net.vadamdev.voxel.engine.utils.FileUtils;
import net.vadamdev.voxel.rendering.models.BlockModels;
import net.vadamdev.voxel.rendering.models.blocks.BlockModel;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VadamDev
 * @since 01/08/2025
 */
public class JsonBlockModel {
    private final Map<String, String> textures;
    private final List<JsonBlockElement> elements;

    public JsonBlockModel(String name) throws IOException, ParseException {
        this.textures = new HashMap<>();
        this.elements = new ArrayList<>();

        final PreemJsonObj modelJson = PreemJsonObj.of(FileUtils.parseJson(BlockModels.MODELS_PATH + name + ".json", new JSONObject()));
        loadTextures(modelJson); //Load textures first, otherwise parent model cannot use overrides we defined in the modelJson

        loadParentModelIfPresent(modelJson.getString("parent"));
        loadElements(modelJson);

        if(elements.isEmpty())
            throw new NullPointerException("No elements where found in model: " + name);
    }

    private void loadParentModelIfPresent(@Nullable String parentName) throws IOException, ParseException {
        if(parentName == null)
            return;

        final PreemJsonObj parentJson = PreemJsonObj.of(FileUtils.parseJson(BlockModels.MODELS_PATH + parentName + ".json", new JSONObject()));
        loadTextures(parentJson);
        loadElements(parentJson);
    }

    private void loadTextures(PreemJsonObj modelJson) {
        //Parent model textures OR current model textures
        final Map<String, String> map = (Map<String, String>) modelJson.get("textures");
        if(map == null)
            return;

        textures.putAll(map);
    }

    private void loadElements(PreemJsonObj modelJson) {
        //Parent model elements OR current model elements
        final PreemJsonArray elementsJson = modelJson.getJsonArray("elements");
        if(elementsJson == null)
            return;

        if(!elements.isEmpty())
            elements.clear();

        elementsJson.asListOf(PreemJsonObj.class).forEach(elementJson -> elements.add(new JsonBlockElement(elementJson, textures)));
    }

    public BlockModel toBlockModel() {
        return new BlockModel(elements.stream().map(JsonBlockElement::toVoxelFactory).toList());
    }
}
