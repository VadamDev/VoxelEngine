package net.vadamdev.voxel.rendering.terrain;

import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.rendering.mesh.InstancedMesh;
import net.vadamdev.voxel.rendering.terrain.debug.ChunkOutlineRenderer;
import net.vadamdev.voxel.rendering.terrain.highlight.BlockHighlightRenderer;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshUnion;
import net.vadamdev.voxel.rendering.terrain.shaders.ColoredWireframeShader;
import net.vadamdev.voxel.rendering.terrain.shaders.SolidTerrainShader;
import net.vadamdev.voxel.rendering.terrain.shaders.WaterTerrainShader;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

/**
 * @author VadamDev
 * @since 04/07/2025
 */
public class WorldRendererHelper {
    private final WorldRenderer worldRenderer;
    protected int polygonMode;

    public WorldRendererHelper(WorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
    }

    public void renderChunks(Collection<ChunkMeshUnion> chunkMeshes, boolean faceCulling) {
        worldRenderer.textureAtlas.bind();

        //Solid Mesh

        final SolidTerrainShader solidShader = worldRenderer.getSolidShader();
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
        final WaterTerrainShader waterShader = worldRenderer.getWaterShader();
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

        worldRenderer.textureAtlas.unbind();
    }

    public void renderBlockOver(BlockHighlightRenderer blockHighlight, Vector3i blockPos) {
        final Set<Vector3i> offsets = blockHighlight.highlightedOffsets();

        if(!offsets.isEmpty())
            offsets.clear();

        if(blockPos != null && (blockPos.x() != 0 && blockPos.y() != 0 && blockPos.z() != 0))
            offsets.add(blockPos);

        final ColoredWireframeShader shader = blockHighlight.shader();
        shader.bind();
        shader.engineData.set();
        shader.setWireframeColor(Color.WHITE.getRGB());

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glEnable(GL11.GL_CULL_FACE);

        final InstancedMesh mesh = blockHighlight.mesh();
        mesh.updateOffsets(offsets);
        mesh.render();

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);

        shader.unbind();
    }

    public void renderDebugBorder(ChunkOutlineRenderer chunkOutline, Set<Vector3i> chunkPositions) {
        final ColoredWireframeShader shader = chunkOutline.shader();
        shader.bind();
        shader.engineData.set();
        shader.setWireframeColor(0xFFFF00);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glEnable(GL11.GL_CULL_FACE);

        final InstancedMesh mesh = chunkOutline.mesh();
        mesh.updateOffsets(chunkPositions);
        mesh.render();

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);

        shader.unbind();
    }
}
