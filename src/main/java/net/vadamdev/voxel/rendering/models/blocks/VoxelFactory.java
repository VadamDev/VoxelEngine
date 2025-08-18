package net.vadamdev.voxel.rendering.models.blocks;

import net.vadamdev.voxel.rendering.terrain.ao.AOBlockGroup;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshBase;
import net.vadamdev.voxel.rendering.terrain.mesh.ChunkMeshes;
import net.vadamdev.voxel.rendering.terrain.texture.FaceUVs;
import net.vadamdev.voxel.world.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 04/06/2025
 */
public class VoxelFactory {
    public static final float VOXEL_PRECISION = 16; //Defines voxel size precision 0 -> 16 inside the block

    private static final int[][] AO_INDEXES = new int[][] {
            { 2, 1, 0, 2, 3, 1 }, // +X
            { 2, 3, 1, 2, 1, 0 }, // -X
            { 2, 3, 1, 2, 1, 0 }, // +Y
            { 1, 2, 3, 1, 0, 2 }, // -Y
            { 3, 1, 2, 0, 2, 1 }, // +Z
            { 3, 1, 2, 0, 2, 1 }, // -Z
    };

    private final FaceUVs[] uvs;
    @Nullable private final Matrix4f rotation;
    private final int culledFaces;

    private float[] posXPlane, negXPlane, posYPlane, negYPlane, posZPlane, negZPlane;
    private float[] posXUv, negXUv, posYUv, negYUv, posZUv, negZUv;

    public VoxelFactory(Vector3i from, Vector3i to, @Nullable Matrix4f rotation, int culledFaces, FaceUVs[] uvs) {
        this.rotation = rotation;
        this.culledFaces = culledFaces;
        this.uvs = uvs;

        bakePlanes(from, to);
    }

    private void bakePlanes(Vector3i from, Vector3i to) {
        final float startX = from.x() / VOXEL_PRECISION;
        final float startY = from.y() / VOXEL_PRECISION;
        final float startZ = from.z() / VOXEL_PRECISION;

        final float endX = to.x() / VOXEL_PRECISION;
        final float endY = to.y() / VOXEL_PRECISION;
        final float endZ = to.z() / VOXEL_PRECISION;

        if(uvs[0] != null) {
            posXPlane = new float[] {
                    endX, startY, endZ,
                    endX, endY,   startZ,
                    endX, endY,   endZ,

                    endX, startY, endZ,
                    endX, startY, startZ,
                    endX, endY,   startZ,
            };
            rotatePlane(posXPlane);

            final FaceUVs uv = uvs[0];
            posXUv = new float[] {
                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v0(),
                    uv.u0(), uv.v0(),

                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v1(),
                    uv.u1(), uv.v0()
            };
        }

        if(uvs[1] != null) {
            negXPlane = new float[] {
                    startX, startY, startZ,
                    startX, startY, endZ,
                    startX, endY,   endZ,

                    startX, startY, startZ,
                    startX, endY,   endZ,
                    startX, endY,   startZ
            };
            rotatePlane(negXPlane);

            final FaceUVs uv = uvs[1];
            negXUv = new float[] {
                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v1(),
                    uv.u1(), uv.v0(),

                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v0(),
                    uv.u0(), uv.v0()
            };
        }

        if(uvs[4] != null) {
            posYPlane = new float[] {
                    startX, endY, startZ,
                    startX, endY, endZ,
                    endX,   endY, endZ,

                    startX, endY, startZ,
                    endX,   endY, endZ,
                    endX,   endY, startZ
            };
            rotatePlane(posYPlane);

            final FaceUVs uv = uvs[4];
            posYUv = new float[] {
                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v1(),
                    uv.u1(), uv.v0(),

                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v0(),
                    uv.u0(), uv.v0()
            };
        }

        if(uvs[5] != null) {
            negYPlane = new float[] {
                    endX,   startY, startZ,
                    startX, startY, endZ,
                    startX, startY, startZ,

                    endX,   startY, startZ,
                    endX,   startY, endZ,
                    startX, startY, endZ
            };
            rotatePlane(negYPlane);

            final FaceUVs uv = uvs[5];
            negYUv = new float[] {
                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v0(),
                    uv.u0(), uv.v0(),

                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v1(),
                    uv.u1(), uv.v0()
            };
        }

        if(uvs[2] != null) {
            posZPlane = new float[] {
                    endX,   startY, endZ,
                    endX,   endY,   endZ,
                    startX, startY, endZ,

                    startX, endY,   endZ,
                    startX, startY, endZ,
                    endX,   endY,   endZ
            };
            rotatePlane(posZPlane);

            final FaceUVs uv = uvs[2];
            posZUv = new float[] {
                    uv.u1(), uv.v1(),
                    uv.u1(), uv.v0(),
                    uv.u0(), uv.v1(),

                    uv.u0(), uv.v0(),
                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v0()
            };
        }

        if(uvs[3] != null) {
            negZPlane = new float[] {
                    startX, startY, startZ,
                    startX, endY,   startZ,
                    endX,   startY, startZ,

                    endX,   endY,   startZ,
                    endX,   startY, startZ,
                    startX, endY,   startZ
            };
            rotatePlane(negZPlane);

            final FaceUVs uv = uvs[3];
            negZUv = new float[] {
                    uv.u1(), uv.v1(),
                    uv.u1(), uv.v0(),
                    uv.u0(), uv.v1(),

                    uv.u0(), uv.v0(),
                    uv.u0(), uv.v1(),
                    uv.u1(), uv.v0()
            };
        }
    }

