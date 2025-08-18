package net.vadamdev.voxel.world.raycast;

import net.vadamdev.voxel.engine.graphics.rendering.Camera;
import net.vadamdev.voxel.world.AbstractWorld;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * @author VadamDev
 * @since 16/08/2025
 */
public class Raycast {
    private final AbstractWorld world;

    private final float stepSize;
    private final double maxRange;

    public Raycast(AbstractWorld world, float stepSize, double maxRange) {
        this.world = world;

        this.stepSize = stepSize;
        this.maxRange = maxRange;
    }

    public Result cast(Vector3f position, Vector3f direction) {
        final Vector3d pos = new Vector3d(position);
        final Vector3f step =  new Vector3f(direction).normalize().mul(stepSize);

        Vector3d previousPos = pos;

        for(double length = 0; length < maxRange; length += stepSize) {
            final short blockId = world.getBlockId(pos.x(), pos.y(), pos.z());
            if(blockId != 0)
                return new Result(pos, previousPos, blockId, length);

            previousPos = new Vector3d(pos);
            pos.add(step);
        }

        return EMPTY;
    }

    public Result cast(Camera camera) {
        return cast(camera.position(), camera.getDirection());
    }

    public static final Result EMPTY = new Result(new Vector3d(), new Vector3d(), (short) 0, 0);
    public record Result(Vector3d pos, Vector3d previousPos, short blockId, double distance) {
        public boolean isEmpty() {
            return blockId == 0;
        }
    }
}
