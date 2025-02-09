package net.vadamdev.voxel.engine.loop;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public interface IGameLoop {
    void init(ILoop loop);

    void processInputs(float dt);
    void update();
    void render();

    void cleanup();
}
