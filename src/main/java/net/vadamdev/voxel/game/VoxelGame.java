package net.vadamdev.voxel.game;

import net.vadamdev.voxel.Launcher;
import net.vadamdev.voxel.engine.AbstractGame;
import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.graphics.rendering.MatrixDrawer;
import net.vadamdev.voxel.engine.imgui.ImGuiCapableWindow;
import net.vadamdev.voxel.engine.inputs.Keyboard;
import net.vadamdev.voxel.engine.inputs.Mouse;
import net.vadamdev.voxel.engine.loop.ILoop;
import net.vadamdev.voxel.game.rendering.WorldRenderer;
import net.vadamdev.voxel.game.world.LocalPlayer;
import net.vadamdev.voxel.game.world.World;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class VoxelGame extends AbstractGame {
    public static final float DEFAULT_FOV = 90;
    public static final float NEAR_CLIP_PLANE = 0.01f;
    public static final float FAR_CLIP_PLANE = 1000f;

    private final ImGuiCapableWindow window;

    private MatrixDrawer drawer;
    private Camera camera;

    private WorldRenderer worldRenderer;

    private LocalPlayer localPlayer;
    private World world;

    public VoxelGame() {
        super(new ImGuiCapableWindow(Launcher.GAME_TITLE, 960, 540));

        window = (ImGuiCapableWindow) super.window;
    }

    @Override
    public void init(ILoop loop) {
        drawer = new MatrixDrawer();
        camera = new Camera();

        try {
            worldRenderer = new WorldRenderer(drawer);
        }catch (Exception e) {
            e.printStackTrace();

            System.exit(-1);
            return;
        }

        window.onResize(window -> drawer.updateProjectionMatrix(DEFAULT_FOV, window.getAspectRatio(), NEAR_CLIP_PLANE, FAR_CLIP_PLANE));

        //Game stuff
        localPlayer = new LocalPlayer(camera, drawer);
        world = new World(worldRenderer);

        //ImGui
        window.registerImGuiWindow(new GameInterface(loop, window, camera, drawer, worldRenderer, world));
    }

    @Override
    public void processInputs(float dt) {
        if(Mouse.isButtonDown(Mouse.BUTTON_1) && !window.isGrabbed() && !window.wantCapturePeripherals())
            window.setGrabbed(true);
        else if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && window.isGrabbed())
            window.setGrabbed(false);

        if(window.isGrabbed())
            localPlayer.processInputs(dt);
    }

    @Override
    public void update() {
        world.update(localPlayer);
    }

    @Override
    public void render() {
        world.render();
    }

    @Override
    public void cleanup() {
        worldRenderer.destroy();
        world.destroy();

        drawer.destroy();
    }
}
