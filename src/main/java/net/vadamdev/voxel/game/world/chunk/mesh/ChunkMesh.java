package net.vadamdev.voxel.game.world.chunk.mesh;

import net.vadamdev.voxel.Launcher;
import net.vadamdev.voxel.engine.loop.ITaskHandler;
import net.vadamdev.voxel.engine.loop.RunContext;
import net.vadamdev.voxel.game.world.chunk.Chunk;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author VadamDev
 * @since 05/02/2025
 */
public class ChunkMesh {
    public static final int
            BITMASK_POS_X = 1,
            BITMASK_NEG_X = 1 << 1,
            BITMASK_POS_Y = 1 << 2,
            BITMASK_NEG_Y = 1 << 3,
            BITMASK_POS_Z = 1 << 4,
            BITMASK_NEG_Z = 1 << 5;

    public static final int BITMASK_ALL = BITMASK_POS_X | BITMASK_NEG_X | BITMASK_POS_Y | BITMASK_NEG_Y | BITMASK_POS_Z | BITMASK_NEG_Z;

    private int vao;
    private int vbo, tbo;

    private boolean isVAOBound;

    private ChunkMeshData meshData;
    public boolean isMeshBuilt;

    private volatile boolean destroyed;

    public void constructMeshAsync(Chunk chunk) {
        if(isMeshBuilt)
            return;

        isMeshBuilt = true;

        chunk.getWorld().getChunkMeshFactory().constructMeshAsync(chunk)
                .whenComplete((meshData, throwable) -> {
                    Launcher.game.runTask(RunContext.RENDER, () -> {
                        this.meshData = meshData;

                        try {
                            bindOrUpdateVBO();
                        }finally {
                            this.meshData.free();
                        }
                    });
                });
    }

    public void constructMeshSync(Chunk chunk) {
        try {
            meshData = chunk.getWorld().getChunkMeshFactory().constructMeshSync(chunk);
            isMeshBuilt = true;

            bindOrUpdateVBO();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            meshData.free();
        }
    }

    private void bindOrUpdateVBO() {
        if(isVAOBound) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, meshData.verticesBuffer(), GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, tbo);
            glBufferData(GL_ARRAY_BUFFER, meshData.textureCoordsBuffer(), GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }else {
            //Vao
            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            //Vertex Buffer Object
            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            glBufferData(GL_ARRAY_BUFFER, meshData.verticesBuffer(), GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //Texture Coords Buffer Object
            tbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, tbo);

            glBufferData(GL_ARRAY_BUFFER, meshData.textureCoordsBuffer(), GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            isVAOBound = true;
        }
    }

    public void render() {
        if(!isVAOBound)
            return;

        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLES, 0, meshData.verticesCount());

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void destroy() {
        if(!isVAOBound)
            return;

        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vbo);
        glDeleteBuffers(tbo);

        glBindVertexArray(0);
        glDeleteVertexArrays(vao);

        vao = 0;
        vbo = 0;

        isVAOBound = false;

        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
