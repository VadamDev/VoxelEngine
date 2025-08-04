package net.vadamdev.voxel;

import net.vadamdev.voxel.controller.PlayerController;
import net.vadamdev.voxel.engine.AbstractGame;
import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.engine.graphics.rendering.EngineData;
import net.vadamdev.voxel.engine.graphics.rendering.MatrixDrawer;
import net.vadamdev.voxel.engine.inputs.Keyboard;
import net.vadamdev.voxel.engine.inputs.Mouse;
import net.vadamdev.voxel.engine.window.imgui.ImGuiCapableWindow;
import net.vadamdev.voxel.rendering.models.BlockModels;
import net.vadamdev.voxel.rendering.terrain.WorldRenderer;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshFactory;
import net.vadamdev.voxel.world.World;
import net.vadamdev.voxel.world.blocks.Blocks;
import org.joml.Vector3f;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class VoxelGame extends AbstractGame<ImGuiCapableWindow> {
    //World
    private World world;

    //Matrices & Controls
    private MatrixDrawer matrixDrawer;
    private PlayerController controller;

    private EngineData engineRenderingData;

    //World Rendering
    private ChunkMeshFactory meshFactory;
    private WorldRenderer worldRenderer;

    public VoxelGame() {
        super(new ImGuiCapableWindow("Voxel Game", 960, 540));
    }

    @Override
    public void init() throws Exception {
        world = new World();

        //Matrices
        matrixDrawer = new MatrixDrawer();

        //Camera & Player Controller
        final Camera camera = new Camera(new Vector3f(0.5f, 0, 0.5f), window, matrixDrawer);
        controller = new PlayerController(camera);

        engineRenderingData = new EngineData(camera, matrixDrawer, gameLoop);

        //World
        meshFactory = new ChunkMeshFactory(world);
        worldRenderer = new WorldRenderer(meshFactory, matrixDrawer);

        //Blocks
        Blocks.registerAll();
        BlockModels.loadBlockModels();

        //Init world
        world.init(camera, worldRenderer);

        //Interfaces
        window.registerImGui(new GameInterface(gameLoop, camera, worldRenderer));

        worldRenderer.postInit();
    }

    @Override
    public void processInputs(float deltaTime) {
        if(Mouse.isButtonDown(Mouse.BUTTON_1) && !window.isGrabbed() && !window.wantCapturePeripherals())
            window.setGrabbed(true);
        else if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && window.isGrabbed())
            window.setGrabbed(false);

        if(!window.isGrabbed())
            return;

        controller.processInputs(deltaTime);
    }

    @Override
    public void update() {
        world.update();
    }

    @Override
    public void render(float deltaTime) {
        worldRenderer.render();
    }

    @Override
    public void dispose() {
        meshFactory.shutdown();
        worldRenderer.dispose();
        matrixDrawer.dispose();
    }

    public EngineData getRenderingEngineData() {
        return engineRenderingData;
    }

    public World getWorld() {
        return world;
    }

    public ChunkMeshFactory getChunkMeshFactory() {
        return meshFactory;
    }

    public WorldRenderer getWorldRenderer() {
        return worldRenderer;
    }

    /*
       Main
     */

    private static VoxelGame instance;
    public static VoxelGame get() { return instance; }

    public static void runOnMainThread(Runnable runnable) {
        instance.gameLoop.submitTask(runnable);
    }

    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        instance = new VoxelGame();
        instance.start();
    }
}
