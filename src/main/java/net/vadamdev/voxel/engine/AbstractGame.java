package net.vadamdev.voxel.engine;

import net.vadamdev.voxel.engine.loop.*;
import net.vadamdev.voxel.engine.window.Window;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public abstract class AbstractGame implements IGameLoop, ITaskHandler {
    protected final Window window;
    private final ILoop loop;

    public AbstractGame(Window window) {
        this.window = window;
        this.loop = new FixedStepLoop(window, this);
    }

    public AbstractGame(Window window, ILoop loop) {
        this.window = window;
        this.loop = loop;
    }

    public void start() {
        window.create();

        loop.start();
    }

    public void stop() {
        loop.stop();
    }

    @Override
    public void runTask(RunContext context, Runnable task) {
        loop.runTask(context, task);
    }
}
