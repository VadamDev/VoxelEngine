package net.vadamdev.voxel.engine.loop;

import net.vadamdev.voxel.engine.window.Window;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class FixedStepLoop implements Runnable {
    public static final int DEFAULT_TARGET_UPS = 20;
    public static final int DEFAULT_TARGET_FPS = 60;

    private static final double NANO = 1e9d;

    private final Window window;
    private final IGameLogic game;

    private final Thread thread;
    private boolean running;

    private final Queue<Runnable> tasks;

    private int targetUps, targetFps;
    private double updateTime, renderTime;

    private int ups, fps;
    private double msPerUpdateAvg, msPerFrameAvg;
    private float currentFrameTime;

    public FixedStepLoop(Window window, IGameLogic game) {
        this.window = window;
        this.game = game;

        this.thread = new Thread(this, "Main Thread");
        this.running = false;

        this.tasks = new ConcurrentLinkedQueue<>();

        this.targetUps = DEFAULT_TARGET_UPS;
        this.targetFps = DEFAULT_TARGET_FPS;

        this.updateTime = NANO / targetUps;
        this.renderTime = NANO / targetFps;
    }

    public void start() {
        if(running)
            throw new IllegalStateException("The game loop is already running");

        running = true;
        thread.start();
    }

    public void stop() {
        if(!running)
            throw new IllegalStateException("The game loop is already stopped");

        running = false;
    }

    @Override
    public void run() {
        try {
            window.init();
            game.init();
        }catch(Exception e) {
            System.err.println("Failed to initialize the game:");
            e.printStackTrace();

            return;
        }

        long lastUpdateTime = System.nanoTime(), lastRenderTime = System.nanoTime();
        int updates = 0, frames = 0;
        double msPerUpdate = 0, msPerFrame = 0;

        long timer = System.currentTimeMillis();

        while(running && !window.shouldClose()) {
            final long now = System.nanoTime();

            if(now - lastUpdateTime > updateTime) {
                game.update();

                msPerUpdate += (System.nanoTime() - now) / 1e6d;

                updates++;
                lastUpdateTime = now;
            }else if(now - lastRenderTime > renderTime) {
                currentFrameTime = now / 1e9f;
                final float deltaTime = (now - lastRenderTime) / 1e9f;

                try {
                    if(!tasks.isEmpty()) {
                        Runnable task;
                        while((task = tasks.poll()) != null)
                            task.run();
                    }

                    game.processInputs(deltaTime);

                    window.update();
                    game.render(deltaTime);
                    window.swapBuffers();
                }catch (Exception e) {
                    System.err.println("An error occurred while rendering frame:");
                    e.printStackTrace();

                    break;
                }

                msPerFrame += (System.nanoTime() - now) / 1e6;

                frames++;
                lastRenderTime = now;
            }

            if(System.currentTimeMillis() - timer > 1000) {
                ups = updates;
                fps = frames;

                msPerUpdateAvg = msPerUpdate / Math.max(1, updates);
                msPerFrameAvg = msPerFrame / Math.max(1, frames);

                updates = 0;
                frames = 0;

                msPerUpdate = 0;
                msPerFrame = 0;

                timer += 1000;

                System.out.println(String.format("FPS: %s (%.3f ms) | UPS: %s (%.3f ms)", fps, msPerFrameAvg, ups, msPerUpdateAvg));
            }
        }

        running = false;

        game.dispose();
        window.dispose();
    }

    public void submitTask(Runnable runnable) {
        tasks.add(runnable);
    }

    public void setTargetUps(int targetUps) {
        this.targetUps = targetUps;
        this.updateTime = NANO / targetUps;
    }

    public int getTargetUps() {
        return targetUps;
    }

    public void setTargetFps(int targetFps) {
        this.targetFps = targetFps;
        this.renderTime = NANO / targetFps;
    }

    public int getTargetFps() {
        return targetFps;
    }

    public int getUps() {
        return ups;
    }

    public double getMsPerUpdateAvg() {
        return msPerUpdateAvg;
    }

    public int getFps() {
        return fps;
    }

    public double getMsPerFrameAvg() {
        return msPerFrameAvg;
    }

    public float getCurrentFrameTime() {
        return currentFrameTime;
    }
}
