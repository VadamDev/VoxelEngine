package net.vadamdev.voxel.rendering.terrain.debug;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.rendering.mesh.InstancedMesh;
import net.vadamdev.voxel.rendering.mesh.helpers.Wireframes;
import net.vadamdev.voxel.rendering.terrain.shaders.ColoredWireframeShader;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author VadamDev
 * @since 19/08/2025
 */
public record ChunkOutlineRenderer(InstancedMesh mesh, ColoredWireframeShader shader) implements Disposable {
    public static ChunkOutlineRenderer create() throws ShaderException, IOException {
        final InstancedMesh mesh = createOutlineMesh();

        final ColoredWireframeShader shader = new ColoredWireframeShader();
        shader.create();

        return new ChunkOutlineRenderer(mesh, shader);
    }

    private static InstancedMesh createOutlineMesh() {
        InstancedMesh mesh = null;

        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
            verticesBuffer = MemoryUtil.memAllocFloat(288);
            indicesBuffer = MemoryUtil.memAllocInt(432);

            Wireframes.createWireframeBox(0.001f, new Vector3f(), new Vector3f(Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH), verticesBuffer, indicesBuffer);

            mesh = new InstancedMesh(GL15.GL_STREAM_DRAW);
            mesh.create(verticesBuffer, indicesBuffer);
        }catch(Exception e) {
            VoxelGame.get().getLogger().error("An error occurred while generating chunk outline mesh:", e);
        }finally {
            MemoryUtil.memFree(verticesBuffer);
            MemoryUtil.memFree(indicesBuffer);
        }

        return mesh;
    }

    @Override
    public void dispose() {
        mesh.dispose();
        shader.dispose();
    }
}
