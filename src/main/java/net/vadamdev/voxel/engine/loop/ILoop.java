package net.vadamdev.voxel.engine.loop;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public interface ILoop extends ITaskHandler {
    void start();
    void stop();

    int getUPS();
    int getFPS();

    int getTargetFps();
    void setTargetFps(int targetFps);

    int getTargetUps();
    void setTargetUps(int targetUps);
}
