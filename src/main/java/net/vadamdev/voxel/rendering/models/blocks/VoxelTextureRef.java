package net.vadamdev.voxel.rendering.models.blocks;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.rendering.terrain.texture.FaceUVs;
import net.vadamdev.voxel.rendering.terrain.texture.TerrainTextureAtlas;

/**
 * @author VadamDev
 * @since 08/06/2025
 */
public record VoxelTextureRef(String texture, int[] uvs) {
    private static final TerrainTextureAtlas terrainAtlas = VoxelGame.get().getWorldRenderer().getTextureAtlas();

    public FaceUVs toFaceUv(float voxelSize) {
        final int textureAtlasSize = terrainAtlas.getAtlas().getWidth();
        final float normalizedVoxelSize = 1f / voxelSize / textureAtlasSize;
        final float onePixel = 1f / textureAtlasSize; //1f = voxelSize / blockTextureSize. If blockTextureSize change, we are fucked TODO: higher texture size support

        final FaceUVs offsets = terrainAtlas.getUvOffset(texture);

        return new FaceUVs(
                offsets.u0() - (uvs[0] * normalizedVoxelSize),
                offsets.v0() - (uvs[1] * normalizedVoxelSize),
                offsets.u1() - (uvs[2] * normalizedVoxelSize) + (uvs[2] > 0 ? onePixel : 0),
                offsets.v1() - (uvs[3] * normalizedVoxelSize) + (uvs[3] > 0 ? onePixel : 0));
    }
}
