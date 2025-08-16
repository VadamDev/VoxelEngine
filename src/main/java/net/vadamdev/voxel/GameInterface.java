package net.vadamdev.voxel;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.vadamdev.voxel.debug.NoiseVisualizer;
import net.vadamdev.voxel.debug.TextureVisualizer;
import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.loop.FixedStepLoop;
import net.vadamdev.voxel.engine.math.MathHelper;
import net.vadamdev.voxel.engine.window.imgui.DearImGui;
import net.vadamdev.voxel.rendering.terrain.WorldRenderer;
import net.vadamdev.voxel.rendering.terrain.ChunkMeshFactory;
import net.vadamdev.voxel.rendering.terrain.shaders.WaterTerrainShader;
import net.vadamdev.voxel.world.World;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import static imgui.ImGui.*;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class GameInterface implements DearImGui {
    private final FixedStepLoop gameLoop;
    private final Camera camera;

    private final WorldRenderer worldRenderer;

    public GameInterface(FixedStepLoop gameLoop, Camera camera, WorldRenderer worldRenderer) {
        this.gameLoop = gameLoop;
        this.camera = camera;

        this.worldRenderer = worldRenderer;

        initParameters();
    }

    //Other Interfaces
    private TextureVisualizer atlasVisualizer;
    private NoiseVisualizer noiseVisualizer;

    //Engine
    private ImBoolean fpsCap, viewTextureAtlas, viewTerrainNoise;

    //Shaders
    private ImBoolean waveEnabled;
    private float[] waveMaxDistance, waveStrength, waveFalloff;

    private float[] aoIntensity;

    //Camera
    private static final float MIN_FOV = 70, MAX_FOV = 110;
    private float[] fov;

    private static final int MIN_RENDER_DISTANCE = 1, MAX_RENDER_DISTANCE = 32;
    private int[] renderDistance;

    private static final String[] renderModes = new String[] { "GL_FILL", "GL_LINE", "GL_POINT" };
    private ImInt renderModeIndex;

    private ImBoolean faceCulling, frustumCulling;
    private ImBoolean chunkBorders;

    private void initParameters() {
        atlasVisualizer = new TextureVisualizer(VoxelGame.get().getWorldRenderer().getTextureAtlas().getAtlas());
        noiseVisualizer = new NoiseVisualizer(VoxelGame.get().getWorld().noise, camera);

        //Engine
        fpsCap = new ImBoolean(true);
        viewTextureAtlas = new ImBoolean(false);
        viewTerrainNoise = new ImBoolean(false);

        //Shaders
        final WaterTerrainShader.WaveEffect waveEffect = worldRenderer.getWaterShader().waveEffect.get();
        waveEnabled = new ImBoolean(waveEffect.enabled);
        waveMaxDistance = new float[] { waveEffect.maxDistance };
        waveStrength = new float[] { waveEffect.strength };
        waveFalloff = new float[] { waveEffect.falloff };

        aoIntensity = new float[] { worldRenderer.aoIntensity };

        //Camera
        fov = new float[] { camera.fov };
        renderDistance = new int[] { World.RENDER_DISTANCE };

        renderModeIndex = new ImInt();

        faceCulling = new ImBoolean(worldRenderer.faceCulling);
        frustumCulling = new ImBoolean(worldRenderer.frustumCulling);

        chunkBorders = new ImBoolean(worldRenderer.renderChunkBorders);
    }

    @Override
    public void begin() {
        ImGui.begin("Voxel Game");
    }

    @Override
    public void draw() {
        //Engine
        if(collapsingHeader("Engine", null, ImGuiTreeNodeFlags.DefaultOpen)) {
            text("UPS: " + gameLoop.getUps() + " (avg " + String.format("%.3f", gameLoop.getMsPerUpdateAvg()) + " ms)");
            text("FPS: " + gameLoop.getFps() + " (avg " + String.format("%.3f", gameLoop.getMsPerFrameAvg()) + " ms) |"); sameLine(); checkbox("Limit FPS", fpsCap);
            newLine();
            checkbox("View Texture Atlas", viewTextureAtlas);
            checkbox("View Terrain Noise", viewTerrainNoise);

            newLine();
        }

        //Renderer
        if(collapsingHeader("Renderer")) {
            final ChunkMeshFactory meshFactory = VoxelGame.get().getChunkMeshFactory();

            WorldRenderer.ChunkCounter chunkCounter = worldRenderer.getRenderedMeshesCount();

            text("Chunk Meshes:");
            text("Solid: " + chunkCounter.solidMeshCount() + " | Water: " + chunkCounter.waterMeshCount() + " | Empty: " + chunkCounter.emptyCount());
            text("Rendered: " + chunkCounter.totalRendered() + " / " + chunkCounter.total());
            newLine();
            text("Currently Meshing: " + meshFactory.getActiveMeshingTasks());

            newLine();
        }

        if(collapsingHeader("Shaders")) {
            checkbox("waveEnabled", waveEnabled);
            dragFloat("waveDistance", waveMaxDistance, 1);
            dragFloat("waveStrength", waveStrength, 0.01f);
            dragFloat("waveFalloff", waveFalloff, 0.1f);

            newLine();
            dragFloat("aoIntensity", aoIntensity, 0.01f);

            newLine();
        }

        //Camera
        if(collapsingHeader("Camera", null, ImGuiTreeNodeFlags.DefaultOpen)) {
            final Vector3f cameraPos = camera.position();

            text(String.format("World XYZ: %.2f | %.2f | %.2f", cameraPos.x(), cameraPos.y(), cameraPos.z()));
            text("Chunk XYZ: " + MathHelper.floorDiv(cameraPos.x(), Chunk.CHUNK_WIDTH) + " | " + MathHelper.floorDiv(cameraPos.y(), Chunk.CHUNK_HEIGHT) + " | " + MathHelper.floorDiv(cameraPos.z(), Chunk.CHUNK_DEPTH));
            newLine();
            text("Yaw: " + camera.rotation().y() + "° | Pitch: " + camera.rotation().x() + "°");
            newLine();
            text("Fov"); sliderFloat(" ", fov, MIN_FOV, MAX_FOV);
            text("Render Distance"); dragInt("  ", renderDistance, 1, MIN_RENDER_DISTANCE, MAX_RENDER_DISTANCE);
            text("Polygon Mode"); combo("   ", renderModeIndex, renderModes);
            newLine();
            checkbox("Face Culling", faceCulling); sameLine(); checkbox("Frustum Culling", frustumCulling);
            checkbox("Chunk Borders", chunkBorders);

            newLine();
        }
    }

    @Override
    public void end() {
        ImGui.end();

        //Engine
        final boolean shouldFpsBeCapped = fpsCap.get();
        final boolean areFpsCapped = gameLoop.getTargetFps() != Integer.MAX_VALUE;

        if(shouldFpsBeCapped && !areFpsCapped)
            gameLoop.setTargetFps(FixedStepLoop.DEFAULT_TARGET_FPS);
        else if(!shouldFpsBeCapped && areFpsCapped)
            gameLoop.setTargetFps(Integer.MAX_VALUE);

        //Shaders
        final WaterTerrainShader.WaveEffect waveEffect = worldRenderer.getWaterShader().waveEffect.get();
        if(waveEffect.enabled != waveEnabled.get())
            waveEffect.enabled = waveEnabled.get();

        if(waveMaxDistance[0] != waveEffect.maxDistance)
            waveEffect.maxDistance = waveMaxDistance[0];

        if(waveStrength[0] != waveEffect.strength)
            waveEffect.strength = waveStrength[0];

        if(waveFalloff[0] != waveEffect.falloff)
            waveEffect.falloff = waveFalloff[0];

        if(aoIntensity[0] != worldRenderer.aoIntensity)
            worldRenderer.aoIntensity = aoIntensity[0];

        //Camera
        if(fov[0] < MIN_FOV)
            fov[0] = MIN_FOV;
        else if(fov[0] > MAX_FOV)
            fov[0] = MAX_FOV;

        if(camera.fov != fov[0]) {
            camera.fov = fov[0];
            camera.updateProjectionMatrix();
        }

        if(worldRenderer.faceCulling != faceCulling.get())
            worldRenderer.faceCulling = faceCulling.get();

        if(worldRenderer.frustumCulling != frustumCulling.get())
            worldRenderer.frustumCulling = frustumCulling.get();

        if(worldRenderer.renderChunkBorders != chunkBorders.get())
            worldRenderer.renderChunkBorders = chunkBorders.get();

        //World
        if(renderDistance[0] < MIN_RENDER_DISTANCE)
            renderDistance[0] = MIN_RENDER_DISTANCE;
        else if(renderDistance[0] > MAX_RENDER_DISTANCE)
            renderDistance[0] = MAX_RENDER_DISTANCE;

        if(World.RENDER_DISTANCE != renderDistance[0])
            World.RENDER_DISTANCE = renderDistance[0];

        final int glRenderMode = switch(renderModes[renderModeIndex.get()]) {
            case "GL_LINE" -> GL11.GL_LINE;
            case "GL_POINT" -> GL11.GL_POINT;
            default -> GL11.GL_FILL;
        };

        if(worldRenderer.polygonMode != glRenderMode)
            worldRenderer.polygonMode = glRenderMode;
    }

    @Override
    public void render() {
        if(viewTextureAtlas.get())
            atlasVisualizer.render();

        if(viewTerrainNoise.get())
            noiseVisualizer.render();

        DearImGui.super.render();
    }
}
