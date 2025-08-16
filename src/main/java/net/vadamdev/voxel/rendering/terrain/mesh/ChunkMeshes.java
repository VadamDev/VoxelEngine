package net.vadamdev.voxel.rendering.terrain.mesh;

import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

/**
 * @author VadamDev
 * @since 16/08/2025
 */
public final class ChunkMeshes {
    private ChunkMeshes() {}

    public static class Solid extends ChunkMeshBase<Solid.SolidData> {
        private int aobo;

        public Solid(SolidData meshData) {
            super(meshData);
        }

        @Override
        protected void genBuffers(SolidData meshData) {
            super.genBuffers(meshData);

            aobo = genBuffer(meshData.aoBuffer, 1);
        }

        @Override
        public void updateBuffers(SolidData newMeshData) {
            super.updateBuffers(newMeshData);

            updateBuffer(aobo, newMeshData.aoBuffer);
        }

        @Override
        public void dispose() {
            super.dispose();

            aobo = 0;
        }

        public static class SolidData extends Data {
            public final IntBuffer aoBuffer;

            public SolidData() {
                this.aoBuffer = MemoryUtil.memAllocInt(MAX_VERTEX_COUNT); //1 ambiant occlusion value per vertex
            }

            @Override
            public void flip() {
                super.flip();

                aoBuffer.flip();
            }

            @Override
            public void free() {
                super.free();

                if(aoBuffer != null)
                    MemoryUtil.memFree(aoBuffer);
            }
        }
    }

    public static final class Water extends ChunkMeshBase<ChunkMeshBase.Data> {
        public Water(Data meshData) {
            super(meshData);
        }
    }
}
