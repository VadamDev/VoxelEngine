package net.vadamdev.voxel.rendering.terrain.texture;

import net.vadamdev.voxel.VoxelGame;

import java.util.Arrays;

/**
 * @author VadamDev
 * @since 08/06/2025
 */
public record FaceUVs(float u0, float v0, float u1, float v1) {
    public static final FaceUVs ZERO = new FaceUVs(0, 0, 0, 0);

    public static FaceUVs[] newVoxelUVs(boolean autoFill) {
        final FaceUVs[] uvs = new FaceUVs[6];

        if(autoFill)
            Arrays.fill(uvs, VoxelGame.get().getWorldRenderer().getTextureAtlas().getDebugUvs());

        return uvs;
    }

    public static FaceUVs[] newVoxelUVs() {
        return newVoxelUVs(true);
    }
}
