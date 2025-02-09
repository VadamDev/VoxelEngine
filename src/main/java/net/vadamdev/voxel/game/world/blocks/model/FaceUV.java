package net.vadamdev.voxel.game.world.blocks.model;

import net.vadamdev.voxel.game.rendering.BlockTextureAtlas;

/**
 * @author VadamDev
 * @since 07/02/2025
 */
public record FaceUV(float u0, float v0, float u1, float v1) {
    private static final FaceUV NO_SHIFT_UV = new FaceUV(0, 0, 0.5f, 0.5f);
    private static final FaceUV ONE_U_SHIFT_UV = shift(NO_SHIFT_UV, 0.5f, 0);
    private static final FaceUV TWO_U_SHIFT_UV = shift(NO_SHIFT_UV, 1, 0);

    public static final FaceUV[] FULL_CUBE_UV = new FaceUV[] {
            NO_SHIFT_UV, //Pos X
            NO_SHIFT_UV, //Neg X
            NO_SHIFT_UV, //Pos Y
            NO_SHIFT_UV, //Neg Y
            NO_SHIFT_UV, //Pos Z
            NO_SHIFT_UV  //Neg Z
    };

    public static final FaceUV[] TOP_SIDES_BOTTOM_UV = new FaceUV[] {
            ONE_U_SHIFT_UV, //Pos X
            ONE_U_SHIFT_UV, //Neg X
            NO_SHIFT_UV, //Pos Y
            TWO_U_SHIFT_UV, //Neg Y
            ONE_U_SHIFT_UV, //Pos Z
            ONE_U_SHIFT_UV  //Neg Z
    };

    public static FaceUV[] shift(FaceUV[] uvs, float uOffset, float vOffset) {
        final FaceUV[] shifted = new FaceUV[uvs.length];
        for(int i = 0; i < uvs.length; i++)
            shifted[i] = shift(uvs[i], uOffset, vOffset);

        return shifted;
    }

    public static FaceUV shift(FaceUV uv, float uOffset, float vOffset) {
        return new FaceUV(uv.u0 + uOffset, uv.v0 + vOffset, uv.u1 + uOffset, uv.v1 + vOffset);
    }
}
