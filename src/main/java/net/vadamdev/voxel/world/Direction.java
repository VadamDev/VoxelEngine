package net.vadamdev.voxel.world;

import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 04/06/2025
 */
public enum Direction {
    //ORDER MUST STAY LIKE THIS, ADDING NEW DIRECTIONS WILL LIKELY BREAK EVERYTHING
    NORTH (new Vector3i(1, 0,  0),  new Vector3f(1, 0.5f, 0.5f)),
    SOUTH (new Vector3i(-1, 0,  0), new Vector3f(0, 0.5f, 0.5f)),
    EAST  (new Vector3i(0, 0,  1),  new Vector3f(0.5f, 0.5f, 1)),
    WEST  (new Vector3i(0, 0,  -1), new Vector3f(0.5f, 0.5f, 0)),
    UP    (new Vector3i(0, 1,  0),  new Vector3f(0.5f, 1, 0.5f)),
    DOWN  (new Vector3i(0, -1, 0),  new Vector3f(0.5f, 0, 0.5f));

    private final Vector3i mod;
    private final Vector3f centerPos;

    private final int bitMask;

    Direction(Vector3i mod, Vector3f centerPos) {
        this.mod = mod;
        this.centerPos = centerPos;

        this.bitMask = 1 << ordinal();
    }

    /*
       Mod
     */

    public Vector3i mod() {
        return new Vector3i(mod);
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

    /*
       Center Pos
     */

    public Vector3f centerPos() {
        return new Vector3f(centerPos);
    }

    /*
       Bit mask
     */

    public int bitMask() {
        return bitMask;
    }

    /*
       Opposite
     */

    public Direction opposite() {
        return switch(this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case UP -> DOWN;
            case DOWN -> UP;
        };
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
