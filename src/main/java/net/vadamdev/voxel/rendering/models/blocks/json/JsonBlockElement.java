package net.vadamdev.voxel.rendering.models.blocks.json;

import net.vadamdev.voxel.engine.json.PreemJsonArray;
import net.vadamdev.voxel.engine.json.PreemJsonObj;
import net.vadamdev.voxel.rendering.models.blocks.VoxelFactory;
import net.vadamdev.voxel.rendering.models.blocks.VoxelTextureRef;
import net.vadamdev.voxel.rendering.terrain.texture.FaceUVs;
import net.vadamdev.voxel.world.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;

/**
 * @author VadamDev
 * @since 02/08/2025
 */
public class JsonBlockElement {
    private final Map<String, String> textures;

    private final Vector3i from, to;
    @Nullable private Matrix4f rotation;
    private int culledFaces;

    private final VoxelTextureRef[] textureRefs;

    public JsonBlockElement(PreemJsonObj elementJson, Map<String, String> textures) {
        this.textures = textures;

        this.from = elementJson.getJsonArray("from").acquire(arr -> new Vector3i(arr.toIntArray()));
        this.to = elementJson.getJsonArray("to").acquire(arr -> new Vector3i(arr.toIntArray()));

        this.rotation = null;
        this.culledFaces = 0;

        this.textureRefs = new VoxelTextureRef[6];

        loadRotation(elementJson.getJsonObject("rotation"));
        loadFaces(elementJson.getJsonObject("faces"));
    }

    private void loadRotation(@Nullable PreemJsonObj rotationJson) {
        if(rotationJson == null)
            return;

        final float angle = Math.toRadians(rotationJson.getFloat("angle"));
        final String axis = rotationJson.getString("axis");
        final Vector3f origin = rotationJson.getJsonArray("origin").acquire(arr -> new Vector3f(new Vector3i(arr.toIntArray())).div(VoxelFactory.VOXEL_PRECISION));

        if(angle == 0 || axis == null)
            return;

        final Matrix4f rotationMatrix = new Matrix4f().identity()
                .translate(origin.x(), origin.y(), origin.z());

        switch(axis) {
            case "x" -> rotationMatrix.rotateX(angle);
            case "y" -> rotationMatrix.rotateY(angle);
            case "z" -> rotationMatrix.rotateZ(angle);
            default -> throw new IllegalArgumentException("Found invalid axis in model rotation: " + axis);
        }

        rotationMatrix.translate(-origin.x(), -origin.y(), -origin.z());
        rotation = rotationMatrix;
    }

    private void loadFaces(@Nullable PreemJsonObj facesJson) {
        if(facesJson == null)
            return;

        final Direction[] directions = Direction.readValues();
        for(int i = 0; i < directions.length; i++) {
            final Direction dir = directions[i];

            final PreemJsonObj faceJson = facesJson.getJsonObject(dir.name().toLowerCase());
            if(faceJson == null)
                continue;

            //Texture
            final PreemJsonArray uv = faceJson.getJsonArray("uv");
            final String textureKey = faceJson.getString("texture");

            if(uv != null && textureKey != null) {
                final String textureName;
                if(textureKey.startsWith("#"))
                    textureName = textures.get(textureKey.replace("#", ""));
                else
                    textureName = textureKey;

                if(textureName != null)
                    textureRefs[i] = new VoxelTextureRef(textureName, uv.toIntArray());
            }

            //Culling
            if(faceJson.getBoolean("culled"))
                culledFaces |= dir.bitMask();
        }
    }

    private FaceUVs[] bakeUvs() {
        final FaceUVs[] uvs = FaceUVs.newVoxelUVs(false);
        for(int i = 0; i < Direction.readValues().length; i++) {
            final VoxelTextureRef textureRef = textureRefs[i];

            if(textureRef != null)
                uvs[i] = textureRef.toFaceUv(VoxelFactory.VOXEL_PRECISION);
            else
                uvs[i] = null;
        }

        return uvs;
    }

    public VoxelFactory toVoxelFactory() {
        return new VoxelFactory(from, to, rotation, culledFaces, bakeUvs());
    }
}
