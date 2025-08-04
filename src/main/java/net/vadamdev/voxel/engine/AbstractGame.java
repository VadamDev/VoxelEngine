package net.vadamdev.voxel.engine;

import net.vadamdev.voxel.engine.loop.FixedStepLoop;
import net.vadamdev.voxel.engine.loop.IGameLogic;
import net.vadamdev.voxel.engine.window.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public abstract class AbstractGame<W extends Window> implements IGameLogic {
    protected final Logger logger;

    protected W window;
    protected FixedStepLoop gameLoop;

    public AbstractGame(W window) {
        this.logger = LoggerFactory.getLogger(getClass());

        this.window = window;
        this.gameLoop = new FixedStepLoop(window, this);
    }

    public void start() {
        gameLoop.start();
    }

    public float getCurrentFrameTime() {
        return gameLoop.getCurrentFrameTime();
    }

    public Logger getLogger() {
        return logger;
    }
}
