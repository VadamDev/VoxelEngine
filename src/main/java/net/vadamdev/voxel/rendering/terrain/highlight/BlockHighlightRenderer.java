package net.vadamdev.voxel.rendering.terrain.highlight;

import net.vadamdev.voxel.VoxelGame;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.rendering.mesh.InstancedMesh;
import net.vadamdev.voxel.rendering.mesh.helpers.Wireframes;
import net.vadamdev.voxel.rendering.terrain.shaders.ColoredWireframeShader;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VadamDev
 * @since 19/08/2025
 */
public record BlockHighlightRenderer(InstancedMesh mesh, ColoredWireframeShader shader, Set<Vector3i> highlightedOffsets) implements Disposable {
    private static final float WIDTH = 0.001f;

    public static BlockHighlightRenderer create() throws ShaderException, IOException {
        final InstancedMesh mesh = createOutlineMesh();

        final ColoredWireframeShader shader = new ColoredWireframeShader();
        shader.create();

        return new BlockHighlightRenderer(mesh, shader);
    }

    private static InstancedMesh createOutlineMesh() {
        final InstancedMesh mesh = new InstancedMesh(GL15.GL_STREAM_DRAW);

        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
            verticesBuffer = MemoryUtil.memAllocFloat(288);
            indicesBuffer = MemoryUtil.memAllocInt(432);

            Wireframes.createWireframeBox(WIDTH, new Vector3f(-WIDTH, -WIDTH, -WIDTH), new Vector3f(1 + WIDTH, 1 + WIDTH, 1 + WIDTH), verticesBuffer, indicesBuffer);

            mesh.create(verticesBuffer, indicesBuffer);
        }catch(Exception e) {
            VoxelGame.get().getLogger().error("An error occurred while generating block outline mesh:", e);
        }finally {
            MemoryUtil.memFree(verticesBuffer);
            MemoryUtil.memFree(indicesBuffer);
        }

        return mesh;
    }

    public BlockHighlightRenderer(InstancedMesh mesh, ColoredWireframeShader shader) {
        this(mesh, shader, new HashSet<>());
    }

    @Override
    public void dispose() {
        mesh.dispose();
        shader.dispose();
    }
}