    public void bakeVerticesToWorld(ChunkMeshBase.Data meshData, int dX, int dY, int dZ, int adjacentBlocks, AOBlockGroup.Face[] aoGroup) {
        if(isFacePresent(posXPlane) && (!isDirCulled(Direction.NORTH) || (adjacentBlocks & Direction.NORTH.bitMask()) == 0))
            addFace(posXPlane, posXUv, AO_INDEXES[0], dX, dY, dZ, meshData, calculateAO(Direction.NORTH, aoGroup));

        if(isFacePresent(negXPlane) && (!isDirCulled(Direction.SOUTH) || (adjacentBlocks & Direction.SOUTH.bitMask()) == 0))
            addFace(negXPlane, negXUv, AO_INDEXES[1], dX, dY, dZ, meshData, calculateAO(Direction.SOUTH, aoGroup));

        if(isFacePresent(posYPlane) && (!isDirCulled(Direction.UP) || (adjacentBlocks & Direction.UP.bitMask()) == 0))
            addFace(posYPlane, posYUv, AO_INDEXES[2], dX, dY, dZ, meshData, calculateAO(Direction.UP, aoGroup));

        if(isFacePresent(negYPlane) && (!isDirCulled(Direction.DOWN) || (adjacentBlocks & Direction.DOWN.bitMask()) == 0))
            addFace(negYPlane, negYUv, AO_INDEXES[3], dX, dY, dZ, meshData, calculateAO(Direction.DOWN, aoGroup));

        if(isFacePresent(posZPlane) && (!isDirCulled(Direction.EAST) || (adjacentBlocks & Direction.EAST.bitMask()) == 0))
            addFace(posZPlane, posZUv, AO_INDEXES[4], dX, dY, dZ, meshData, calculateAO(Direction.EAST, aoGroup));

        if(isFacePresent(negZPlane) && (!isDirCulled(Direction.WEST) || (adjacentBlocks & Direction.WEST.bitMask()) == 0))
            addFace(negZPlane, negZUv, AO_INDEXES[5], dX, dY, dZ, meshData, calculateAO(Direction.WEST, aoGroup));
    }

    private void addFace(float[] plane, float[] uvs, int[] aoIndex, float dX, float dY, float dZ, ChunkMeshBase.Data meshData, byte[] ao) {
        for(int i = 0; i < plane.length; i += 3) {
            meshData.verticesBuffer.put(plane[i] + dX);
            meshData.verticesBuffer.put(plane[i + 1] + dY);
            meshData.verticesBuffer.put(plane[i + 2] + dZ);

            if(meshData instanceof ChunkMeshes.Solid.SolidData solidData)
                solidData.aoBuffer.put(ao[aoIndex[i / 3]]);
        }

        if(uvs != null && uvs.length > 0)
            meshData.textureCoordsBuffer.put(uvs);
    }

    private boolean isFacePresent(float[] face) {
        return face != null && face.length > 0;
    }

    private boolean isDirCulled(Direction direction) {
        return (culledFaces & direction.bitMask()) != 0;
    }

    private byte[] calculateAO(Direction direction, AOBlockGroup.Face[] aoGroup) {
        if(aoGroup[direction.ordinal()] == null)
            return new byte[4];

        return aoGroup[direction.ordinal()].calculateAO();
    }

    private void rotatePlane(float[] plane) {
        if(rotation == null)
            return;

        for(int i = 0; i < plane.length; i += 3) {
            final Vector3f vertex = new Vector3f(plane[i], plane[i + 1], plane[i + 2]);
            rotation.transformPosition(vertex);

            plane[i] = vertex.x();
            plane[i + 1] = vertex.y();
            plane[i + 2] = vertex.z();
        }
    }
}
