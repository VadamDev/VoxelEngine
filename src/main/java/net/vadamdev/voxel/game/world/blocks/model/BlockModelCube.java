package net.vadamdev.voxel.game.world.blocks.model;

import net.vadamdev.voxel.game.world.chunk.mesh.ChunkMesh;
import org.jetbrains.annotations.NotNull;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class BlockModelCube implements BlockModel {
    private final FaceUV[] uvs;

    public BlockModelCube(FaceUV[] uvs, float uShift, float vShift) {
        this.uvs = FaceUV.shift(uvs, uShift, vShift);
    }

    @Override
    public @NotNull BlockModelFactory factory(float xOffset, float yOffset, float zOffset) {
        return new Factory(xOffset, yOffset, zOffset);
    }

    private final class Factory extends BlockModelFactory {
        private final float[] vertices;
        private byte verticesCount, vertexWriteIndex;

        private final float[] textureCoords;
        private byte textureCoordsCount, textureCoordsWriteIndex;

        public Factory(float xOffset, float yOffset, float zOffset) {
            super(xOffset, yOffset, zOffset);

            this.vertices = new float[3 * 6 * 6];
            this.verticesCount = 0;
            this.vertexWriteIndex = 0;

            this.textureCoords = new float[2 * 6 * 6];
            this.textureCoordsCount = 0;
            this.textureCoordsWriteIndex = 0;
        }

        @NotNull
        @Override
        public BlockModelFactory work(int adjacentBlocks) {
            if((adjacentBlocks & ChunkMesh.BITMASK_POS_X) == 0)
                addPositiveXPlane();

            if((adjacentBlocks & ChunkMesh.BITMASK_NEG_X) == 0)
                addNegativeXPlane();

            if((adjacentBlocks & ChunkMesh.BITMASK_POS_Y) == 0)
                addPositiveYPlane();

            if((adjacentBlocks & ChunkMesh.BITMASK_NEG_Y) == 0)
                addNegativeYPlane();

            if((adjacentBlocks & ChunkMesh.BITMASK_POS_Z) == 0)
                addPositiveZPlane();

            if((adjacentBlocks & ChunkMesh.BITMASK_NEG_Z) == 0)
                addNegativeZPlane();

            vertexWriteIndex = 0;
            textureCoordsWriteIndex = 0;

            return this;
        }

        private void addPositiveXPlane() {
            final FaceUV uv = uvs[0];

            addVertex(1, 1, 1); addTextureCoord(uv.u0(), uv.v0());
            addVertex(1, 0, 1); addTextureCoord(uv.u0(), uv.v1());
            addVertex(1, 0, 0); addTextureCoord(uv.u1(), uv.v1());

            addVertex(1, 1, 1); addTextureCoord(uv.u0(), uv.v0());
            addVertex(1, 0, 0); addTextureCoord(uv.u1(), uv.v1());
            addVertex(1, 1, 0); addTextureCoord(uv.u1(), uv.v0());
        }

        private void addNegativeXPlane() {
            final FaceUV uv = uvs[1];

            addVertex(0, 1, 1); addTextureCoord(uv.u1(), uv.v0());
            addVertex(0, 1, 0); addTextureCoord(uv.u0(), uv.v0());
            addVertex(0, 0, 0); addTextureCoord(uv.u0(), uv.v1());

            addVertex(0, 0, 0); addTextureCoord(uv.u0(), uv.v1());
            addVertex(0, 0, 1); addTextureCoord(uv.u1(), uv.v1());
            addVertex(0, 1, 1); addTextureCoord(uv.u1(), uv.v0());
        }

        private void addPositiveYPlane() {
            final FaceUV uv = uvs[2];

            addVertex(1, 1, 1); addTextureCoord(uv.u1(), uv.v0());
            addVertex(1, 1, 0); addTextureCoord(uv.u0(), uv.v0());
            addVertex(0, 1, 0); addTextureCoord(uv.u0(), uv.v1());

            addVertex(0, 1, 0); addTextureCoord(uv.u0(), uv.v1());
            addVertex(0, 1, 1); addTextureCoord(uv.u1(), uv.v1());
            addVertex(1, 1, 1); addTextureCoord(uv.u1(), uv.v0());
        }

        private void addNegativeYPlane() {
            final FaceUV uv = uvs[3];

            addVertex(1, 0, 1); addTextureCoord(uv.u1(), uv.v0());
            addVertex(0, 0, 1); addTextureCoord(uv.u0(), uv.v0());
            addVertex(0, 0, 0); addTextureCoord(uv.u0(), uv.v1());

            addVertex(1, 0, 1); addTextureCoord(uv.u1(), uv.v0());
            addVertex(0, 0, 0); addTextureCoord(uv.u0(), uv.v1());
            addVertex(1, 0, 0); addTextureCoord(uv.u1(), uv.v1());
        }

        private void addPositiveZPlane() {
            final FaceUV uv = uvs[4];

            addVertex(1, 1, 1); addTextureCoord(uv.u1(), uv.v0());
            addVertex(0, 1, 1); addTextureCoord(uv.u0(), uv.v0());
            addVertex(0, 0, 1); addTextureCoord(uv.u0(), uv.v1());

            addVertex(1, 1, 1); addTextureCoord(uv.u1(), uv.v0());
            addVertex(0, 0, 1); addTextureCoord(uv.u0(), uv.v1());
            addVertex(1, 0, 1); addTextureCoord(uv.u1(), uv.v1());
        }

        private void addNegativeZPlane() {
            final FaceUV uv = uvs[5];

            addVertex(1, 1, 0); addTextureCoord(uv.u0(), uv.v0());
            addVertex(1, 0, 0); addTextureCoord(uv.u0(), uv.v1());
            addVertex(0, 0, 0); addTextureCoord(uv.u1(), uv.v1());

            addVertex(0, 0, 0); addTextureCoord(uv.u1(), uv.v1());
            addVertex(0, 1, 0); addTextureCoord(uv.u1(), uv.v0());
            addVertex(1, 1, 0); addTextureCoord(uv.u0(), uv.v0());
        }

        private void addVertex(float x, float y, float z) {
            vertices[vertexWriteIndex++] = x + xOffset;
            vertices[vertexWriteIndex++] = y + yOffset;
            vertices[vertexWriteIndex++] = z + zOffset;

            verticesCount++;
        }

        private void addTextureCoord(float u, float v) {
            textureCoords[textureCoordsWriteIndex++] = u;
            textureCoords[textureCoordsWriteIndex++] = v;

            textureCoordsCount++;
        }

        @Override
        public float[] getVertices() {
            final float[] compressed = new float[verticesCount * 3];
            System.arraycopy(vertices, 0, compressed, 0, compressed.length);

            return compressed;
        }

        @Override
        public int getVerticesCount() {
            return verticesCount;
        }

        @Override
        public float[] getTextureCoords() {
            final float[] compressed = new float[textureCoordsCount * 2];
            System.arraycopy(textureCoords, 0, compressed, 0, compressed.length);

            return compressed;
        }
    }
}
