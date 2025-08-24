package net.vadamdev.voxel.engine.graphics.rendering;

import net.vadamdev.voxel.engine.graphics.shaders.GLSLStruct;
import net.vadamdev.voxel.engine.graphics.shaders.IUniformAccess;
import net.vadamdev.voxel.engine.loop.FixedStepLoop;

import java.util.Map;

/**
 * @author VadamDev
 * @since 18/07/2025
 */
public class EngineData implements GLSLStruct {
    @Uniform(name = "projectionViewMatrix")   private final MatrixDrawer matrixDrawer;
    @Uniform(name = "cameraPos")              private final Camera camera;
    @Uniform(name = "currentFrameTime")       private final FixedStepLoop gameLoop;

    public EngineData(Camera camera, MatrixDrawer matrixDrawer, FixedStepLoop gameLoop) {
        this.camera = camera;
        this.matrixDrawer = matrixDrawer;
        this.gameLoop = gameLoop;
    }

    @Override
    public void sendToShader(Map<String, IUniformAccess> uniforms) {
        uniforms.get("projectionViewMatrix").setMatrix4f(matrixDrawer.projViewMatrixBuffer());
        uniforms.get("cameraPos").set3f(camera.position());
        uniforms.get("currentFrameTime").set1f(gameLoop.getCurrentFrameTime());
    }
}
