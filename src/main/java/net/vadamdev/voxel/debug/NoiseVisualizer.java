package net.vadamdev.voxel.debug;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.vadamdev.voxel.engine.fastnoise.FastNoiseLite;
import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.graphics.texture.Texture;
import net.vadamdev.voxel.engine.window.imgui.DearImGui;
import org.joml.Math;

import java.awt.*;
import java.awt.image.BufferedImage;

import static imgui.ImGui.*;

/**
 * @author VadamDev
 * @since 09/06/2025
 */
public class NoiseVisualizer implements DearImGui {
    private final FastNoiseLite noise;
    private final Camera camera;

    private BufferedImage image;
    private Texture texture;

    public NoiseVisualizer(FastNoiseLite noise, Camera camera) {
        this.noise = noise;
        this.camera = camera;

        initParameters();
        updateTexture();
    }

    private ImBoolean followCamera, autoUpdate;

    public static final int MIN_TEXTURE_SIZE = 64, MAX_TEXTURE_SIZE = 256;
    public static final int MIN_SCAN_RANGE = 32, MAX_SCAN_RANGE = 512;
    private int[] textureSize, scanRange;

    private int[] xOffset, zOffset;

    private void initParameters() {
        followCamera = new ImBoolean(true);
        autoUpdate = new ImBoolean(true);
        textureSize = new int[] { MAX_TEXTURE_SIZE };
        scanRange = new int[] { 256 };

        xOffset = new int[] { 0 };
        zOffset = new int[] { 0 };
    }

    @Override
    public void begin() {
        ImGui.begin("Noise Viewer");
    }

    @Override
    public void draw() {
        if(beginTabBar("options")) {
            //Parameters
            if(beginTabItem("Parameters")) {
                checkbox("Follow Camera", followCamera);
                checkbox("Auto Update", autoUpdate);
                dragInt("Texture Size", textureSize, 1, MIN_TEXTURE_SIZE, MAX_TEXTURE_SIZE);
                dragInt("Scan Range", scanRange, 1, MIN_SCAN_RANGE, MAX_SCAN_RANGE);

                endTabItem();
            }

            //Offsets
            if(beginTabItem("Offsets")) {
                dragInt("xOffset", xOffset);
                dragInt("zOffset", zOffset);

                if(button("Reset")) {
                    xOffset[0] = 0;
                    zOffset[0] = 0;
                }

                endTabItem();
            }

            endTabBar();
        }

        validateParameters();

        newLine();

        if(beginTabBar("noise")) {
            if(beginTabItem("Noise")) {
                newLine();

                if(texture != null) {
                    float imageScalar = (float) textureSize[0] / scanRange[0];
                    image(texture.getTextureId(), texture.getWidth() * imageScalar, texture.getHeight() * imageScalar);
                }else
                    text("No texture found !");

                newLine();

                //Update button
                final boolean autoUpdate = this.autoUpdate.get();
                if(autoUpdate)
                    beginDisabled();

                if(button("Update") || autoUpdate)
                    updateTexture();

                if(autoUpdate)
                    endDisabled();

                endTabItem();
            }

            endTabBar();
        }
    }

    @Override
    public void end() {
        ImGui.end();
    }

    private void validateParameters() {
        if(textureSize[0] < MIN_TEXTURE_SIZE)
            textureSize[0] = MIN_TEXTURE_SIZE;
        else if(textureSize[0] > MAX_TEXTURE_SIZE)
            textureSize[0] = MAX_TEXTURE_SIZE;

        if(scanRange[0] < MIN_SCAN_RANGE)
            scanRange[0] = MIN_SCAN_RANGE;
        else if(scanRange[0] > MAX_SCAN_RANGE)
            scanRange[0] = MAX_SCAN_RANGE;
    }

    private void updateTexture() {
        if(texture != null)
            texture.dispose();

        final int scanRange = this.scanRange[0];
        final int halfScanRange = scanRange / 2;
        if(image == null || image.getWidth() != scanRange || image.getHeight() != scanRange)
            image = new BufferedImage(scanRange, scanRange, BufferedImage.TYPE_INT_ARGB);

        int xOffset = this.xOffset[0];
        int zOffset = this.zOffset[0];

        if(followCamera.get()) {
            xOffset += (int) Math.floor(camera.position().x());
            zOffset += (int) Math.floor(camera.position().z());
        }

        for(int x = -halfScanRange; x < halfScanRange; x++) {
            for(int y = -halfScanRange; y < halfScanRange; y++) {
                final float noisy = (this.noise.GetNoise(x + xOffset, y + zOffset) + 1) / 2f;

                //(textureSize - 1) - (x + halfTextureSize) is used to flip the image, so that xOffset++ move the image upwards
                image.setRGB(y + halfScanRange, (scanRange - 1) - (x + halfScanRange), new Color(noisy, noisy, noisy).getRGB());
            }
        }

        if(this.xOffset[0] == 0 && this.zOffset[0] == 0) {
            final int cursorColor = Color.RED.getRGB();

            //final int cursorSize = this.cursorSize[0];
            final int cursorSize = Math.max(1, scanRange / 64);
            if(cursorSize == 1)
                image.setRGB(halfScanRange, halfScanRange, cursorColor);
            else {
                final int halfCursorSize = cursorSize / 2;

                for(int x = -halfCursorSize; x < halfCursorSize; x++) {
                    for(int y = -halfCursorSize; y < halfCursorSize; y++) {
                        image.setRGB(halfScanRange + x, halfScanRange + y, cursorColor);
                    }
                }
            }
        }

        texture = new Texture(image);
        texture.create();
    }
}
