package net.vadamdev.voxel.rendering.terrain;

import net.vadamdev.voxel.engine.graphics.rendering.MatrixDrawer;
import net.vadamdev.voxel.engine.graphics.rendering.Renderable;
import net.vadamdev.voxel.engine.graphics.shaders.exceptions.ShaderException;
import net.vadamdev.voxel.engine.graphics.texture.Texture;
import net.vadamdev.voxel.engine.utils.Disposable;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshUnion;
import net.vadamdev.voxel.rendering.terrain.shaders.SolidTerrainShader;
import net.vadamdev.voxel.rendering.terrain.shaders.WaterTerrainShader;
import net.vadamdev.voxel.rendering.terrain.texture.TerrainTextureAtlas;
import net.vadamdev.voxel.world.chunk.Chunk;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author VadamDev
 * @since 29/06/2025
 */
public class WorldRenderer implements Renderable, Disposable {
    private final Map<Vector3i, ChunkMeshUnion> chunkMeshes;
    private final ChunkMeshFactory meshFactory;
    private final MatrixDrawer matrixDrawer;

    private final TerrainTextureAtlas textureAtlas;
    private ChunkDebugBorder debugBorder;

    private final SolidTerrainShader solidShader;
    private final WaterTerrainShader waterShader;

    private final WorldRenderPipeline pipeline;

    //Public Parameters
    public boolean renderChunkBorders = false;
    public boolean faceCulling = true, frustumCulling = true;
    public int polygonMode = GL11.GL_FILL;

    public float aoIntensity = 0.5f;

    public WorldRenderer(ChunkMeshFactory meshFactory, MatrixDrawer matrixDrawer) throws URISyntaxException, IOException, ShaderException {
        this.chunkMeshes = new ConcurrentHashMap<>();
        this.meshFactory = meshFactory;
        this.matrixDrawer = matrixDrawer;

        this.textureAtlas = new TerrainTextureAtlas();
        this.textureAtlas.create();

        this.solidShader = new SolidTerrainShader();
        this.solidShader.create();

        this.waterShader = new WaterTerrainShader();
        this.waterShader.create();

        this.pipeline = new WorldRenderPipeline(this, textureAtlas, solidShader, waterShader);

        GL11.glCullFace(GL11.GL_BACK);
    }

    public void postInit() {
        this.debugBorder = new ChunkDebugBorder();
        this.debugBorder.create();
    }

    @Override
    public void render() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);
        pipeline.begin();

        //Chunks
        final Collection<ChunkMeshUnion> chunkMeshes = computeVisibleChunks();
        pipeline.renderChunks(chunkMeshes, faceCulling);

        //Chunk debug border
        if(renderChunkBorders)
            pipeline.renderDebugBorder(debugBorder, chunkMeshes, polygonMode);

        pipeline.end();
    }

    private Collection<ChunkMeshUnion> computeVisibleChunks() {
        if(!frustumCulling)
            return chunkMeshes.values();

        return chunkMeshes.values().parallelStream()
                .filter(union -> {
                    final Vector3f chunkStart = new Vector3f(union.worldPosition());
                    final Vector3f chunkEnd = new Vector3f(union.worldPosition()).add(Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH);

                    return matrixDrawer.frustumIntersection().testAab(chunkStart, chunkEnd);
                }).toList();
    }

    @Override
    public void dispose() {
        debugBorder.dispose();
        solidShader.dispose();
        waterShader.dispose();

        Optional.ofNullable(textureAtlas.getAtlas()).ifPresent(Texture::dispose);
    }

    public synchronized void addChunk(Chunk chunk) {
        final Vector3i chunkPos = chunk.position();
        if(chunkMeshes.containsKey(chunkPos) || meshFactory.isMeshing(chunkPos))
            return;

        final ChunkMeshUnion union = new ChunkMeshUnion(chunkPos);
        chunkMeshes.put(chunkPos, union);
        union.constructMeshAsync(chunk);
    }

    public synchronized void updateChunk(Chunk chunk) {
        final ChunkMeshUnion union = chunkMeshes.get(chunk.position());
        if(union == null)
            return;

        union.constructMeshAsync(chunk);
    }

    public synchronized void removeChunk(Vector3i chunkPos) {
        final ChunkMeshUnion union = chunkMeshes.get(chunkPos);
        if(union == null)
            return;

        if(meshFactory.isMeshing(chunkPos))
            meshFactory.cancelMeshingTask(chunkPos);

        chunkMeshes.remove(chunkPos);
        union.dispose();
    }

    public TerrainTextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public SolidTerrainShader getSolidShader() {
        return solidShader;
    }

    public WaterTerrainShader getWaterShader() {
        return waterShader;
    }

    public ChunkCounter getRenderedMeshesCount() {
        return chunkMeshes.values().parallelStream()
                .collect(ChunkCounter::new, (counter, union) -> {
                    final boolean hasSolidMesh = union.solidMesh() != null;
                    final boolean hasWaterMesh = union.waterMesh() != null;

                    if(!hasSolidMesh && !hasWaterMesh)
                        counter.emptyCount++;
                    else {
                        if(hasSolidMesh)
                            counter.solidMeshCount++;

                        if(hasWaterMesh)
                            counter.waterMeshCount++;
                    }
                }, ChunkCounter::add);
    }

    public static final class ChunkCounter {
        private ChunkCounter() {}

        private long solidMeshCount, waterMeshCount;
        private long emptyCount;

        private void add(ChunkCounter b) {
            solidMeshCount += b.solidMeshCount;
            waterMeshCount += b.waterMeshCount;
            emptyCount += b.emptyCount;
        }

        public long solidMeshCount() {
            return solidMeshCount;
        }

        public long waterMeshCount() {
            return waterMeshCount;
        }

        public long emptyCount() {
            return emptyCount;
        }

        public long totalRendered() {
            return solidMeshCount + waterMeshCount;
        }

        public long total() {
            return totalRendered() + emptyCount;
        }
    }
}
