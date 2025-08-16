package net.vadamdev.voxel.world;

import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 04/06/2025
 */
public enum Direction {
    //ORDER MUST STAY LIKE THIS, ADDING NEW DIRECTIONS WILL LIKELY BREAK EVERYTHING
    NORTH (new Vector3i(1, 0,  0)),
    SOUTH (new Vector3i(-1, 0,  0)),
    EAST  (new Vector3i(0, 0,  1)),
    WEST  (new Vector3i(0, 0,  -1)),
    UP    (new Vector3i(0, 1,  0)),
    DOWN  (new Vector3i(0, -1, 0));

    private final Vector3i mod;
    private final int bitMask;

    Direction(Vector3i mod) {
        this.mod = mod;
        this.bitMask = 1 << ordinal();
    }

    public int modX() {
        return mod.x();
    }

    public int modY() {
        return mod.y();
    }

    public int modZ() {
        return mod.z();
    }

    public Vector3i newModVector() {
        return new Vector3i(mod);
    }

    public int bitMask() {
        return bitMask;
    }

    /*
       Static
     */

    private static final Direction[] readValues;
    public static final int ALL_DIRECTIONS_MASK;

    static {
        readValues = values();

        int mask = 0;
        for(Direction dir : readValues)
            mask |= dir.bitMask();

        ALL_DIRECTIONS_MASK = mask;
    }

    public static Direction[] readValues() {
        return readValues;
    }
}
