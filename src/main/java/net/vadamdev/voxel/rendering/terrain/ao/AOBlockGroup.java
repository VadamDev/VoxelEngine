package net.vadamdev.voxel.rendering.terrain.ao;

import net.vadamdev.voxel.world.AbstractWorld;
import net.vadamdev.voxel.world.blocks.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Funny dev note: This is probably the shitiest code I've ever wrote
 * somehow still take less time to compute than it takes to upload 32 bit to the gpu. *trollface*
 *
 * @author VadamDev
 * @since 22/07/2025
 */
public record AOBlockGroup(Face northFace, Face southFace, Face eastFace, Face westFace, Face upFace, Face downFace) {
    public static final AOBlockGroup.Face[] EMPTY = new AOBlockGroup.Face[] { Face.EMPTY, Face.EMPTY, Face.EMPTY, Face.EMPTY, Face.EMPTY, Face.EMPTY };

    public static Face[] collectFaces(AbstractWorld world, int x, int y, int z) {
        final Block self = world.getBlock(x, y, z);
        if(self != null && (self.isTransparent() || self.isWater()))
            return EMPTY;

        final Block northUp        = world.getBlock(x + 1, y + 1,     z    );
        final Block northUpRight   = world.getBlock(x + 1, y + 1,  z - 1);
        final Block northUpLeft    = world.getBlock(x + 1, y + 1,  z + 1);
        final Block northRight     = world.getBlock(x + 1,    y,      z - 1);
        final Block northDown      = world.getBlock(x + 1, y - 1,     z    );
        final Block northDownRight = world.getBlock(x + 1, y - 1,  z - 1);
        final Block northDownLeft  = world.getBlock(x + 1, y - 1,  z + 1);
        final Block northLeft      = world.getBlock(x + 1,    y,      z + 1);

        final Block southUp        = world.getBlock(x - 1, y + 1,    z    );
        final Block southUpRight   = world.getBlock(x - 1, y + 1, z + 1);
        final Block southUpLeft    = world.getBlock(x - 1, y + 1, z - 1);
        final Block southRight     = world.getBlock(x - 1,    y,     z + 1);
        final Block southDown      = world.getBlock(x - 1, y - 1,    z    );
        final Block southDownRight = world.getBlock(x - 1, y - 1, z + 1);
        final Block southDownLeft  = world.getBlock(x - 1, y - 1, z - 1);
        final Block southLeft      = world.getBlock(x - 1,    y,     z - 1);

        final Block eastUp         = world.getBlock(x, y + 1, z + 1);
        final Block eastUpRight    = northUpLeft;
        final Block eastUpLeft     = southUpRight;
        final Block eastRight      = northLeft;
        final Block eastDown       = world.getBlock(x, y - 1, z + 1);
        final Block eastDownRight  = northDownLeft;
        final Block eastDownLeft   = southDownRight;
        final Block eastLeft       = southRight;

        final Block westUp         = world.getBlock(x, y + 1, z - 1);
        final Block westUpRight    = southUpLeft;
        final Block westUpLeft     = northUpRight;
        final Block westRight      = southLeft;
        final Block westDown       = world.getBlock(x, y - 1, z - 1);
        final Block westDownRight  = southDownLeft;
        final Block westDownLeft   = northDownRight;
        final Block westLeft       = northRight;

        final Block upUp           = northUp;
        final Block upUpRight      = northUpLeft;
        final Block upUpLeft       = northUpRight;
        final Block upRight        = eastUp;
        final Block upDown         = southUp;
        final Block upDownRight    = southUpRight;
        final Block upDownLeft     = southUpLeft;
        final Block upLeft         = westUp;

        final Block downUp         = northDown;
        final Block downUpRight    = northDownRight;
        final Block downUpLeft     = northDownLeft;
        final Block downRight      = westDown;
        final Block downDown       = southDown;
        final Block downDownRight  = southDownLeft;
        final Block downDownLeft   = southDownRight;
        final Block downLeft       = eastDown;

        return new Face[] {
                Face.of(northUp, northDown, northRight, northLeft, northUpLeft, northUpRight, northDownLeft, northDownRight),
                Face.of(southUp, southDown, southRight, southLeft, southUpLeft, southUpRight, southDownLeft, southDownRight),
                Face.of(eastUp, eastDown, eastRight, eastLeft, eastUpLeft, eastUpRight, eastDownLeft, eastDownRight),
                Face.of(westUp, westDown, westRight, westLeft, westUpLeft, westUpRight, westDownLeft, westDownRight),
                Face.of(upUp, upDown, upRight, upLeft, upUpLeft, upUpRight, upDownLeft, upDownRight),
                Face.of(downUp, downDown, downRight, downLeft, downUpLeft, downUpRight, downDownLeft, downDownRight)
        };
    }

    public record Face(@Nullable Block north, @Nullable Block south, @Nullable Block east, @Nullable Block west, @Nullable Block northWest,
                       @Nullable Block northEast, @Nullable Block southWest, @Nullable Block southEast) {

        public static final Face EMPTY = new Face(null, null, null, null, null, null, null, null);

        public static Face of(@Nullable Block north, @Nullable Block south, @Nullable Block east, @Nullable Block west, @Nullable Block northWest,
                              @Nullable Block northEast, @Nullable Block southWest, @Nullable Block southEast) {

            if(north == null && south == null && east == null && west == null && northWest == null && northEast == null && southWest == null && southEast == null)
                return EMPTY;

            return new Face(north, south, east, west, northWest, northEast, southWest, southEast);
        }

        @Override
        public Block north() {
            return acquireFace(north);
        }

        @Override
        public Block south() {
            return acquireFace(south);
        }

        @Override
        public Block east() {
            return acquireFace(east);
        }

        @Override
        public Block west() {
            return acquireFace(west);
        }

        @Override
        public Block northWest() {
            return acquireFace(northWest);
        }

        @Override
        public Block northEast() {
            return acquireFace(northEast);
        }

        @Override
        public Block southWest() {
            return acquireFace(southWest);
        }

        @Override
        public Block southEast() {
            return acquireFace(southEast);
        }

        @Nullable
        private static Block acquireFace(@Nullable Block face) {
             if(face == null || (face.isTransparent() && !face.isWater()))
                 return null;

            return face;
        }

        /*
           0, 1 is Up
           2, 3 is Down
           0 --- 1
           |     |
           |     |
           2 --- 3
         */
        public byte[] calculateAO() {
            final byte[] result = new byte[4];
            if(isEmpty())
                return result;

            //0
            if(north() != null && west() != null)
                result[0] = 3;
            else if((north() != null || west() != null) && northWest() != null)
                result[0] = 2;
            else if(north() != null || northWest() != null || west() != null)
                result[0] = 1;

            //1
            if(north() != null && east() != null)
                result[1] = 3;
            else if((north() != null || east() != null) && northEast() != null)
                result[1] = 2;
            else if(north() != null || northEast() != null || east() != null)
                result[1] = 1;

            //2
            if(south() != null && west() != null)
                result[2] = 3;
            else if((south() != null || west() != null) && southWest() != null)
                result[2] = 2;
            else if(south() != null || southWest() != null || west() != null)
                result[2] = 1;

            //3
            if(south() != null && east() != null)
                result[3] = 3;
            else if((south() != null || east() != null) && southEast() != null)
                result[3] = 2;
            else if(south() != null || southEast() != null || east() != null)
                result[3] = 1;

            return result;
        }

        public boolean isEmpty() {
            return north == null && south == null && east == null && west == null && northWest == null && northEast == null &&  southWest == null && southEast == null;
        }
    }
}
