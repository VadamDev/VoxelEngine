package net.vadamdev.voxel.engine.loop;

import net.vadamdev.voxel.engine.utils.Disposable;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public interface IGameLogic extends Disposable {
    void init() throws Exception;

    void processInputs(float deltaTime);
    void update();
    void render(float deltaTime);
}
