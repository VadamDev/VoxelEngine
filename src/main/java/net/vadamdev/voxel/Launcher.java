package net.vadamdev.voxel;

import net.vadamdev.voxel.game.VoxelGame;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public class Launcher {
    public static final String GAME_TITLE = "Voxel Game";
    public static final VoxelGame game = new VoxelGame();

    public static void main(String[] args) {
        game.start();
    }
}
