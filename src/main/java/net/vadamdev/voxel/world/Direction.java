package net.vadamdev.voxel.world;

import org.joml.Vector3i;

/**
 * @author VadamDev
 * @since 04/06/2025
 */
public enum Direction {
    NORTH ( 1, 0,  0),
    SOUTH (-1, 0,  0),
    EAST  ( 0, 0,  1),
    WEST  ( 0, 0,  -1),
    UP    ( 0, 1,  0),
    DOWN  ( 0, -1, 0);

    private final Vector3i mod;
    private final byte index;
    private final int bitMask;

    Direction(int modX, int modY, int modZ) {
        this.mod = new Vector3i(modX, modY, modZ);
        this.index = A.localIndex++;
        this.bitMask = 1 << index;
    }

    public Vector3i asModVector() {
        return new Vector3i(mod);
    }

    public byte index() {
        return this.index;
    }

    public int bitMask() {
        return bitMask;
    }

    //cannot use static value inside enum constructors, so fk retarded
    //TODO: get rid of this
    private static final class A {
        private A() {}

        private static byte localIndex = 0;
    }
}
