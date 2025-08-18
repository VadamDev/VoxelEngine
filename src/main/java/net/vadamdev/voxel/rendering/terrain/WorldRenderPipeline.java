package net.vadamdev.voxel.rendering.terrain;

import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.rendering.terrain.debug.ChunkOutlineMesh;
import net.vadamdev.voxel.rendering.terrain.debug.ChunkOutlineShader;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshUnion;
import net.vadamdev.voxel.rendering.terrain.shaders.SolidTerrainShader;
import net.vadamdev.voxel.rendering.terrain.shaders.WaterTerrainShader;
import net.vadamdev.voxel.rendering.terrain.texture.TerrainTextureAtlas;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author VadamDev
 * @since 04/07/2025
 */
public class WorldRenderPipeline {
    private final WorldRenderer worldRenderer;

    private final TerrainTextureAtlas textureAtlas;

    private final SolidTerrainShader solidShader;
    private final WaterTerrainShader waterShader;
    private final ChunkOutlineShader chunkOutlineShader;

    public WorldRenderPipeline(WorldRenderer worldRenderer, TerrainTextureAtlas textureAtlas, SolidTerrainShader solidShader, WaterTerrainShader waterShader, ChunkOutlineShader chunkOutlineShader) {
        this.worldRenderer = worldRenderer;
        this.textureAtlas = textureAtlas;

        this.solidShader = solidShader;
        this.waterShader = waterShader;
        this.chunkOutlineShader = chunkOutlineShader;
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

        if(faceCulling)
            GL11.glEnable(GL11.GL_CULL_FACE);

        for(ChunkMeshUnion union : chunkMeshes) {
            final Renderable solidMesh = union.solidMesh();
            if(solidMesh == null)
                continue;

            solidShader.chunkPos.set3i(union.worldPosition());
            solidMesh.render();
        }

        if(faceCulling)
            GL11.glDisable(GL11.GL_CULL_FACE);

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

    public void renderDebugBorder(ChunkOutlineMesh debugBorder, Collection<ChunkMeshUnion> chunkMeshes, int polygonMode) {
        chunkOutlineShader.bind();
        chunkOutlineShader.engineData.set();

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glEnable(GL11.GL_CULL_FACE);

        debugBorder.updatePostions(chunkMeshes.stream().map(ChunkMeshUnion::worldPosition).collect(Collectors.toSet()));
        debugBorder.render();

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);

        chunkOutlineShader.unbind();
    }

    public void end() {
        textureAtlas.unbind();
    }
}
