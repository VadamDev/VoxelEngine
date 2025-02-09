package net.vadamdev.voxel.engine.loop;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public interface ITaskHandler {
    void runTask(RunContext context, Runnable task);
}
