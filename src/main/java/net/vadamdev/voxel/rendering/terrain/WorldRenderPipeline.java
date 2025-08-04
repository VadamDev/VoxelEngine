package net.vadamdev.voxel.rendering.terrain;

import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshUnion;
import net.vadamdev.voxel.rendering.terrain.shaders.SolidTerrainShader;
import net.vadamdev.voxel.rendering.terrain.shaders.WaterTerrainShader;
import net.vadamdev.voxel.rendering.terrain.texture.TerrainTextureAtlas;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * @author VadamDev
 * @since 04/07/2025
 */
public class WorldRenderPipeline {
    private final WorldRenderer worldRenderer;

    private final TerrainTextureAtlas textureAtlas;

    private final SolidTerrainShader solidShader;
    private final WaterTerrainShader waterShader;

    public WorldRenderPipeline(WorldRenderer worldRenderer, TerrainTextureAtlas textureAtlas, SolidTerrainShader solidShader, WaterTerrainShader waterShader) {
        this.worldRenderer = worldRenderer;
        this.textureAtlas = textureAtlas;

        this.solidShader = solidShader;
        this.waterShader = waterShader;
    }

    public void begin() {
        textureAtlas.bind();
    }

    public void renderChunks(Collection<ChunkMeshUnion> chunkMeshes, boolean faceCulling) {
        //Solid Mesh

        solidShader.bind();
        solidShader.engineData.set();
        solidShader.aoIntensity.set1f(worldRenderer.aoIntensity);

        solidShader.textureId.set1i(0);

        for(ChunkMeshUnion union : chunkMeshes) {
            final Renderable solidMesh = union.solidMesh();
            if(solidMesh == null)
                continue;

            if(faceCulling)
                GL11.glEnable(GL11.GL_CULL_FACE);

            solidShader.chunkPos.set3i(union.worldPosition());
            solidMesh.render();

            if(faceCulling)
                GL11.glDisable(GL11.GL_CULL_FACE);
        }

        solidShader.unbind();

        //Water Mesh
        waterShader.bind();
        waterShader.engineData.set();
        waterShader.waveEffect.set();

        waterShader.textureId.set1i(0);

        for(ChunkMeshUnion union : chunkMeshes) {
            final Renderable waterMesh = union.waterMesh();
            if(waterMesh == null)
                continue;

            waterShader.chunkPos.set3i(union.worldPosition());
            waterMesh.render();
        }

        waterShader.unbind();
    }

    public void renderDebugBorder(ChunkDebugBorder debugBorder, Collection<ChunkMeshUnion> chunkMeshes, int polygonMode) {
        solidShader.bind();
        solidShader.engineData.set();
        solidShader.textureId.set1i(0);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);

        for(ChunkMeshUnion union : chunkMeshes) {
            if(union.isEmpty())
                continue;

            solidShader.chunkPos.set3i(union.worldPosition());
            debugBorder.render();
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);

        solidShader.unbind();
    }

    public void end() {
        textureAtlas.unbind();
    }
}
