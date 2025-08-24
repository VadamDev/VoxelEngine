package net.vadamdev.voxel.rendering.mesh.helpers;

import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author VadamDev
 * @since 16/08/2025
 */
public final class Wireframes {
    private Wireframes() {}

    public static void createWireframeBox(float width, Vector3f from, Vector3f to, FloatBuffer verticesBuffer, IntBuffer indicesBuffer) {
        final CubeMeshData x = new CubeMeshData(new Vector3f(from.x(), 0, 0), new Vector3f(to.x(), width, width));
        final CubeMeshData y = new CubeMeshData(new Vector3f(0, from.y(), 0), new Vector3f(width, to.y(), width));
        final CubeMeshData z = new CubeMeshData(new Vector3f(0, 0, from.z()), new Vector3f(width, width, to.z()));

        final AtomicInteger indicesOffset = new AtomicInteger(0);

        moveAndPut(x, -from.x(), 0, 0, verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(x, -from.x(), to.y(), 0, verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(x, -from.x(), 0, to.z(), verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(x, -from.x(), to.y(), to.z(), verticesBuffer, indicesBuffer, indicesOffset);

        moveAndPut(y, 0, -from.y(), 0, verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(y, to.x(), -from.y(), 0, verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(y, 0, -from.y(), to.z(), verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(y, to.x(), -from.y(), to.z(), verticesBuffer, indicesBuffer, indicesOffset);

        moveAndPut(z, 0, 0, -from.z(), verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(z, to.x(), 0, -from.z(), verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(z, 0, to.y(), -from.z(), verticesBuffer, indicesBuffer, indicesOffset);
        moveAndPut(z, to.x(), to.y(), -from.z(), verticesBuffer, indicesBuffer, indicesOffset);

        verticesBuffer.flip();
        indicesBuffer.flip();
    }

    private static void moveAndPut(CubeMeshData cubeMesh, float dX, float dY, float dZ, FloatBuffer verticesBuffer, IntBuffer indicesBuffer, AtomicInteger indicesOffset) {
        final float[] vertices = cubeMesh.getVertices();
        final int[] indices = cubeMesh.getIndices();

        for(int i = 0; i < vertices.length; i += 3)
            verticesBuffer.put(vertices[i] + dX).put(vertices[i + 1] + dY).put(vertices[i + 2] + dZ);

        for(int indice : indices)
            indicesBuffer.put(indice + indicesOffset.get());

        indicesOffset.addAndGet(cubeMesh.getVerticesCount());
    }
}
