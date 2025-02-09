package net.vadamdev.voxel.game.rendering;

import net.vadamdev.voxel.engine.graphics.rendering.MatrixDrawer;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;
import net.vadamdev.voxel.game.world.chunk.Chunk;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Collection;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class WorldRenderer {
    private final MatrixDrawer drawer;
    private final FrustumIntersection frustumIntersection;

    private final ChunkShader shader;

    public int renderMode = GL_FILL;
    public boolean faceCulling, frustumCulling;

    public WorldRenderer(MatrixDrawer drawer) throws IOException, ShaderException {
        this.drawer = drawer;
        this.frustumIntersection = drawer.getFrustumIntersection();

        shader = new ChunkShader();
        shader.create();
    }

    public void render(Collection<Chunk> chunks) {
        glPolygonMode(GL_FRONT_AND_BACK, renderMode);

        if(faceCulling) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }

        shader.bind();
        shader.setUniform4fv("projectionMatrix", drawer.getProjectionMatrix());
        shader.setUniform1i("texture_sampler", 0);

        glActiveTexture(GL_TEXTURE0);
        BlockTextureAtlas.get().bind();

        for(Chunk chunk : chunks) {
            final Vector3f chunkPos = chunk.position();

            if(frustumCulling && !frustumIntersection.testAab(chunkPos.x(), chunkPos.y(), chunkPos.z(), chunkPos.x() + Chunk.CHUNK_WIDTH, chunkPos.y() + Chunk.CHUNK_HEIGHT, chunkPos.z() + Chunk.CHUNK_DEPTH))
                continue;

            shader.setUniform4fv("modelViewMatrix", drawer.updateModelViewMatrix(chunkPos));
            chunk.render();
        }

        BlockTextureAtlas.get().unbind();

        shader.unbind();

        if(faceCulling)
            glDisable(GL_CULL_FACE);
    }

    public void destroy() {
        shader.destroy();
    }
}
