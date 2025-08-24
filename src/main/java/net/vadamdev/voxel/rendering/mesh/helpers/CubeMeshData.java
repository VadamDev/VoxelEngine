package net.vadamdev.voxel.rendering.mesh.helpers;

import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 16/08/2025
 */
public class CubeMeshData {
    private final float[] vertices;
    private final int[] indices;

    public CubeMeshData(Vector3f from, Vector3f to) {
        final float startX = from.x();
        final float startY = from.y();
        final float startZ = from.z();

        final float endX = to.x();
        final float endY = to.y();
        final float endZ = to.z();

        vertices = new float[] {
                // Front face
                startX, startY, endZ,
                endX,   startY, endZ,
                endX,   endY,   endZ,
                startX, endY,   endZ,

                // Back face
                startX, startY, startZ,
                endX,   startY, startZ,
                endX,   endY,   startZ,
                startX, endY,   startZ
        };

        indices = new int[] {
                // Front face
                0, 1, 2,
                2, 3, 0,

                // Right face
                1, 5, 6,
                6, 2, 1,

                // Back face
                5, 4, 7,
                7, 6, 5,

                // Left face
                4, 0, 3,
                3, 7, 4,

                // Top face
                3, 2, 6,
                6, 7, 3,

                // Bottom face
                4, 5, 1,
                1, 0, 4
        };
    }

    public float[] getVertices() {
        return vertices;
    }

    public int getVerticesCount() {
        return vertices.length / 3;
    }

    public int[] getIndices() {
        return indices;
    }
}
