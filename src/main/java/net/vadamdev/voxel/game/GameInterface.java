package net.vadamdev.voxel.game;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.vadamdev.voxel.Launcher;
import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.graphics.rendering.editor.IProjectionMatrixEditor;
import net.vadamdev.voxel.engine.imgui.ImGuiWindow;
import net.vadamdev.voxel.engine.loop.ILoop;
import net.vadamdev.voxel.engine.window.Window;
import net.vadamdev.voxel.game.rendering.WorldRenderer;
import net.vadamdev.voxel.game.world.World;
import net.vadamdev.voxel.game.world.chunk.Chunk;
import org.joml.Math;
import org.lwjgl.opengl.GL11;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class GameInterface implements ImGuiWindow {
    private final ILoop loop;
    private final Window window;

    private final Camera camera;

    private final IProjectionMatrixEditor projectionMatrix;
    private final WorldRenderer worldRenderer;

    private World world;

    public GameInterface(ILoop loop, Window window, Camera camera, IProjectionMatrixEditor projectionMatrix, WorldRenderer worldRenderer, World world) {
        this.loop = loop;
        this.window = window;

        this.camera = camera;
        this.projectionMatrix = projectionMatrix;

        this.worldRenderer = worldRenderer;
        this.world = world;

        initData();
    }

    //Engine
    private ImBoolean fpsCap;

    //Rendering
    private float[] fov;
    private float currentFov = VoxelGame.DEFAULT_FOV;

    private int[] renderDistance;

    private String[] polygonModes;
    private ImInt selectedPolygonMode;

    private ImBoolean faceCulling, frustumCulling;

    private void initData() {
        fpsCap = new ImBoolean(true);

        fov = new float[] { currentFov };
        renderDistance = new int[] { World.RENDER_DISTANCE };

        polygonModes = new String[] { "GL_FILL", "GL_LINE", "GL_POINT" };
        selectedPolygonMode = new ImInt();

        faceCulling = new ImBoolean(true);
        frustumCulling = new ImBoolean(true);
    }

    @Override
    public void begin() {
        ImGui.begin(Launcher.GAME_TITLE);
    }

    @Override
    public void draw() {
        final int targetFps = loop.getTargetFps();

        ImGui.text("Engine:");
        ImGui.text("FPS: " + loop.getFPS() + (targetFps != Integer.MAX_VALUE ? " / " + targetFps : "") + " | "); ImGui.sameLine(); ImGui.checkbox("Limit Fps", fpsCap);
        ImGui.text("UPS: " + loop.getUPS() + " / " + loop.getTargetUps());

        ImGui.newLine();

        ImGui.text("Camera:");
        ImGui.text("X: " + camera.position().x() + " Y: " + camera.position().y() + " Z: " + camera.position().z());
        ImGui.text("Chunk XZ: " + Math.floor(camera.position().x() / Chunk.CHUNK_WIDTH) + " | " + Math.floor(camera.position().z() / Chunk.CHUNK_DEPTH));
        ImGui.newLine();
        ImGui.text("Yaw: " + Math.toDegrees(camera.getYaw()) + " | Pitch: " + Math.toDegrees(camera.getPitch()));

        ImGui.newLine();

        ImGui.text("Rendering:");
        ImGui.text("FOV");
        ImGui.sliderFloat(" ", fov, 70, 110);
        ImGui.text("Render Distance");
        ImGui.sliderInt("  ", renderDistance, 6, 32);
        ImGui.text("Polygon Mode");
        ImGui.combo("   ", selectedPolygonMode, polygonModes);

        ImGui.newLine();

        ImGui.checkbox("Face Culling", faceCulling);
        ImGui.checkbox("Frustum Culling", frustumCulling);
    }

    @Override
    public void end() {
        ImGui.end();

        //Engine
        final boolean shouldFpsBeCapped = fpsCap.get();
        final boolean areFpsCapped = loop.getTargetFps() != Integer.MAX_VALUE;

        if(shouldFpsBeCapped && !areFpsCapped)
            loop.setTargetFps(60);
        else if(!shouldFpsBeCapped && areFpsCapped)
            loop.setTargetFps(Integer.MAX_VALUE);

        //Rendering
        final float fov = this.fov[0];
        if(currentFov != fov) {
            projectionMatrix.updateProjectionMatrix(fov, window.getAspectRatio(), VoxelGame.NEAR_CLIP_PLANE, VoxelGame.FAR_CLIP_PLANE);
            currentFov = fov;
        }

        final int renderDistance = this.renderDistance[0];
        if(World.RENDER_DISTANCE != renderDistance && world.getChunkMeshFactory().isIdling())
            World.RENDER_DISTANCE = renderDistance;

        final int renderMode = switch(polygonModes[selectedPolygonMode.get()]) {
            case "GL_FILL" -> GL11.GL_FILL;
            case "GL_LINE" -> GL11.GL_LINE;
            case "GL_POINT" -> GL11.GL_POINT;
            default -> 0;
        };

        if(worldRenderer.renderMode != renderMode)
            worldRenderer.renderMode = renderMode;

        final boolean faceCulling = this.faceCulling.get();
        if(worldRenderer.faceCulling != faceCulling)
            worldRenderer.faceCulling = faceCulling;

        final boolean frustumCulling = this.frustumCulling.get();
        if(worldRenderer.frustumCulling != frustumCulling)
            worldRenderer.frustumCulling = frustumCulling;
    }
}
