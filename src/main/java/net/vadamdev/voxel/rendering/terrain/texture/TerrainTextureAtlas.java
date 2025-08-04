package net.vadamdev.voxel.rendering.terrain.texture;

import net.vadamdev.voxel.engine.graphics.texture.TextureAtlas;
import net.vadamdev.voxel.engine.graphics.texture.TextureUtils;
import net.vadamdev.voxel.engine.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VadamDev
 * @since 07/06/2025
 */
public class TerrainTextureAtlas {
    public static final int FILL_COLOR = new Color(49, 49, 49).getRGB();
    public static final int GRID_COLOR = new Color(65, 65, 65).getRGB();

    private static final int GRID_SIZE = 16;
    private static final int GRID_SIZE_SQUARED = GRID_SIZE * GRID_SIZE;

    private final Map<String, FaceUVs> uvOffsets;

    private TextureAtlas atlas;
    private FaceUVs debugUVs;

    public TerrainTextureAtlas() {
        this.uvOffsets = new HashMap<>();
        this.atlas = null;
    }

    public void create() throws URISyntaxException, IOException {
        //gather data
        final List<TextureData> textures = retrieveBlockTextures();
        final int atlasSize = calculateAtlasSize(textures.size());

        //Computing texture atlas, combining small textures into a giant one
        final BufferedImage result = new BufferedImage(atlasSize, atlasSize, BufferedImage.TYPE_INT_ARGB);
        final Map<String, Vector4i> offsets = new HashMap<>();

        //replace empty pixels by a background and a grid
        TextureUtils.fill(result, FILL_COLOR);
        TextureUtils.writeGrid(result, GRID_SIZE, GRID_COLOR);

        int xOffset = 0, yOffset = 0;
        for(TextureData texture : textures) {
            final BufferedImage image = texture.image();
            final int imageWidth = image.getWidth();

            //Add offsets for the next image
            if(xOffset + imageWidth > atlasSize) {
                yOffset += GRID_SIZE;
                xOffset = 0;
            }

            TextureUtils.copyPixelsToImage(image, result, xOffset, yOffset);
            offsets.put(texture.textureName(), new Vector4i(xOffset, yOffset, imageWidth, image.getHeight()));

            xOffset += GRID_SIZE;
        }

        //Dynamic uv mapping
        for(Map.Entry<String, Vector4i> entry : offsets.entrySet()) {
            final Vector4i data = entry.getValue();

            final float startU = (float) data.x() / atlasSize;
            final float startV = (float) data.y() / atlasSize;

            final float endU = startU + (float) data.z() / atlasSize;
            final float endV = startV + (float) data.w() / atlasSize;

            uvOffsets.put(entry.getKey(), new FaceUVs(startU, startV, endU, endV));
        }

        atlas = new TextureAtlas(result);
        atlas.create();

        debugUVs = getUvOffset("debug");

        System.out.println("Generated terrain texture atlas! (" + atlasSize + "*" + atlasSize + " pixels)");
    }

    private List<TextureData> retrieveBlockTextures() throws URISyntaxException, IOException {
        final List<TextureData> textures = new ArrayList<>();

        for(String path : FileUtils.getResourcesInPath("assets/textures/block")) {
            final InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            final BufferedImage image = ImageIO.read(stream);
            stream.close();

            if(image.getWidth() > GRID_SIZE || image.getHeight() > GRID_SIZE) {
                System.err.println("Image size must be lower or equal to grid size (" + GRID_SIZE + "x)");
                continue;
            }

            final String textureName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
            textures.add(new TextureData(textureName, image));
        }

        return textures;
    }

    private int calculateAtlasSize(int texturesAmount) {
        int currentPower = 4;//Default is 4 (atlasSize=16)
        int atlasSize = (int) Math.pow(2, currentPower);

        while(atlasSize * atlasSize / GRID_SIZE_SQUARED < texturesAmount)
            atlasSize = (int) Math.pow(2, ++currentPower);

        return atlasSize;
    }

    public void bind() {
        atlas.bind();
    }

    public void unbind() {
        atlas.unbind();
    }

    @NotNull
    public FaceUVs getUvOffset(String textureName) {
        if(!uvOffsets.containsKey(textureName))
            return FaceUVs.ZERO;

        return uvOffsets.get(textureName);
    }

    @NotNull
    public FaceUVs getDebugUvs() {
        return debugUVs;
    }

    @Nullable
    public TextureAtlas getAtlas() {
        return atlas;
    }

    private record TextureData(String textureName, BufferedImage image) {}
}
