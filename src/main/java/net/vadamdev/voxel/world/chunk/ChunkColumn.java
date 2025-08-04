package net.vadamdev.voxel.world.chunk;

import net.vadamdev.voxel.engine.fastnoise.FastNoiseLite;
import net.vadamdev.voxel.world.blocks.Block;
import net.vadamdev.voxel.world.blocks.Blocks;
import org.joml.Random;
import org.joml.Vector3i;

import static net.vadamdev.voxel.world.chunk.Chunk.*;

/**
 * @author VadamDev
 * @since 17/06/2025
 */
public class ChunkColumn {
    private static final float amplitude = 96;
    private static final int caveHeight = 32;

    private static final float waterHeight = (amplitude + caveHeight) / 2f;
    private static final int maxWaterDepth = 6;

    private final int height;
    private final int worldX, worldZ;

    private final Chunk[] chunks;

    private final Random random = new Random();

    public ChunkColumn(int chunkX, int chunkZ, int height) {
        this.height = height;

        this.worldX = chunkX * CHUNK_WIDTH;
        this.worldZ = chunkZ * CHUNK_DEPTH;

        this.chunks = new Chunk[height];

        //Populate
        for(int i = 0; i < chunks.length; i++)
            chunks[i] = new Chunk(new Vector3i(chunkX, i, chunkZ));
    }

    public void generate(FastNoiseLite noise) {
        for(int x = 0; x < CHUNK_WIDTH; x++) {
            for(int z = 0; z < CHUNK_DEPTH; z++) {
                final float noisy = (noise.GetNoise(worldX + x, worldZ + z) + 1) / 2;

                int height = Math.clamp((int) Math.floor(noisy * amplitude), 0, this.height * CHUNK_HEIGHT);
                //height += caveHeight;

                if(height > waterHeight)
                    height -= height % 3;
                else {
                    final int depth = (int) (waterHeight - height);
                    if(depth > maxWaterDepth) {
                        float delta = depth - maxWaterDepth;
                        delta -= depth * (0.5f * 0.5f); //smooth water floor

                        height += Math.round(delta);
                    }
                }

                if(height > 0) {
                    Block topLayer = Blocks.GRASS;
                    if(height <= waterHeight)
                        topLayer = Blocks.SAND;

                    if(topLayer == Blocks.GRASS && random.nextInt(101) < 40)
                        setBlock(Blocks.BILLBOARD.blockId(), x, height + 1, z);

                    //Top layer
                    setBlock(topLayer.blockId(), x, height--, z);

                    //3 blocks dirt layer
                    for(int i = 0; i < 2 && height > 0; i++)
                        setBlock(topLayer == Blocks.GRASS ? Blocks.DIRT.blockId() : topLayer.blockId(), x, height--, z);

                    //Fill the rest with stone
                    while(height >= 1)
                        setBlock(Blocks.STONE.blockId(), x, height--, z);
                }

                //Water
                for(int y = 0; y < waterHeight; y++) {
                    if(getBlock(x, y ,z) != 0)
                        continue;

                    setBlock(Blocks.WATER.blockId(), x, y, z);
                }

                //Add bedrock
                setBlock(Blocks.BEDROCK.blockId(), x, 0, z);
            }
        }

        //Compress Result
        tryCompress();
    }

    private void tryCompress() {
        for(Chunk chunk : chunks)
            chunk.tryCompress();
    }

    private void setBlock(short blockId, int x, int y, int z) {
        findChunkAt(y).setBlock(blockId, x, y % CHUNK_HEIGHT, z);
    }

    private short getBlock(int x, int y, int z) {
        return findChunkAt(y).getBlock(x, y % CHUNK_HEIGHT, z);
    }

    private Chunk findChunkAt(int y) {
        //TODO: this is prone to errors, if y >= (height + CHUNK_HEIGH) it will still return the highest chunk in the column
        return chunks[Math.clamp(Math.floorDiv(y, CHUNK_HEIGHT), 0, chunks.length - 1)];
    }

    public Chunk[] getChunks() {
        return chunks;
    }
}
