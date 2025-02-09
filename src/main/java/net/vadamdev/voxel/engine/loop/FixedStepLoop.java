package net.vadamdev.voxel.engine.loop;

import net.vadamdev.voxel.engine.window.Window;
import org.joml.Math;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public final class FixedStepLoop implements ILoop {
    private static final int TARGET_UPS = 20;
    private static final int TARGET_FPS = 60;

    private final Window window;
    private final IGameLoop game;

    private boolean running;

    private final Deque<Runnable> updatesTasks, renderTasks;

    private int targetUps, targetFps;
    private double updateTime, renderTime;

    private int ups, fps;

    public FixedStepLoop(Window window, IGameLoop game) {
        this.window = window;
        this.game = game;

        this.running = false;

        this.updatesTasks = new ConcurrentLinkedDeque<>();
        this.renderTasks = new ConcurrentLinkedDeque<>();

        this.targetUps = TARGET_UPS;
        this.targetFps = TARGET_FPS;

        //TODO: remove this hacky way of doing things
        this.ups = targetUps;
        this.fps = targetFps;
    }

    @Override
    public void start() {
        if(running)
            return;

        running = true;

        loop();
    }

    @Override
    public void runTask(RunContext context, Runnable task) {
        switch(context) {
            case UPDATE:
                updatesTasks.add(task);
                break;
            case RENDER:
                renderTasks.add(task);
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {
        if(!running)
            return;

        running = false;
    }

    private void loop() {
        updateTime = 1000000000d / targetUps;
        renderTime = 1000000000d / targetFps;

        long lastUpdateTime = System.nanoTime();
        long lastRenderTime = System.nanoTime();

        int updates = 0, frames = 0;

        long timer = System.currentTimeMillis();

        game.init(this);

        while(running && !window.shouldClose()) {
            final long now = System.nanoTime();

            if(now - lastUpdateTime > updateTime) {
                if(!updatesTasks.isEmpty()) {
                    Runnable task;
                    while((task = updatesTasks.poll()) != null)
                        task.run();
                }

                game.update();

                updates++;

                lastUpdateTime = now;
            }else if(now - lastRenderTime > renderTime) {
                if(!renderTasks.isEmpty()) {
                    Runnable task;
                    while((task = renderTasks.poll()) != null)
                        task.run();
                }

                game.processInputs(60f / Math.max(1, fps));

                window.update();
                game.render();
                window.swapBuffers();

                frames++;

                lastRenderTime = now;
            }

            final long nowMs = System.currentTimeMillis();
            if(nowMs - timer > 1000) {
                ups = updates;
                fps = frames;

                updates = 0;
                frames = 0;

                timer = nowMs;
            }
        }

        game.cleanup();
        window.destroy();
    }

    @Override
    public int getUPS() {
        return ups;
    }

    @Override
    public int getFPS() {
        return fps;
    }

    @Override
    public int getTargetUps() {
        return targetUps;
    }

    @Override
    public int getTargetFps() {
        return targetFps;
    }

    @Override
    public void setTargetUps(int targetUps) {
        this.targetUps = targetUps;

        this.updateTime = 1000000000d / targetUps;
    }

    @Override
    public void setTargetFps(int targetFps) {
        this.targetFps = targetFps;

        this.renderTime = 1000000000d / targetFps;
    }
}
