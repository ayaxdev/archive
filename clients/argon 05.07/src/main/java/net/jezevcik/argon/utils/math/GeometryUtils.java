package net.jezevcik.argon.utils.math;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * A set of methods used for helping with geometry.
 */
public class GeometryUtils {

    /**
     * Checks whether the provided X and Y coordinates are in the provided bounds.
     *
     * @param x The X coordinate to be checked.
     * @param y The Y coordinate to be checked.
     * @param left The upper-left X coordinate of the bounds.
     * @param up The upper-left Y coordinate of the bounds.
     * @param right The bottom-right X coordinate of the bounds.
     * @param down The bottom-right Y coordinate of the bounds.
     * @param relative Whether the right and down points are relative to the left and up points.
     * @return Whether the X and Y coordinates are in the provided bounds.
     */
    public static boolean isInBounds(final double x, final double y, final double left, final double up, final double right, final double down, final boolean relative) {
        if (relative)
            return x >= left && x <= left + right && y >= up && y <= up + down;
        else
            return x >= left && x <= right && y >= up && y <= down;
    }

    /**
     * Returns the closest point in a box to a vector
     *
     * @param start The vector to be closest to the box
     * @param box The box in which the method is looking for
     * @return The closest point in a box to a vector
     */
    public static Vec3d closestPointInBox(Vec3d start, Box box) {
        double x = Math.max(box.minX, Math.min(box.maxX, start.x));
        double y = Math.max(box.minY, Math.min(box.maxY, start.y));
        double z = Math.max(box.minZ, Math.min(box.maxZ, start.z));
        return new Vec3d(x, y, z);
    }

}
