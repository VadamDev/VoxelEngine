package net.vadamdev.voxel.engine.graphics.mesh;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author VadamDev
 * @since 25/06/2025
 */
public final class NormalsBakery {
    private NormalsBakery() {}

    public static void calculateVertexNormals(FloatBuffer verticesBuffer, FloatBuffer normalsBuffer) {
        if(verticesBuffer.capacity() != normalsBuffer.capacity())
            throw new IllegalArgumentException("verticesBuffer size must be equal to normalsBuffer size");

        final Vector3f[] vertices = bakeVertices(verticesBuffer);

        for(int i = 0; i < vertices.length; i += 3) {
            final Vector3f v0 = vertices[i];
            final Vector3f normal = vertices[i + 1].sub(v0).cross(vertices[i + 2].sub(v0)).normalize();

            for(int j = 0; j < 3; j++)
                normalsBuffer.put(normal.x()).put(normal.y()).put(normal.z());
        }

        normalsBuffer.flip();
    }

    private static Vector3f[] bakeVertices(FloatBuffer verticesBuffer) {
        final Vector3f[] result = new Vector3f[verticesBuffer.limit() / 3];

        int i = 0;
        while(verticesBuffer.position() < verticesBuffer.limit()) {
            result[i] = new Vector3f(verticesBuffer.get(), verticesBuffer.get(), verticesBuffer.get());
            i++;
        }

        verticesBuffer.position(0);

        return result;
    }
}
